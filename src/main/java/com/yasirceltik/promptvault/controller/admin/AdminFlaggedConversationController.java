package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/flagged-conversations")
@RequiredArgsConstructor
public class AdminFlaggedConversationController {
	private final SessionService sessionService;
	private final ConversationService conversationService;

	private boolean isAdmin(HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		return user != null && user.getRole() == UserRole.ADMIN;
	}

	@GetMapping
	public String listFlaggedConversations(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("conversations", conversationService.getFlaggedConversations());
		return "admin/flagged-conversations/list";
	}

	@PostMapping("/{id}/delete")
	public String deleteFlaggedConversation(@PathVariable Long id, HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		conversationService.adminDeleteConversation(id);
		return "redirect:/admin/flagged-conversations";
	}
}
