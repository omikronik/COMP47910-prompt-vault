package com.yasirceltik.promptvault.dto;

import java.time.LocalDateTime;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.LoginStatus;

public record LoginResultDto(
        LoginStatus status,
        User user,
        LocalDateTime lockedUntil
) {

    public static LoginResultDto success(
            User user) {

        return new LoginResultDto(
                LoginStatus.SUCCESS,
                user,
                null
        );
    }

    public static LoginResultDto invalidCredentials() {
        return new LoginResultDto(
                LoginStatus.INVALID_CREDENTIALS,
                null,
                null
        );
    }

    public static LoginResultDto locked(
            LocalDateTime lockedUntil) {

        return new LoginResultDto(
                LoginStatus.LOCKED,
                null,
                lockedUntil
        );
    }
}
