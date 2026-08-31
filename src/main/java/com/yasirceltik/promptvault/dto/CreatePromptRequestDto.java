package com.yasirceltik.promptvault.dto;

import com.yasirceltik.promptvault.model.PromptVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePromptRequestDto(
        @NotBlank(message = "Prompt title is required.")
        @Size(max = 255, message = "Prompt title must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "Prompt title must not contain line breaks.")
        String title,

        @NotBlank(message = "Prompt content is required.")
        @Size(max = 255, message = "Prompt content must not exceed 255 characters.")
        String content,

        @NotNull(message = "Visibility is required.")
        PromptVisibility visibility,

        Long categoryId) {
}
