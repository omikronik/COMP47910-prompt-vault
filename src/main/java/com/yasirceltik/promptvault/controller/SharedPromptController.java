package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shared-prompts")
public class SharedPromptController {
	@GetMapping
	public String browseSharedPrompts() {
		return "prompt/shared";
	}

	@GetMapping("/{id}")
	public String viewSharedPrompt(@PathVariable Long id, Model model) {
		return "prompt/shared-details";
	}
}
