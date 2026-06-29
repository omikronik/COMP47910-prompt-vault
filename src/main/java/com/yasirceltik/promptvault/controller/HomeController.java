package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final SessionService sessionService;

	@GetMapping("/")
	public String home(HttpSession session) {
		if (sessionService.isLoggedIn(session)) {
			return "redirect:/dashboard/index";
		}

		return "redirect:/auth/login";
	}
}
