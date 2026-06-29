package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.service.PromptService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/prompts/shared")
@RequiredArgsConstructor
public class SharedPromptController {
	private final SessionService sessionService;
	private final PromptService promptService;

	@GetMapping
	public String browseSharedPrompts(HttpSession session, Model model) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}
		model.addAttribute("prompts", promptService.getSharedPrompts());
		return "prompt/shared";
	}

	@GetMapping("/{id}")
	public String viewSharedPrompt(@PathVariable Long id, Model model) {
		return "prompt/shared-details";
	}
}
