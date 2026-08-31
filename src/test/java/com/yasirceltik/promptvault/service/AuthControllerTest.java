package com.yasirceltik.promptvault.service;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Inject
    private AuthService authService;

    @Inject
    private UserRepository userRepository;

    @Test
    void registrationDoesNotStorePlaintextPassword() {
        var request = new RegisterRequestDto(
            "Test",
            "User",
            "testuser",
            "test@example.com",
            "TestPassword123!"
        );

        authService.register(request);

        User user = userRepository
                .findByEmail("test@example.com")
                .orElseThrow();

        assertNotEquals(
            "TestPassword123!",
            user.getPassword()
        );

        assertTrue(user.getPassword().startsWith("$2"));
    }
}
