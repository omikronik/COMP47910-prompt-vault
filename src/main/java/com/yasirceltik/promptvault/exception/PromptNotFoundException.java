package com.yasirceltik.promptvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PromptNotFoundException extends RuntimeException {

    public PromptNotFoundException() {
        super("Prompt not found");
    }
}
