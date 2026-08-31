package com.yasirceltik.promptvault.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.service.AuthService;
import com.yasirceltik.promptvault.service.SessionRegistryService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final SessionRegistryService sessionRegistryService;

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

		// only expose minimal user details to session
		User authenticatedUser = user.get();
		SessionUserDto sessionUser = new SessionUserDto(
				authenticatedUser.getId(),
				authenticatedUser.getUsername(),
				authenticatedUser.getEmail(),
				authenticatedUser.getRole()
				);

		session.setAttribute("user", sessionUser);
		sessionRegistryService.register(authenticatedUser.getId(), session);
		log.info("Successful login attempt for {}", loginRequest.email());
		return "redirect:/dashboard";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute(
				"registerRequest", 
				new RegisterRequestDto("","","","","")
				);
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(
			@Valid @ModelAttribute("registerRequest") RegisterRequestDto registerRequest,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return "auth/register";
		}

		boolean success = authService.register(registerRequest);

		if (!success) {
			bindingResult.reject("account.exists","An account with that email or username already exists.");
			return "auth/register";
		}

		redirectAttributes.addFlashAttribute("success", "Account created. You can now sign in.");

		return "redirect:/auth/login";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		SessionUserDto principal = (SessionUserDto) session.getAttribute("user");

		if (principal != null) {
			sessionRegistryService.unregister(principal.id(), session);	
		}

		session.invalidate();
		return "redirect:/auth/login";
	}
}
