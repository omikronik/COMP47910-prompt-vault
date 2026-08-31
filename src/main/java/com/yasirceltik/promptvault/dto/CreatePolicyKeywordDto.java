package com.yasirceltik.promptvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePolicyKeywordDto(
        @NotBlank(message = "Keyword cannot be blank.")
        @Size(max = 255, message = "Keyword must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "Keyword must not contain line breaks.")
        String content) {
}
