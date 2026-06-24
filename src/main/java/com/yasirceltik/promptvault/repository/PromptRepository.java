package com.yasirceltik.promptvault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
	Optional<Prompt> findByTitle(String title);
}
