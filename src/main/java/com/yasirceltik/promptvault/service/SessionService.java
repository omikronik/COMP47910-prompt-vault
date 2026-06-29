package com.yasirceltik.promptvault.service;

import org.springframework.stereotype.Component;

import com.yasirceltik.promptvault.model.User;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionService {
	public User getCurrentUser(HttpSession session) {
		return (User) session.getAttribute("user");
	}

	public boolean isLoggedIn(HttpSession session) {
		return getCurrentUser(session) != null;
	}
}
