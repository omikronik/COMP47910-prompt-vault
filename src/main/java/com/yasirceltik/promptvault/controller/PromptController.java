package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import jakarta.validation.Valid;
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
		User user = sessionService.getCurrentUser(session);
		model.addAttribute("prompts", promptService.getPromptsForUser(user));
		return "prompt/list";
	}

	@GetMapping("/create")
	public String createPromptPage(HttpSession session, Model model) {
		model.addAttribute("promptRequest", new CreatePromptRequestDto("", "", com.yasirceltik.promptvault.model.PromptVisibility.PRIVATE, null));
		model.addAttribute("categories", promptService.getAllCategories());
		return "prompt/create";
	}

	@PostMapping("/create")
	public String createPrompt(HttpSession session,
			@Valid @ModelAttribute("promptRequest") CreatePromptRequestDto dto,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", promptService.getAllCategories());
			return "prompt/create";
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
		User user = sessionService.getCurrentUser(session);
		Prompt prompt = promptService.getPromptByIdAndOwner(id, user);

		model.addAttribute("prompt", prompt);
		model.addAttribute("promptRequest", new CreatePromptRequestDto(
				prompt.getTitle(), prompt.getContent(), prompt.getVisibility(),
				prompt.getCategory() == null ? null : prompt.getCategory().getId()));
		model.addAttribute("categories", promptService.getAllCategories());
		return "prompt/edit";
	}

	@PostMapping("/{id}/edit")
	public String editPrompt(@PathVariable long id, HttpSession session,
			@Valid @ModelAttribute("promptRequest") CreatePromptRequestDto dto,
			BindingResult bindingResult, Model model) {
		User user = sessionService.getCurrentUser(session);
		if (bindingResult.hasErrors()) {
			model.addAttribute("prompt", promptService.getPromptByIdAndOwner(id, user));
			model.addAttribute("categories", promptService.getAllCategories());
			return "prompt/edit";
		}

		promptService.editPrompt(id, dto, user);
		return "redirect:/prompts";
	}

	@PostMapping("/{id}/delete")
	public String deletePrompt(@PathVariable Long id, HttpSession session) {
		User user = sessionService.getCurrentUser(session);

		promptService.deletePrompt(id, user);

		return "redirect:/prompts";
	}
}
