package com.yasirceltik.promptvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/flagged-prompts")
public class AdminFlaggedPromptController {

    @GetMapping
    public String listFlaggedPrompts(Model model) {
        return "admin/flagged-prompts";
    }

    @GetMapping("/{id}")
    public String viewFlaggedPrompt(@PathVariable Long id, Model model) {
        return "admin/flagged-prompt-details";
    }
}
