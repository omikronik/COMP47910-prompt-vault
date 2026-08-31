package com.yasirceltik.promptvault.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionRegistryService {

    private final Map<Long, Set<HttpSession>> sessionsByUser =
            new ConcurrentHashMap<>();

    public void register(Long userId, HttpSession session) {
        sessionsByUser
                .computeIfAbsent(
                        userId,
                        id -> ConcurrentHashMap.newKeySet()
                )
                .add(session);
    }

    public void unregister(Long userId, HttpSession session) {
        Set<HttpSession> sessions =
                sessionsByUser.get(userId);

        if (sessions == null) {
            return;
        }

        sessions.remove(session);

        if (sessions.isEmpty()) {
            sessionsByUser.remove(userId);
        }
    }

    public void expireSessionsForUser(Long userId) {
        Set<HttpSession> sessions =
                sessionsByUser.remove(userId);

        if (sessions == null) {
            return;
        }

        for (HttpSession session : sessions) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
                // Session was already invalidated.
            }
        }
    }
}
