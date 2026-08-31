package com.yasirceltik.promptvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @NotBlank(message = "First name is required.")
        @Size(max = 255, message = "First name must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "First name must not contain line breaks.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        @Size(max = 255, message = "Last name must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "Last name must not contain line breaks.")
        String lastName,

        @NotBlank(message = "Username is required.")
        @Size(max = 255, message = "Username must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "Username must not contain line breaks.")
        String username,

        @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.")
        @Size(max = 255, message = "Email must not exceed 255 characters.")
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
