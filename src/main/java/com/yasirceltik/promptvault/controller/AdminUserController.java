package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	@GetMapping
	public String listUsers(Model model) {
		return "admin/users";
	}

	@PostMapping("{id}/enable")
	public String enableUser(@PathVariable Long id) {
		return "redirect:/admin/users";
	}

	@PostMapping("{id}/disable")
	public String disableUser(@PathVariable Long id) {
		return "redirect:/admin/users";
	}
}
