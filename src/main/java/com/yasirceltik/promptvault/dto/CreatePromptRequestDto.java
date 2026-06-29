package com.yasirceltik.promptvault.dto;

import com.yasirceltik.promptvault.model.PromptVisibility;

public record CreatePromptRequestDto(String title, String content, PromptVisibility visibility, Long categoryId) {
}
