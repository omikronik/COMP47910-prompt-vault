package com.yasirceltik.promptvault.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
	private final ConversationService conversationService;
	private final SessionService sessionService;

	@PostMapping("/start")
	public String startConversation(@RequestParam(required = false) Long promptId, HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		if (user == null)
			return "redirect:/auth/login";

		Conversation conversation = conversationService.createConversation(user, promptId);
		return "redirect:/conversations/" + conversation.getId();
	}

	@GetMapping("/{id}")
	public String viewConversation(@PathVariable Long id, HttpSession session, Model model) {
		User user = sessionService.getCurrentUser(session);
		if (user == null)
			return "redirect:/auth/login";

		Conversation conversation = conversationService.getConversationById(id)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));

		if (conversation.getOwner().getId() != user.getId()) {
			return "redirect:/dashboard";
		}

		List<ConversationMessage> messages = conversationService.getMessages(id);
		model.addAttribute("conversation", conversation);
		model.addAttribute("messages", messages);
		return "conversation/details";
	}

	@PostMapping("/{id}/messages")
	public String sendMessage(@PathVariable Long id, @RequestParam String content, HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		if (user == null)
			return "redirect:/auth/login";

		conversationService.sendMessage(id, content, user);
		return "redirect:/conversations/" + id;
	}
}
