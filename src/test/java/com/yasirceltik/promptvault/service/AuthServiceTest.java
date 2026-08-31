package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.LoginResultDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.model.LoginStatus;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.UserRepository;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private Clock clock;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        // Use a real BCrypt encoder here.
        passwordEncoder = new BCryptPasswordEncoder();
        clock = Clock.fixed(
                Instant.parse("2026-08-31T06:00:00Z"),
                ZoneOffset.UTC
        );

        authService = new AuthService(
                clock,
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void registerStoresHashedPassword() {
        RegisterRequestDto request = new RegisterRequestDto(
                "John",
                "Smith",
                "johnsmith",
                "john@example.com",
                "Test1234"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = authService.register(request);

        assertTrue(result);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertNotEquals(
                request.password(),
                savedUser.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        request.password(),
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void loginSucceedsWhenPasswordMatchesHash() {
        String rawPassword = "Test1234";

        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .username("johnsmith")
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.USER)
                .active(true)
                .build();

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        LoginRequestDto request =
                new LoginRequestDto(
                        "john@example.com",
                        rawPassword
                );

        LoginResultDto result = authService.login(request);

        assertEquals(LoginStatus.SUCCESS, result.status());
        assertEquals(user, result.user());
        assertNull(result.lockedUntil());
    }

    @Test
    void loginFailsWhenPasswordIsIncorrect() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .username("johnsmith")
                .password(passwordEncoder.encode("CorrectPassword"))
                .role(UserRole.USER)
                .active(true)
                .build();

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        LoginRequestDto request =
                new LoginRequestDto(
                        "john@example.com",
                        "WrongPassword"
                );

        LoginResultDto result = authService.login(request);

        assertEquals(LoginStatus.INVALID_CREDENTIALS, result.status());
        assertNull(result.user());
        assertEquals(1, user.getLoginAttempts());
    }

    @Test
    void loginFailsForInactiveUserEvenWithCorrectPassword() {
        String rawPassword = "Test1234";

        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .username("johnsmith")
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.USER)
                .active(false)
                .build();

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        LoginRequestDto request =
                new LoginRequestDto(
                        "john@example.com",
                        rawPassword
                );

        LoginResultDto result = authService.login(request);

        assertEquals(LoginStatus.INVALID_CREDENTIALS, result.status());
        assertEquals(0, user.getLoginAttempts());
    }

    @Test
    void thirdFailedLoginLocksAccountForFifteenMinutes() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .password(passwordEncoder.encode("CorrectPassword"))
                .role(UserRole.USER)
                .active(true)
                .loginAttempts(2)
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        LoginResultDto result = authService.login(
                new LoginRequestDto(user.getEmail(), "WrongPassword")
        );

        LocalDateTime expectedUnlockTime =
                LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
                        .plusMinutes(15);

        assertEquals(LoginStatus.LOCKED, result.status());
        assertEquals(expectedUnlockTime, result.lockedUntil());
        assertEquals(expectedUnlockTime, user.getLockedUntil());
        assertEquals(3, user.getLoginAttempts());
    }

    @Test
    void loginDuringLockRemainsLocked() {
        LocalDateTime lockedUntil =
                LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
                        .plusMinutes(5);

        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .password(passwordEncoder.encode("CorrectPassword"))
                .role(UserRole.USER)
                .active(true)
                .loginAttempts(3)
                .lockedUntil(lockedUntil)
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        LoginResultDto result = authService.login(
                new LoginRequestDto(user.getEmail(), "CorrectPassword")
        );

        assertEquals(LoginStatus.LOCKED, result.status());
        assertEquals(lockedUntil, result.lockedUntil());
        assertEquals(3, user.getLoginAttempts());
    }

    @Test
    void successfulLoginAfterExpiredLockClearsLockState() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .password(passwordEncoder.encode("CorrectPassword"))
                .role(UserRole.USER)
                .active(true)
                .loginAttempts(3)
                .lockedUntil(
                        LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
                                .minusSeconds(1)
                )
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        LoginResultDto result = authService.login(
                new LoginRequestDto(user.getEmail(), "CorrectPassword")
        );

        assertEquals(LoginStatus.SUCCESS, result.status());
        assertEquals(user, result.user());
        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getLockedUntil());
    }

    @Test
    void unknownEmailReturnsInvalidCredentials() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        LoginResultDto result = authService.login(
                new LoginRequestDto("missing@example.com", "Password!123")
        );

        assertEquals(LoginStatus.INVALID_CREDENTIALS, result.status());
        assertNull(result.user());
        assertNull(result.lockedUntil());
    }

    @Test
    void registerDoesNotSaveUserWhenEmailAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto(
                "John",
                "Smith",
                "johnsmith",
                "john@example.com",
                "Test1234"
        );

        User existingUser = User.builder()
                .email("john@example.com")
                .build();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(existingUser));

        boolean result = authService.register(request);

        assertFalse(result);

        verify(userRepository, never()).save(any());
    }

    @Test
    void equalPasswordsProduceDifferentBcryptHashes() {
        String password = "Test1234";

        String firstHash = passwordEncoder.encode(password);
        String secondHash = passwordEncoder.encode(password);

        assertNotEquals(firstHash, secondHash);

        assertTrue(passwordEncoder.matches(password, firstHash));
        assertTrue(passwordEncoder.matches(password, secondHash));
    }
}
