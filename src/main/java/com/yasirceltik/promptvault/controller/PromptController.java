package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prompts")
public class PromptController {
	@GetMapping("/")
	public String listPrompts() {
		return "prompt/list";
	}

	@GetMapping("/create")
	public String createPromptPage() {
		return "prompt/create";
	}

	@PostMapping("/create")
	public String createPrompt() {
		return "redirect:/prompts";
	}

	@GetMapping("/{id}")
	public String viewPrompt(@PathVariable Long id, Model model) {
		return "prompt/details";
	}

	@GetMapping("/{id}/edit")
	public String editPromptPage(@PathVariable Long id, Model model) {
		return "prompt/edit";
	}

	@PostMapping("/{id}/edit")
	public String editPrompt(@PathVariable Long id, Model model) {
		return "redirect:/prompts";
	}

	@PostMapping("/{id}/delete")
	public String deletePrompt(@PathVariable Long id) {
		return "redirect:/prompts";
	}

}
