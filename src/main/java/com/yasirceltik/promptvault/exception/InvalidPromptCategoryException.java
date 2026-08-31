package com.yasirceltik.promptvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPromptCategoryException extends RuntimeException {
    public InvalidPromptCategoryException() {
        super("Invalid prompt category.");
    }
}
