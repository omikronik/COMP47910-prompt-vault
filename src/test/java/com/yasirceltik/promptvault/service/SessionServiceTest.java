package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

class SessionServiceTest {

    private UserRepository userRepository;
    private SessionService sessionService;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        session = mock(HttpSession.class);

        sessionService = new SessionService(userRepository);
    }

    @Test
    void getPrincipalReturnsSessionPrincipal() {
        SessionUserDto principal = new SessionUserDto(
                1L,
                "johnsmith",
                "john@example.com",
                UserRole.USER
        );

        when(session.getAttribute("user"))
                .thenReturn(principal);

        SessionUserDto result =
                sessionService.getPrincipal(session);

        assertEquals(principal, result);
    }

    @Test
    void isLoggedInReturnsTrueWhenPrincipalExistsAndUserIsActive() {
            SessionUserDto principal = new SessionUserDto(
                            1L,
                            "johnsmith",
                            "john@example.com",
                            UserRole.USER
                            );

            User user = User.builder()
                    .id(1L)
                    .username("johnsmith")
                    .email("john@example.com")
                    .role(UserRole.USER)
                    .active(true)
                    .build();

            when(session.getAttribute("user"))
                    .thenReturn(principal);

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            assertTrue(
                            sessionService.isLoggedIn(session)
                      );

            verify(userRepository)
                    .findById(1L);
    }

    @Test
    void isLoggedInReturnsFalseWhenUserHasBeenDisabled() {
            SessionUserDto principal = new SessionUserDto(
                            1L,
                            "johnsmith",
                            "john@example.com",
                            UserRole.USER
                            );

            User user = User.builder()
                    .id(1L)
                    .username("johnsmith")
                    .email("john@example.com")
                    .role(UserRole.USER)
                    .active(false)
                    .build();

            when(session.getAttribute("user"))
                    .thenReturn(principal);

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            assertFalse(
                            sessionService.isLoggedIn(session)
                       );

            verify(userRepository)
                    .findById(1L);
    }

    @Test
    void isLoggedInReturnsFalseWhenPrincipalDoesNotExist() {
        when(session.getAttribute("user"))
                .thenReturn(null);

        assertFalse(sessionService.isLoggedIn(session));
    }

    @Test
    void getCurrentUserLoadsUserFromRepositoryByPrincipalId() {
        SessionUserDto principal = new SessionUserDto(
                1L,
                "johnsmith",
                "john@example.com",
                UserRole.USER
        );

        User user = User.builder()
                .id(1L)
                .username("johnsmith")
                .email("john@example.com")
                .password("$2a$10$someHash")
                .role(UserRole.USER)
                .active(true)
                .build();

        when(session.getAttribute("user"))
                .thenReturn(principal);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result =
                sessionService.getCurrentUser(session);

        assertEquals(user, result);

        verify(userRepository).findById(1L);
    }

    @Test
    void getCurrentUserReturnsNullWhenNotLoggedIn() {
        when(session.getAttribute("user"))
                .thenReturn(null);

        User result =
                sessionService.getCurrentUser(session);

        assertNull(result);

        verifyNoInteractions(userRepository);
    }
}
