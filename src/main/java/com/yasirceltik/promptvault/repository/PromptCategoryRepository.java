package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.PromptCategory;

public interface PromptCategoryRepository extends JpaRepository<PromptCategory, Long> {
}
