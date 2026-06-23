package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
