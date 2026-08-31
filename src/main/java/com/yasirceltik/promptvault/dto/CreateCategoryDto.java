package com.yasirceltik.promptvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryDto(
        @NotBlank(message = "Category name is required.")
        @Size(max = 255, message = "Category name must not exceed 255 characters.")
        @Pattern(regexp = "^[^\\r\\n]*$", message = "Category name must not contain line breaks.")
        String name,

        @NotBlank(message = "Description is required.")
        @Size(max = 255, message = "Description must not exceed 255 characters.")
        String description) {
}
