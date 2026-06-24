package com.yasirceltik.promptvault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.PromptCategory;

public interface PromptCategoryRepository extends JpaRepository<PromptCategory, Long> {
	Optional<PromptCategory> findByName(String name);
}
