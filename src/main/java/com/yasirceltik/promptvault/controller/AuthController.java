package com.yasirceltik.promptvault.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.service.AuthService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@GetMapping("/login")
	public String loginPage() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequestDto loginRequest,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		log.info("Login attempt {}", loginRequest.email());

		Optional<User> user = authService.login(loginRequest);

		if (user.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
			log.info("Failed login attempt for {}", loginRequest.email());
			return "redirect:/auth/login";
		}

		session.setAttribute("user", user.get());
		log.info("Successful login attempt for {}", loginRequest.email());
		return "redirect:/dashboard";
	}

	@GetMapping("/register")
	public String registerPage() {
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute RegisterRequestDto registerRequest,
			RedirectAttributes redirectAttributes) {
		boolean success = authService.register(registerRequest);

		if (!success) {
			redirectAttributes.addFlashAttribute("error", "An account with that email or username already exists.");
			return "redirect:/auth/register";
		}

		redirectAttributes.addFlashAttribute("success", "Account created. You can now sign in.");
		return "redirect:/auth/login";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/auth/login";
	}
}
