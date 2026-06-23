package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.PolicyKeyword;

public interface PolicyKeywordRepository extends JpaRepository<PolicyKeyword, Long> {
}
