package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.SessionService;
import com.yasirceltik.promptvault.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
	private final SessionService sessionService;
	private final UserService userService;

	private boolean isAdmin(HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		return user != null && user.getRole() == UserRole.ADMIN;
	}

	@GetMapping
	public String listUsers(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("users", userService.getAllUsers());
		return "admin/users/list";
	}

	@GetMapping("/{id}")
	public String viewUser(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("target", userService.getUserById(id).orElseThrow());
		return "admin/users/view";
	}

	@GetMapping("/{id}/edit")
	public String editUserPage(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("target", userService.getUserById(id).orElseThrow());
		return "admin/users/edit";
	}

	@PostMapping("/{id}/edit")
	public String editUser(@PathVariable Long id, HttpSession session,
			@RequestParam boolean isActive) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		userService.setActive(id, isActive);
		return "redirect:/admin/users";
	}
}
