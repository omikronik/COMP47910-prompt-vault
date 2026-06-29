package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yasirceltik.promptvault.dto.CreateCategoryDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.CategoryService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
	private final SessionService sessionService;
	private final CategoryService categoryService;

	private boolean isAdmin(HttpSession session) {
		User user = sessionService.getCurrentUser(session);
		return user != null && user.getRole() == UserRole.ADMIN;
	}

	@GetMapping
	public String listCategories(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/categories/list";
	}

	@GetMapping("/{id}")
	public String viewCategory(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("category", categoryService.getCategoryById(id).orElseThrow());
		return "admin/categories/view";
	}

	@GetMapping("/create")
	public String createCategoryPage(HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		return "admin/categories/create";
	}

	@PostMapping("/create")
	public String createCategory(HttpSession session, @ModelAttribute CreateCategoryDto dto) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		User user = sessionService.getCurrentUser(session);
		categoryService.createCategory(dto, user);
		return "redirect:/admin/categories";
	}

	@GetMapping("/{id}/edit")
	public String editCategoryPage(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("category", categoryService.getCategoryById(id).orElseThrow());
		return "admin/categories/edit";
	}

	@PostMapping("/{id}/edit")
	public String editCategory(@PathVariable Long id, HttpSession session, @ModelAttribute CreateCategoryDto dto) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		User user = sessionService.getCurrentUser(session);
		categoryService.editCategory(id, dto, user);
		return "redirect:/admin/categories";
	}

	@PostMapping("/{id}/delete")
	public String deleteCategory(@PathVariable Long id, HttpSession session) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		categoryService.deleteCategory(id);
		return "redirect:/admin/categories";
	}
}
