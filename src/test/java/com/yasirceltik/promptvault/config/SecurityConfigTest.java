package com.yasirceltik.promptvault.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    @Test
    void passwordEncoderUsesBCrypt() {
        SecurityConfig config =
                new SecurityConfig(
                        mock(LoginRateLimitFilter.class)
                );

        PasswordEncoder encoder =
                config.passwordEncoder();

        assertInstanceOf(
                BCryptPasswordEncoder.class,
                encoder
        );
    }

    @Test
    void passwordEncoderHashesAndVerifiesPassword() {
        SecurityConfig config =
                new SecurityConfig(
                        mock(LoginRateLimitFilter.class)
                );

        PasswordEncoder encoder =
                config.passwordEncoder();

        String rawPassword = "Test1234";
        String encodedPassword =
                encoder.encode(rawPassword);

        assertNotEquals(
                rawPassword,
                encodedPassword
        );

        assertTrue(
                encoder.matches(
                        rawPassword,
                        encodedPassword
                )
        );
    }
}
