package com.yasirceltik.promptvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequestDto(
        @NotBlank(message = "Message cannot be blank.")
        @Size(max = 255, message = "Message must not exceed 255 characters.")
        String content) {
}
