package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.PromptPolicyMatch;

public interface PromptPolicyMatchRepository extends JpaRepository<PromptPolicyMatch, Long> {
}
