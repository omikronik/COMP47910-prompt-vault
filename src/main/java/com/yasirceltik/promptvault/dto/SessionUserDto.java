package com.yasirceltik.promptvault.dto;

import com.yasirceltik.promptvault.model.UserRole;

public record SessionUserDto(
		long id,
		String username,
		String email,
		UserRole role
		) {}
