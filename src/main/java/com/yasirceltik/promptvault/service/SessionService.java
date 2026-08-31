package com.yasirceltik.promptvault.service;

import org.springframework.stereotype.Component;

import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionService {
	private final UserRepository userRepository;

	public SessionUserDto getPrincipal(HttpSession session) {
		return (SessionUserDto) session.getAttribute("user");
	}

	public User getCurrentUser(HttpSession session) {
		SessionUserDto principal = getPrincipal(session);

		if (principal == null) {
			return null;
		}

		return userRepository
			.findById(principal.id())
			.filter(User::isActive)
			.orElse(null);
	}

	public boolean isLoggedIn(HttpSession session) {
		return getCurrentUser(session) != null;
	}
}
