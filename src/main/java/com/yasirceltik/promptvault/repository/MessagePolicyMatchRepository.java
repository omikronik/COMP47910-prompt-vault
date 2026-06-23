package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.MessagePolicyMatch;

public interface MessagePolicyMatchRepository extends JpaRepository<MessagePolicyMatch, Long> {
}
