package com.yasirceltik.promptvault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "conversation_message")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConversationMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "conversation_id")
	private Conversation conversation;

	@Column(nullable = false)
	private String content;

	private Boolean policyFlagged;

	@Enumerated(EnumType.STRING)
	private ChatRole role;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;
}
