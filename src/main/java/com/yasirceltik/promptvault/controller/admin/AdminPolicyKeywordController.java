package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import jakarta.validation.Valid;
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
	public String createKeywordPage(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("keywordRequest", new CreatePolicyKeywordDto(""));
		return "admin/policy-keywords/create";
	}

	@PostMapping("/create")
	public String createKeyword(HttpSession session,
			@Valid @ModelAttribute("keywordRequest") CreatePolicyKeywordDto dto,
			BindingResult bindingResult) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		if (bindingResult.hasErrors()) return "admin/policy-keywords/create";
		User user = sessionService.getCurrentUser(session);
		if (!policyKeywordService.createKeyword(dto, user)) {
			bindingResult.rejectValue("content", "keyword.duplicate", "That keyword already exists.");
			return "admin/policy-keywords/create";
		}
		return "redirect:/admin/policy-keywords";
	}

	@GetMapping("/{id}/edit")
	public String editKeywordPage(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("keyword", policyKeywordService.getKeywordById(id).orElseThrow());
		model.addAttribute("keywordRequest", new CreatePolicyKeywordDto(
				policyKeywordService.getKeywordById(id).orElseThrow().getContent()));
		return "admin/policy-keywords/edit";
	}

	@PostMapping("/{id}/edit")
	public String editKeyword(@PathVariable Long id, HttpSession session,
			@Valid @ModelAttribute("keywordRequest") CreatePolicyKeywordDto dto,
			BindingResult bindingResult, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		if (bindingResult.hasErrors()) {
			model.addAttribute("keyword", policyKeywordService.getKeywordById(id).orElseThrow());
			return "admin/policy-keywords/edit";
		}
		User user = sessionService.getCurrentUser(session);
		if (!policyKeywordService.editKeyword(id, dto, user)) {
			bindingResult.rejectValue("content", "keyword.duplicate", "That keyword already exists.");
			model.addAttribute("keyword", policyKeywordService.getKeywordById(id).orElseThrow());
			return "admin/policy-keywords/edit";
		}
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
