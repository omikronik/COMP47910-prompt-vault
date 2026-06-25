package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("/login")
	public String loginPage() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login() {
		return "redirect:/dashboard";
	}

	@GetMapping("/register") 
	public String registerPage() {
		return "auth/register";
	}

	@PostMapping("/register") 
	public String register() {
		return "redirect:/auth/login";
	}

	@PostMapping("/logout") 
	public String logout() {
		return "redirect:/auth/login";
	}
}
