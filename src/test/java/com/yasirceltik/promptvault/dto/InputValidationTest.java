package com.yasirceltik.promptvault.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.yasirceltik.promptvault.model.PromptVisibility;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class InputValidationTest {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void blankAndWhitespacePolicyKeywordsAreRejected() {
        assertFalse(validator.validate(new CreatePolicyKeywordDto("")).isEmpty());
        assertFalse(validator.validate(new CreatePolicyKeywordDto("   ")).isEmpty());
    }

    @Test
    void validPolicyKeywordIsAccepted() {
        assertTrue(validator.validate(new CreatePolicyKeywordDto("password")).isEmpty());
    }

    @Test
    void categoryRejectsBlankOversizedAndCrLfValues() {
        assertFalse(validator.validate(new CreateCategoryDto(" ", "Description")).isEmpty());
        assertFalse(validator.validate(new CreateCategoryDto("safe\r\nname", "Description")).isEmpty());
        assertFalse(validator.validate(new CreateCategoryDto("Name", "x".repeat(256))).isEmpty());
    }

    @Test
    void promptRejectsBlankOversizedAndMissingVisibilityValues() {
        assertFalse(validator.validate(new CreatePromptRequestDto("", "content", PromptVisibility.PRIVATE, null)).isEmpty());
        assertFalse(validator.validate(new CreatePromptRequestDto("Title", "x".repeat(256), PromptVisibility.PRIVATE, null)).isEmpty());
        assertFalse(validator.validate(new CreatePromptRequestDto("Title", "content", null, null)).isEmpty());
    }

    @Test
    void messageRejectsBlankAndOversizedContent() {
        assertFalse(validator.validate(new SendMessageRequestDto("\n\t")).isEmpty());
        assertFalse(validator.validate(new SendMessageRequestDto("x".repeat(256))).isEmpty());
    }

    @Test
    void loginRejectsMalformedEmailAndOversizedPassword() {
        assertFalse(validator.validate(new LoginRequestDto("not-an-email", "password")).isEmpty());
        assertFalse(validator.validate(new LoginRequestDto("user@example.com", "x".repeat(65))).isEmpty());
    }
}
