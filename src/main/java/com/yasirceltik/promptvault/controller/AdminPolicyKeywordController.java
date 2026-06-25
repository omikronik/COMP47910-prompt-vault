package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/policy-keywords")
public class AdminPolicyKeywordController {

    @GetMapping
    public String listKeywords(Model model) {
        return "admin/policy-keywords";
    }

    @GetMapping("/create")
    public String createKeywordPage(Model model) {
        return "admin/keyword-create";
    }

    @PostMapping("/create")
    public String createKeyword() {
        return "redirect:/admin/policy-keywords";
    }

    @GetMapping("/{id}/edit")
    public String editKeywordPage(@PathVariable Long id, Model model) {
        return "admin/keyword-edit";
    }

    @PostMapping("/{id}/edit")
    public String editKeyword(@PathVariable Long id) {
        return "redirect:/admin/policy-keywords";
    }

    @PostMapping("/{id}/delete")
    public String deleteKeyword(@PathVariable Long id) {
        return "redirect:/admin/policy-keywords";
    }
}
