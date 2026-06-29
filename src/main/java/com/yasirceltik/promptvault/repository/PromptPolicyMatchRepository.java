package com.yasirceltik.promptvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptPolicyMatch;

public interface PromptPolicyMatchRepository extends JpaRepository<PromptPolicyMatch, Long> {
	void deleteByPrompt(Prompt prompt);

	List<PromptPolicyMatch> findByPrompt(Prompt prompt);
}
