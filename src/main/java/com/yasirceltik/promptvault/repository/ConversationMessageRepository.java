package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.ConversationMessage;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
}
