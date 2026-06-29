package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.dto.CreatePolicyKeywordDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.PolicyKeywordService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/policy-keywords")
@RequiredArgsConstructor
public class AdminPolicyKeywordController {
	private final SessionService sessionService;
	private final PolicyKeywordService policyKeywordService;

	private boolean isAdmin(HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		return user != null && user.getRole() == UserRole.ADMIN;
	}

	@GetMapping
	public String listKeywords(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("keywords", policyKeywordService.getAllKeywords());
		return "admin/policy-keywords/list";
	}

	@GetMapping("/{id}")
	public String viewKeyword(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("keyword", policyKeywordService.getKeywordById(id).orElseThrow());
		return "admin/policy-keywords/view";
	}

	@GetMapping("/create")
	public String createKeywordPage(HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		return "admin/policy-keywords/create";
	}

	@PostMapping("/create")
	public String createKeyword(HttpSession session, @ModelAttribute CreatePolicyKeywordDto dto) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		User user = sessionService.getCurrentUser(session);
		policyKeywordService.createKeyword(dto, user);
		return "redirect:/admin/policy-keywords";
	}

	@GetMapping("/{id}/edit")
	public String editKeywordPage(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("keyword", policyKeywordService.getKeywordById(id).orElseThrow());
		return "admin/policy-keywords/edit";
	}

	@PostMapping("/{id}/edit")
	public String editKeyword(@PathVariable Long id, HttpSession session, @ModelAttribute CreatePolicyKeywordDto dto) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		User user = sessionService.getCurrentUser(session);
		policyKeywordService.editKeyword(id, dto, user);
		return "redirect:/admin/policy-keywords";
	}

	@PostMapping("/{id}/delete")
	public String deleteKeyword(@PathVariable Long id, HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		policyKeywordService.deleteKeyword(id);
		return "redirect:/admin/policy-keywords";
	}
}
