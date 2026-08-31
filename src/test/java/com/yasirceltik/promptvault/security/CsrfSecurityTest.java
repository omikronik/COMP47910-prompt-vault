package com.yasirceltik.promptvault.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.yasirceltik.promptvault.controller.ConversationController;
import com.yasirceltik.promptvault.config.SecurityConfig;
import com.yasirceltik.promptvault.dto.SessionUserDto;
import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.service.ConversationService;
import com.yasirceltik.promptvault.service.SessionService;

import jakarta.servlet.http.HttpSession;

@WebMvcTest(ConversationController.class)
@Import(SecurityConfig.class)
class CsrfSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ConversationService conversationService;

	@MockitoBean
	private SessionService sessionService;

	@Test
	void postWithoutCsrfTokenIsRejected() throws Exception {
		mockMvc.perform(post("/conversations/start"))
				.andExpect(status().isForbidden());

		verifyNoInteractions(conversationService, sessionService);
	}

	@Test
	void postWithInvalidCsrfTokenIsRejected() throws Exception {
		mockMvc.perform(post("/conversations/start").with(csrf().useInvalidToken()))
				.andExpect(status().isForbidden());

		verifyNoInteractions(conversationService, sessionService);
	}

	@Test
	void postWithValidSessionAndCsrfTokenExecutesController() throws Exception {
		User user = User.builder()
				.id(1L)
				.username("owner")
				.email("owner@example.com")
				.role(UserRole.USER)
				.active(true)
				.build();

		SessionUserDto principal = new SessionUserDto(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole());

		Conversation conversation = Conversation.builder()
				.id(42L)
				.owner(user)
				.title("New Conversation")
				.build();

		when(sessionService.getCurrentUser(any(HttpSession.class))).thenReturn(user);
		when(conversationService.createConversation(user, null)).thenReturn(conversation);

		mockMvc.perform(post("/conversations/start")
				.sessionAttr("user", principal)
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/conversations/42"));

		verify(conversationService).createConversation(user, null);
	}
}
