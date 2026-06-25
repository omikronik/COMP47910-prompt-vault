package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("conversations")
public class ConversationController {
	@GetMapping
	public String listConversations(Model model) {
		return "conversation/list";
	}

	@GetMapping("/{id}")
	public String viewConversation(@PathVariable Long id, Model model) {
		return "conversation/details";
	}

	@PostMapping("/start/{promptId}")
	public String startConversation(@PathVariable Long promptId) {
		return "redirect:/conversations/1";
	}

	@PostMapping("/{id}/message")
	public String sendMessage(@PathVariable Long id) {
		return "redirect:/conversations/" + id;
	}
}
