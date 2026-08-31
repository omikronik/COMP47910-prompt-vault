package com.yasirceltik.promptvault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;

import com.yasirceltik.promptvault.controller.admin.AdminPolicyKeywordController;
import com.yasirceltik.promptvault.dto.CreatePolicyKeywordDto;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.PolicyKeywordService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;

class AdminPolicyKeywordControllerTest {

    @Test
    void invalidKeywordIsNotPersisted() {
        SessionService sessionService = mock(SessionService.class);
        PolicyKeywordService keywordService = mock(PolicyKeywordService.class);
        HttpSession session = mock(HttpSession.class);
        BindingResult bindingResult = mock(BindingResult.class);

        User admin = User.builder().role(UserRole.ADMIN).build();
        when(sessionService.getCurrentUser(session)).thenReturn(admin);
        when(bindingResult.hasErrors()).thenReturn(true);

        AdminPolicyKeywordController controller =
                new AdminPolicyKeywordController(sessionService, keywordService);

        String view = controller.createKeyword(
                session,
                new CreatePolicyKeywordDto(""),
                bindingResult);

        assertEquals("admin/policy-keywords/create", view);
        verifyNoInteractions(keywordService);
    }
}
