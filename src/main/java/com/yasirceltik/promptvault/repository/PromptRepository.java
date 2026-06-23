package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
