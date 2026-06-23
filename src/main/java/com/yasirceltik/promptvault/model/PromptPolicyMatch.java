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
@Table(name = "prompt_policy_match")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromptPolicyMatch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "prompt_id")
	private Prompt promptId;

	@ManyToOne
	@JoinColumn(name = "policy_id")
	private PolicyKeyword policyId;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;
}
