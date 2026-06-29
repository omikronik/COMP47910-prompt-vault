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

	@Column(nullable = true, length = 255)
	private String firstName;

	@Column(nullable = true, length = 255)
	private String lastName;

	@Column(nullable = true, unique = true, length = 255)
	private String username;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	@Builder.Default
	private int loginAttempts = 0;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = true)
	private User createdBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedOn;

	@ManyToOne
	@JoinColumn(name = "updated_by", nullable = true)
	private User updatedBy;
}
