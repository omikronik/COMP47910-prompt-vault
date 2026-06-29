package com.yasirceltik.promptvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yasirceltik.promptvault.model.MessagePolicyMatch;

public interface MessagePolicyMatchRepository extends JpaRepository<MessagePolicyMatch, Long> {
	@Modifying
	@Query("DELETE FROM MessagePolicyMatch m WHERE m.message.conversation.id = :conversationId")
	void deleteByConversationId(@Param("conversationId") Long conversationId);
}
