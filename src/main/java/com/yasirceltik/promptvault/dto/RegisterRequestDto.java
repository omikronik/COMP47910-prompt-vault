package com.yasirceltik.promptvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @NotBlank(message = "First name is required.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        String lastName,

        @NotBlank(message = "Username is required.")
        String username,

        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(
                min = 12,
                max = 64,
                message = "Password must be between 12 and 64 characters."
        )
        @Pattern(
                regexp = ".*\\p{Lu}.*",
                message = "Password must contain at least one uppercase letter."
        )
        @Pattern(
                regexp = ".*\\p{Ll}.*",
                message = "Password must contain at least one lowercase letter."
        )
        @Pattern(
                regexp = ".*[^\\p{L}\\p{N}\\s].*",
                message = "Password must contain at least one special character."
        )
        String password

) {
}
