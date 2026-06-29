package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.PromptService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/flagged-prompts")
@RequiredArgsConstructor
public class AdminFlaggedPromptController {
	private final SessionService sessionService;
	private final PromptService promptService;

	private boolean isAdmin(HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		return user != null && user.getRole() == UserRole.ADMIN;
	}

	@GetMapping
	public String listFlaggedPrompts(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("matches", promptService.getFlaggedPromptMatches());
		return "admin/flagged-prompts/list";
	}

	@PostMapping("/{id}/delete")
	public String deleteFlaggedPrompt(@PathVariable Long id, HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		User user = sessionService.getCurrentUser(session);
		promptService.deletePrompt(id, user);
		return "redirect:/admin/flagged-prompts";
	}
}
