package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yasirceltik.promptvault.dto.LoginRequestDto;
import com.yasirceltik.promptvault.dto.LoginResultDto;
import com.yasirceltik.promptvault.dto.RegisterRequestDto;
import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.LoginStatus;
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
	public String loginPage(Model model) {
		model.addAttribute("loginRequest", new LoginRequestDto("", ""));
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
			BindingResult bindingResult,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "auth/login";
		}
		LoginResultDto loginResult = authService.login(loginRequest);

		if (loginResult.status() == LoginStatus.LOCKED) {
			redirectAttributes.addFlashAttribute(
					"error",
					"Too many failed login attempts. Please try again later.");
			return "redirect:/auth/login";
		}

		if (loginResult.status() == LoginStatus.INVALID_CREDENTIALS) {
			redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
			return "redirect:/auth/login";
		}

		// only expose minimal user details to session
		User authenticatedUser = loginResult.user();
		SessionUserDto sessionUser = new SessionUserDto(
				authenticatedUser.getId(),
				authenticatedUser.getUsername(),
				authenticatedUser.getEmail(),
				authenticatedUser.getRole()
				);

		session.setAttribute("user", sessionUser);
		sessionRegistryService.register(authenticatedUser.getId(), session);
		log.info("Successful login for user id={}", authenticatedUser.getId());
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
