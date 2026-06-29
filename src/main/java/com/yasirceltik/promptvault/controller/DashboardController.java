package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
	private final SessionService sessionService;
	private final ConversationService conversationService;

	@GetMapping
	public String dashboard(HttpSession session, Model model) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}
		User user = sessionService.getCurrentUser(session);
		model.addAttribute("conversations", conversationService.getConversationsForUser(user));
		return "dashboard/index";
	}
}
