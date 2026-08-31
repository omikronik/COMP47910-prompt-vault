package com.yasirceltik.promptvault.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RegisterRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    void blankPasswordIsRejected() {
        RegisterRequestDto request =
                validRequestWithPassword("");

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );
    }

    @Test
    void passwordShorterThanTwelveCharactersIsRejected() {
        RegisterRequestDto request =
                validRequestWithPassword("Short!Aa");

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getMessage().equals(
                                        "Password must be between 12 and 64 characters."
                                )
                        )
        );
    }

    @Test
    void passwordLongerThanSixtyFourCharactersIsRejected() {
        String password =
                "A!" + "a".repeat(63);

        RegisterRequestDto request =
                validRequestWithPassword(password);

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getMessage().equals(
                                        "Password must be between 12 and 64 characters."
                                )
                        )
        );
    }

    @Test
    void passwordWithoutUppercaseLetterIsRejected() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "lowercase-only!"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getMessage().equals(
                                        "Password must contain at least one uppercase letter."
                                )
                        )
        );
    }

    @Test
    void passwordWithoutLowercaseLetterIsRejected() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "UPPERCASE-ONLY!"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getMessage().equals(
                                        "Password must contain at least one lowercase letter."
                                )
                        )
        );
    }

    @Test
    void passwordWithoutSpecialCharacterIsRejected() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "ValidPassword123"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getMessage().equals(
                                        "Password must contain at least one special character."
                                )
                        )
        );
    }

    @Test
    void validPasswordPassesValidation() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "CorrectHorse!Battery"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );
    }

    @Test
    void unicodePasswordPassesValidation() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "Árvíztűrő!Password"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );
    }

    @Test
    void passwordAtMinimumLengthIsAccepted() {
        RegisterRequestDto request =
                validRequestWithPassword(
                        "Password!Abc"
                );

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );
    }

    @Test
    void passwordAtMaximumLengthIsAccepted() {
        String password =
                "A!" + "a".repeat(62);

        RegisterRequestDto request =
                validRequestWithPassword(password);

        Set<ConstraintViolation<RegisterRequestDto>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );
    }

    private RegisterRequestDto validRequestWithPassword(
            String password) {

        return new RegisterRequestDto(
                "John",
                "Smith",
                "johnsmith",
                "john@example.com",
                password
        );
    }
}
