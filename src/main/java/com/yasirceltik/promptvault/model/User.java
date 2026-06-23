package com.yasirceltik.promptvault.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 50)
	private String role;

	@Column(nullable = false)
	private Boolean isActive;

	@Column(nullable = false)
	private int loginAttempts = 0;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedOn;

	@ManyToOne
	@JoinColumn(name = "updated_by", nullable = false)
	private User updatedBy;
}
