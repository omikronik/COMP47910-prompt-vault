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
@Table(name = "message_policy_match")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessagePolicyMatch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "message_id")
	private ConversationMessage messageId;

	@ManyToOne
	@JoinColumn(name = "policy_id")
	private PolicyKeyword policyId;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;
}
