package com.yasirceltik.promptvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.User;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
	List<Conversation> findByOwnerOrderByCreatedOnDesc(User user);

	List<Conversation> findByPolicyFlaggedTrue();

	@Modifying
	@Query("UPDATE Conversation c SET c.prompt = null WHERE c.prompt.id = :promptId")
	void nullifyPromptReferences(@Param("promptId") Long promptId);
}
