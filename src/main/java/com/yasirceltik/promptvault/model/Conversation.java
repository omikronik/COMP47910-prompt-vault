package com.yasirceltik.promptvault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "conversation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, length = 255)
	private String title;

	@ManyToOne
	@JoinColumn(name = "prompt_id", nullable = true)
	private Prompt prompt;

	@ManyToOne
	@JoinColumn(name = "owner")
	private User owner;

	private Boolean policyFlagged;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedOn;

	@ManyToOne
	@JoinColumn(name = "updated_by")
	private User updatedBy;

	@OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdOn ASC")
	private List<ConversationMessage> messages = new ArrayList<>();
}
