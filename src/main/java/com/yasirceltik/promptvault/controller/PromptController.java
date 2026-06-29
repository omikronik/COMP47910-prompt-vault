package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.dto.CreatePromptRequestDto;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.service.PromptService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptController {
	private final SessionService sessionService;
	private final PromptService promptService;

	@GetMapping
	public String listPrompts(HttpSession session, Model model) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}
		User user = sessionService.getCurrentUser(session);
		model.addAttribute("prompts", promptService.getPromptsForUser(user));
		return "prompt/list";
	}

	@GetMapping("/create")
	public String createPromptPage(HttpSession session, Model model) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}
		model.addAttribute("categories", promptService.getAllCategories());
		return "prompt/create";
	}

	@PostMapping("/create")
	public String createPrompt(HttpSession session, @ModelAttribute CreatePromptRequestDto dto) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}
		User user = sessionService.getCurrentUser(session);
		promptService.createPrompt(dto, user);
		return "redirect:/prompts";
	}

	@GetMapping("/{id}")
	public String viewPrompt(@PathVariable Long id, Model model) {
		return "prompt/details";
	}

	@GetMapping("/{id}/edit")
	public String editPromptPage(@PathVariable long id, Model model, HttpSession session) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}

		Prompt prompt = promptService.getPromptById(id).orElseThrow();

		model.addAttribute("prompt", prompt);
		model.addAttribute("categories", promptService.getAllCategories());
		return "prompt/edit";
	}

	@PostMapping("/{id}/edit")
	public String editPrompt(@PathVariable long id, HttpSession session, @ModelAttribute CreatePromptRequestDto dto) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}

		User user = sessionService.getCurrentUser(session);

		promptService.editPrompt(id, dto, user);
		return "redirect:/prompts";
	}

	@PostMapping("/{id}/delete")
	public String deletePrompt(@PathVariable Long id, HttpSession session) {
		if (!sessionService.isLoggedIn(session)) {
			return "redirect:/auth/login";
		}

		User user = sessionService.getCurrentUser(session);

		promptService.deletePrompt(id, user);

		return "redirect:/prompts";
	}
}
