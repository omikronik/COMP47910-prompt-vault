package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.dto.LoginRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("/login")
	public String loginPage() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest loginRequest) {
		log.info("Entering login for email: " + loginRequest.email());
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
