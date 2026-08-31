package com.yasirceltik.promptvault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.LoginResultDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.AuthService;
import com.yasirceltik.promptvault.service.SessionRegistryService;

import jakarta.servlet.http.HttpSession;

class AuthControllerTest {

        private AuthService authService;
        private SessionRegistryService sessionRegistryService;
        private AuthController authController;
        private HttpSession session;
        private Model model;
        private BindingResult bindingResult;
        private RedirectAttributes redirectAttributes;

        @BeforeEach
        void setUp() {
                authService = mock(AuthService.class);
                sessionRegistryService = mock(SessionRegistryService.class);
                session = mock(HttpSession.class);
                model = mock(Model.class);
                bindingResult = mock(BindingResult.class);
                redirectAttributes = mock(RedirectAttributes.class);

                authController = new AuthController(authService, sessionRegistryService);
        }

        @Test
        void loginPageReturnsLoginTemplate() {
                assertEquals(
                                "auth/login",
                                authController.loginPage(model)
                            );
        }

        @Test
        void registerPageReturnsRegisterTemplate() {
                assertEquals(
                                "auth/register",
                                authController.registerPage(model)
                            );

                verify(model).addAttribute(
                                eq("registerRequest"),
                                any(RegisterRequestDto.class)
                                );
        }

        @Test
        void successfulLoginStoresPrincipalAndRedirectsToDashboard() {
                LoginRequestDto request = new LoginRequestDto(
                                "john@example.com",
                                "Test1234"
                                );

                User user = User.builder()
                        .id(1L)
                        .username("johnsmith")
                        .email("john@example.com")
                        .password("$2a$10$someHash")
                        .role(UserRole.USER)
                        .active(true)
                        .build();

                when(authService.login(request))
                        .thenReturn(LoginResultDto.success(user));

                String result = authController.login(
                                request,
                                bindingResult,
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

                SessionUserDto principal =
                        (SessionUserDto) captor.getValue();

                assertEquals(1L, principal.id());
                assertEquals("johnsmith", principal.username());
                assertEquals("john@example.com", principal.email());
                assertEquals(UserRole.USER, principal.role());
        }

        @Test
        void failedLoginRedirectsToLoginAndDoesNotCreatePrincipal() {
                LoginRequestDto request = new LoginRequestDto(
                                "john@example.com",
                                "WrongPassword"
                                );

                when(authService.login(request))
                        .thenReturn(LoginResultDto.invalidCredentials());

                String result = authController.login(
                                request,
                                bindingResult,
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
        void lockedLoginRedirectsWithoutCreatingPrincipal() {
                LoginRequestDto request = new LoginRequestDto(
                                "john@example.com",
                                "WrongPassword"
                                );

                when(authService.login(request))
                        .thenReturn(LoginResultDto.locked(
                                        java.time.LocalDateTime.of(
                                                        2026, 8, 31, 7, 15
                                                        )
                                        ));

                String result = authController.login(
                                request,
                                bindingResult,
                                session,
                                redirectAttributes
                                );

                assertEquals("redirect:/auth/login", result);
                verify(session, never()).setAttribute(eq("user"), any());
                verify(sessionRegistryService, never())
                        .register(anyLong(), any(HttpSession.class));
                verify(redirectAttributes).addFlashAttribute(
                                "error",
                                "Too many failed login attempts. Please try again later."
                                );
        }

        @Test
        void successfulRegistrationRedirectsToLogin() {
                RegisterRequestDto request = new RegisterRequestDto(
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
                                bindingResult,
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
                RegisterRequestDto request = new RegisterRequestDto(
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
                                bindingResult,
                                redirectAttributes
                                );

                assertEquals(
                                "auth/register",
                                result
                            );

                verify(bindingResult)
                        .reject(
                                        "account.exists",
                                        "An account with that email or username already exists."
                               );
        }

        @Test
        void logoutInvalidatesSessionAndRedirectsToLogin() {
                String result = authController.logout(session);

                verify(session).invalidate();

                assertEquals(
                                "redirect:/auth/login",
                                result
                            );
        }
        @Test
        void registrationWithValidationErrorsDoesNotCallAuthService() {
                RegisterRequestDto request =
                        new RegisterRequestDto(
                                        "John",
                                        "Smith",
                                        "johnsmith",
                                        "john@example.com",
                                        "weak"
                                        );

                BindingResult bindingResult =
                        mock(BindingResult.class);

                when(bindingResult.hasErrors())
                        .thenReturn(true);

                String result =
                        authController.register(
                                        request,
                                        bindingResult,
                                        redirectAttributes
                                        );

                assertEquals(
                                "auth/register",
                                result
                            );

                verifyNoInteractions(authService);
        }
        @Test
        void validRegistrationCallsAuthService() {
                RegisterRequestDto request =
                        new RegisterRequestDto(
                                        "John",
                                        "Smith",
                                        "johnsmith",
                                        "john@example.com",
                                        "CorrectHorse!Battery"
                                        );

                BindingResult bindingResult =
                        mock(BindingResult.class);

                when(bindingResult.hasErrors())
                        .thenReturn(false);

                when(authService.register(request))
                        .thenReturn(true);

                String result =
                        authController.register(
                                        request,
                                        bindingResult,
                                        redirectAttributes
                                        );

                assertEquals(
                                "redirect:/auth/login",
                                result
                            );

                verify(authService)
                        .register(request);
        }
}
