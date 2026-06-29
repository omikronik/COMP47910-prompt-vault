package com.yasirceltik.promptvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
	List<ConversationMessage> findByConversationOrderByCreatedOnAsc(Conversation conversation);
}
