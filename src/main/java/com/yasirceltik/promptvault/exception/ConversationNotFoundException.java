package com.yasirceltik.promptvault.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Conversation not found.");
    }
}
