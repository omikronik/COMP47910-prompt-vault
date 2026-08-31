package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.UserRepository;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        // Use a real BCrypt encoder here.
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(
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

        Optional<User> result = authService.login(request);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
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

        Optional<User> result = authService.login(request);

        assertTrue(result.isEmpty());
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

        Optional<User> result = authService.login(request);

        assertTrue(result.isEmpty());
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
