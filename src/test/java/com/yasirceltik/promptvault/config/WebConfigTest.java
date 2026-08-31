package com.yasirceltik.promptvault.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.yasirceltik.promptvault.controller.AuthController;
import com.yasirceltik.promptvault.service.AuthService;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionRegistryService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, WebConfig.class, AuthInterceptor.class})
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private SessionRegistryService sessionRegistryService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void faviconRequestDoesNotRedirectOrInvalidateAnonymousSession() throws Exception {
        HttpSession session = mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(get("/favicon.ico").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isNotFound());
    }
}
