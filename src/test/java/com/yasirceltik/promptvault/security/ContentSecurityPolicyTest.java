package com.yasirceltik.promptvault.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.yasirceltik.promptvault.config.SecurityConfig;
import com.yasirceltik.promptvault.controller.AuthController;
import com.yasirceltik.promptvault.service.AuthService;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionRegistryService;
import com.yasirceltik.promptvault.service.SessionService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class ContentSecurityPolicyTest {

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
    void publicResponseHasEnforcedCsp() throws Exception {
        assertCsp(get("/auth/login"));
    }

    @Test
    void authenticatedAndAdminPathsHaveEnforcedCsp() throws Exception {
        assertCsp(get("/dashboard"));
        assertCsp(get("/admin/categories"));
    }

    @Test
    void errorResponseHasEnforcedCsp() throws Exception {
        assertCsp(post("/auth/register"));
    }

    @Test
    void staticScriptResponseHasEnforcedCsp() throws Exception {
        assertCsp(get("/js/app.js"));
    }

    private void assertCsp(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request)
            throws Exception {
        mockMvc.perform(request)
                .andExpect(header().string(
                        "Content-Security-Policy",
                        SecurityConfig.CONTENT_SECURITY_POLICY));
    }
}
