
package com.yasirceltik.promptvault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "prompt")
public class Prompt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true, length = 255)
	private String title;

	private String content;

	@ManyToOne
	@JoinColumn(name = "owner")
	private User owner;

	private String visibility;

	private Boolean policyFlagged;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private PromptCategory categoryId;

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
}
