package com.yasirceltik.promptvault.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;

	public Optional<User> login(LoginRequestDto request) {
		return userRepository.findByEmail(request.email())
				.filter(u -> u.getPassword().equals(request.password()))
				.filter(User::isActive);
	}

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
				.password(request.password())
				.role(UserRole.USER)
				.active(true)
				.build();

		userRepository.save(user);
		return true;
	}
}
