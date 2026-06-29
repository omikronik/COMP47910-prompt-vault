package com.yasirceltik.promptvault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptVisibility;
import com.yasirceltik.promptvault.model.User;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
	Optional<Prompt> findByTitle(String title);

	List<Prompt> findAllByOwner(User owner);

	List<Prompt> findAllByVisibility(PromptVisibility visibility);

	@Modifying
	@Query("UPDATE Prompt p SET p.category = null WHERE p.category.id = :categoryId")
	void nullifyCategoryReferences(@Param("categoryId") Long categoryId);
}
