package com.yasirceltik.promptvault.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;

	@Column(nullable = false, unique = true, length = 255)
	private String Email;

	@Column(nullable = false)
	private String Password;

	@Column(nullable = false, length = 50)
	private String Role;

	@Column(nullable = false)
	private Boolean isActive;

	@Column(nullable = false)
	private int LoginAttempts = 0;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

	//@Column(nullable = false)
	@ManyToOne
	@JoinColumn("created_by")
	private User createdBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedOn;

	@ManyToOne
	@JoinColumn("updated_by")
	private long updatedBy;
	
	
}
