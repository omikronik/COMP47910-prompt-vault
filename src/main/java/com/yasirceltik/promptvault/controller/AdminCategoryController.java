package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
	@GetMapping
	public String listCategories(Model model) {
		return "admin/users";
	}

	@GetMapping("/create")
	public String createCategoryPage(Model model) {
		return "admin/category-create";
	}

	@PostMapping("/create")
	public String createCategory() {
		return "redirect:/admin/categories";
	}

	@GetMapping("/{id}/edit")
	public String editCategoryPage(@PathVariable Long id, Model model) {
		return "admin/category-edit";
	}

	@PostMapping("/{id}/edit")
	public String editCategory(@PathVariable Long id) {
		return "redirect:/admin/categories";
	}

	@PostMapping("/{id}/delete")
	public String deleteCategory(@PathVariable Long id) {
		return "redirect:/admin/categories";
	}
}
