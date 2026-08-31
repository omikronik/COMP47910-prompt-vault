package com.yasirceltik.promptvault.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import jakarta.validation.Valid;
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
	public String createCategoryPage(HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("categoryRequest", new CreateCategoryDto("", ""));
		return "admin/categories/create";
	}

	@PostMapping("/create")
	public String createCategory(HttpSession session,
			@Valid @ModelAttribute("categoryRequest") CreateCategoryDto dto,
			BindingResult bindingResult) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		if (bindingResult.hasErrors()) return "admin/categories/create";
		User user = sessionService.getCurrentUser(session);
		if (!categoryService.createCategory(dto, user)) {
			bindingResult.rejectValue("name", "category.duplicate", "That category already exists.");
			return "admin/categories/create";
		}
		return "redirect:/admin/categories";
	}

	@GetMapping("/{id}/edit")
	public String editCategoryPage(@PathVariable Long id, HttpSession session, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		model.addAttribute("category", categoryService.getCategoryById(id).orElseThrow());
		var category = categoryService.getCategoryById(id).orElseThrow();
		model.addAttribute("categoryRequest", new CreateCategoryDto(category.getName(), category.getDescription()));
		return "admin/categories/edit";
	}

	@PostMapping("/{id}/edit")
	public String editCategory(@PathVariable Long id, HttpSession session,
			@Valid @ModelAttribute("categoryRequest") CreateCategoryDto dto,
			BindingResult bindingResult, Model model) {
		if (!isAdmin(session))
			return "redirect:/dashboard";
		if (bindingResult.hasErrors()) {
			model.addAttribute("category", categoryService.getCategoryById(id).orElseThrow());
			return "admin/categories/edit";
		}
		User user = sessionService.getCurrentUser(session);
		if (!categoryService.editCategory(id, dto, user)) {
			bindingResult.rejectValue("name", "category.duplicate", "That category already exists.");
			model.addAttribute("category", categoryService.getCategoryById(id).orElseThrow());
			return "admin/categories/edit";
		}
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
