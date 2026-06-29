package com.yasirceltik.promptvault.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {
	public final SessionService sessionService;
	public final ConversationService conversationService;

	@ModelAttribute
	public void addCurrentUser(HttpSession session, Model model) {
		User user = sessionService.getCurrentUser(session);
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("isAdmin", user.getRole() == UserRole.ADMIN);
			model.addAttribute("sidebarConversations",
					conversationService.getConversationsForUser(user));
		}
	}
}
