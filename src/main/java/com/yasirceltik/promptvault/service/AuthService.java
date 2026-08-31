package com.yasirceltik.promptvault.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.LoginResultDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private static final int MAX_LOGIN_ATTEMPTS = 3;
	private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

	private final Clock clock;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public boolean register(RegisterRequestDto request) {
		if (userRepository.findByEmail(request.email()).isPresent()) {
			return false;
		}
		if (userRepository.findByUsername(request.username()).isPresent()) {
			return false;
		}

		User user = User.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.username(request.username())
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.role(UserRole.USER)
				.active(true)
				.build();

		userRepository.save(user);
		return true;
	}

	@Transactional
	public LoginResultDto login(
			LoginRequestDto request) {

		Optional<User> optionalUser =
				userRepository.findByEmail(
					request.email()
				);

		if (optionalUser.isEmpty()) {
			return LoginResultDto.invalidCredentials();
		}

		User user = optionalUser.get();

		LocalDateTime now = LocalDateTime.now(clock);

		/*
		 * Account is currently locked.
		 */
		if (user.getLockedUntil() != null
				&& now.isBefore(
					user.getLockedUntil()
					)) {

			return LoginResultDto.locked(user.getLockedUntil());
		}

		/*
		 * A previous lock has expired.
		 */
		if (user.getLockedUntil() != null) {
			user.setLockedUntil(null);
			user.setLoginAttempts(0);
		}

		if (!user.isActive()) {
			return LoginResultDto.invalidCredentials();
		}

		/*
		 * Password is wrong.
		 */
		if (!passwordEncoder.matches(
					request.password(),
					user.getPassword()
					)) {

			int attempts = user.getLoginAttempts() + 1;

			user.setLoginAttempts(attempts);

			if (attempts >= MAX_LOGIN_ATTEMPTS) {
				LocalDateTime lockedUntil =
					now.plus(LOCK_DURATION);

				user.setLockedUntil(
						lockedUntil
						);

				log.warn(
						"User id={} temporarily locked until {}",
						user.getId(),
						lockedUntil
						);

				return LoginResultDto.locked(
						lockedUntil
						);
			}

			log.info(
					"Failed login attempt {} for user id={}",
					attempts,
					user.getId()
					);

			return LoginResultDto.invalidCredentials();
		}

		/*
		 * Successful authentication.
		 */
		user.setLoginAttempts(0);
		user.setLockedUntil(null);

		return LoginResultDto.success(user);
	}

}
