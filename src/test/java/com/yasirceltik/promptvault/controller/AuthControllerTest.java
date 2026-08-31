package com.yasirceltik.promptvault.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.AuthService;

import jakarta.servlet.http.HttpSession;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;
    private HttpSession session;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        session = mock(HttpSession.class);
        redirectAttributes = mock(RedirectAttributes.class);

        authController = new AuthController(authService);
    }

    @Test
    void successfulLoginStoresMinimalPrincipalInSession() {
        LoginRequestDto request =
                new LoginRequestDto(
                        "john@example.com",
                        "Test1234"
                );

        User user = User.builder()
                .id(1L)
                .username("johnsmith")
                .email("john@example.com")
                .password("$2a$10$secretHashThatShouldNotBeInSession")
                .role(UserRole.USER)
                .active(true)
                .build();

        when(authService.login(request))
                .thenReturn(Optional.of(user));

        String result = authController.login(
                request,
                session,
                redirectAttributes
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        ArgumentCaptor<Object> captor =
                ArgumentCaptor.forClass(Object.class);

        verify(session).setAttribute(
                eq("user"),
                captor.capture()
        );

        Object sessionValue = captor.getValue();

        assertInstanceOf(
                SessionUserDto.class,
                sessionValue
        );

        SessionUserDto principal =
                (SessionUserDto) sessionValue;

        assertEquals(1L, principal.id());
        assertEquals(
                "johnsmith",
                principal.username()
        );
        assertEquals(
                "john@example.com",
                principal.email()
        );
        assertEquals(
                UserRole.USER,
                principal.role()
        );
    }

    @Test
    void failedLoginDoesNotStoreAnythingInSession() {
        LoginRequestDto request =
                new LoginRequestDto(
                        "john@example.com",
                        "WrongPassword"
                );

        when(authService.login(request))
                .thenReturn(Optional.empty());

        String result = authController.login(
                request,
                session,
                redirectAttributes
        );

        assertEquals(
                "redirect:/auth/login",
                result
        );

        verify(session, never())
                .setAttribute(
                        eq("user"),
                        any()
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "error",
                        "Invalid email or password."
                );
    }

    @Test
    void logoutInvalidatesSession() {
        String result =
                authController.logout(session);

        verify(session).invalidate();

        assertEquals(
                "redirect:/auth/login",
                result
        );
    }

    @Test
    void successfulRegistrationRedirectsToLogin() {
        RegisterRequestDto request =
                new RegisterRequestDto(
                        "John",
                        "Smith",
                        "johnsmith",
                        "john@example.com",
                        "Test1234"
                );

        when(authService.register(request))
                .thenReturn(true);

        String result = authController.register(
                request,
                redirectAttributes
        );

        assertEquals(
                "redirect:/auth/login",
                result
        );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "success",
                        "Account created. You can now sign in."
                );
    }

    @Test
    void failedRegistrationRedirectsBackToRegister() {
        RegisterRequestDto request =
                new RegisterRequestDto(
                        "John",
                        "Smith",
                        "johnsmith",
                        "john@example.com",
                        "Test1234"
                );

        when(authService.register(request))
                .thenReturn(false);

        String result = authController.register(
                request,
                redirectAttributes
        );

        assertEquals(
                "redirect:/auth/register",
                result
        );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "error",
                        "An account with that email or username already exists."
                );
    }
}
