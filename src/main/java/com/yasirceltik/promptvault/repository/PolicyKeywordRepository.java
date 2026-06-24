package com.yasirceltik.promptvault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.PolicyKeyword;

public interface PolicyKeywordRepository extends JpaRepository<PolicyKeyword, Long> {
	Optional<PolicyKeyword> findByContent(String content);
}
