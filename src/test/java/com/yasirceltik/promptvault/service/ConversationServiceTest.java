package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.yasirceltik.promptvault.exception.PromptNotFoundException;
import com.yasirceltik.promptvault.model.ChatRole;
import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;
import com.yasirceltik.promptvault.model.MessagePolicyMatch;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptVisibility;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.ConversationMessageRepository;
import com.yasirceltik.promptvault.repository.ConversationRepository;
import com.yasirceltik.promptvault.repository.MessagePolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;

class ConversationServiceTest {

    private ConversationRepository conversationRepository;
    private ConversationMessageRepository conversationMessageRepository;
    private PromptRepository promptRepository;
    private PolicyKeywordRepository policyKeywordRepository;
    private MessagePolicyMatchRepository messagePolicyMatchRepository;

    private ConversationService conversationService;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        conversationRepository =
                mock(ConversationRepository.class);

        conversationMessageRepository =
                mock(ConversationMessageRepository.class);

        promptRepository =
                mock(PromptRepository.class);

        policyKeywordRepository =
                mock(PolicyKeywordRepository.class);

        messagePolicyMatchRepository =
                mock(MessagePolicyMatchRepository.class);

        conversationService = new ConversationService(
                conversationRepository,
                conversationMessageRepository,
                promptRepository,
                policyKeywordRepository,
                messagePolicyMatchRepository
        );

        owner = User.builder()
                .id(1L)
                .username("owner")
                .email("owner@example.com")
                .role(UserRole.USER)
                .active(true)
                .build();

        otherUser = User.builder()
                .id(2L)
                .username("other")
                .email("other@example.com")
                .role(UserRole.USER)
                .active(true)
                .build();
    }

    @Test
    void createConversationWithOwnPrivatePromptSucceeds() {
        Prompt prompt = Prompt.builder()
                .id(10L)
                .title("Private Owner Prompt")
                .owner(owner)
                .visibility(PromptVisibility.PRIVATE)
                .build();

        when(promptRepository.findUsablePrompt(
                10L,
                owner.getId(),
                PromptVisibility.SHARED
        )).thenReturn(Optional.of(prompt));

        when(conversationRepository.save(
                any(Conversation.class)
        )).thenAnswer(invocation -> {
            Conversation conversation =
                    invocation.getArgument(0);

            conversation.setId(100L);

            return conversation;
        });

        Conversation result =
                conversationService.createConversation(
                        owner,
                        10L
                );

        assertEquals(
                "Private Owner Prompt",
                result.getTitle()
        );

        assertEquals(
                owner,
                result.getOwner()
        );

        assertEquals(
                owner,
                result.getCreatedBy()
        );

        assertEquals(
                prompt,
                result.getPrompt()
        );

        assertFalse(
                result.getPolicyFlagged()
        );

        verify(promptRepository)
                .findUsablePrompt(
                        10L,
                        owner.getId(),
                        PromptVisibility.SHARED
                );

        verify(conversationRepository)
                .save(any(Conversation.class));
    }

    @Test
    void createConversationWithAnotherUsersSharedPromptSucceeds() {
        Prompt sharedPrompt = Prompt.builder()
                .id(20L)
                .title("Shared Prompt")
                .owner(otherUser)
                .visibility(PromptVisibility.SHARED)
                .build();

        when(promptRepository.findUsablePrompt(
                20L,
                owner.getId(),
                PromptVisibility.SHARED
        )).thenReturn(Optional.of(sharedPrompt));

        when(conversationRepository.save(
                any(Conversation.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Conversation result =
                conversationService.createConversation(
                        owner,
                        20L
                );

        assertEquals(
                "Shared Prompt",
                result.getTitle()
        );

        assertEquals(
                sharedPrompt,
                result.getPrompt()
        );

        assertEquals(
                owner,
                result.getOwner()
        );

        verify(promptRepository)
                .findUsablePrompt(
                        20L,
                        owner.getId(),
                        PromptVisibility.SHARED
                );

        verify(conversationRepository)
                .save(any(Conversation.class));
    }

    @Test
    void createConversationWithAnotherUsersPrivatePromptIsDenied() {
        when(promptRepository.findUsablePrompt(
                20L,
                owner.getId(),
                PromptVisibility.SHARED
        )).thenReturn(Optional.empty());

        assertThrows(
                PromptNotFoundException.class,
                () ->
                        conversationService.createConversation(
                                owner,
                                20L
                        )
        );

        verify(promptRepository)
                .findUsablePrompt(
                        20L,
                        owner.getId(),
                        PromptVisibility.SHARED
                );

        verify(
                conversationRepository,
                never()
        ).save(any(Conversation.class));
    }

    @Test
    void createConversationWithMissingPromptIsDenied() {
        when(promptRepository.findUsablePrompt(
                999L,
                owner.getId(),
                PromptVisibility.SHARED
        )).thenReturn(Optional.empty());

        assertThrows(
                PromptNotFoundException.class,
                () ->
                        conversationService.createConversation(
                                owner,
                                999L
                        )
        );

        verify(promptRepository)
                .findUsablePrompt(
                        999L,
                        owner.getId(),
                        PromptVisibility.SHARED
                );

        verify(
                conversationRepository,
                never()
        ).save(any(Conversation.class));
    }

    @Test
    void createConversationWithNullPromptCreatesBlankConversation() {
        when(conversationRepository.save(
                any(Conversation.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Conversation result =
                conversationService.createConversation(
                        owner,
                        null
                );

        assertEquals(
                "New Conversation",
                result.getTitle()
        );

        assertNull(
                result.getPrompt()
        );

        assertEquals(
                owner,
                result.getOwner()
        );

        assertEquals(
                owner,
                result.getCreatedBy()
        );

        assertFalse(
                result.getPolicyFlagged()
        );

        verifyNoInteractions(
                promptRepository
        );

        verify(conversationRepository)
                .save(any(Conversation.class));
    }

    @Test
    void deniedPrivatePromptDoesNotCreateConversation() {
        when(promptRepository.findUsablePrompt(
                20L,
                owner.getId(),
                PromptVisibility.SHARED
        )).thenReturn(Optional.empty());

        assertThrows(
                PromptNotFoundException.class,
                () ->
                        conversationService.createConversation(
                                owner,
                                20L
                        )
        );

        verify(
                conversationRepository,
                never()
        ).save(any());

        verifyNoInteractions(
                conversationMessageRepository
        );

        verifyNoInteractions(
                messagePolicyMatchRepository
        );
    }

    @Test
    void sendMessageSucceedsForConversationOwner() {
        Conversation conversation =
                Conversation.builder()
                        .id(10L)
                        .title("Existing Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .policyFlagged(false)
                        .messages(new ArrayList<>())
                        .build();

        when(conversationRepository.findByIdAndOwnerId(
                10L,
                owner.getId()
        )).thenReturn(Optional.of(conversation));

        when(policyKeywordRepository.findAll())
                .thenReturn(List.of());

        conversationService.sendMessage(
                10L,
                "Hello",
                owner.getId()
        );

        verify(conversationRepository)
                .findByIdAndOwnerId(
                        10L,
                        owner.getId()
                );

        ArgumentCaptor<ConversationMessage> messageCaptor =
                ArgumentCaptor.forClass(
                        ConversationMessage.class
                );

        verify(
                conversationMessageRepository,
                times(2)
        ).save(messageCaptor.capture());

        List<ConversationMessage> savedMessages =
                messageCaptor.getAllValues();

        ConversationMessage userMessage =
                savedMessages.get(0);

        ConversationMessage agentMessage =
                savedMessages.get(1);

        assertEquals(
                "Hello",
                userMessage.getContent()
        );

        assertEquals(
                ChatRole.USER,
                userMessage.getRole()
        );

        assertFalse(
                userMessage.getPolicyFlagged()
        );

        assertEquals(
                ChatRole.AGENT,
                agentMessage.getRole()
        );

        assertFalse(
                agentMessage.getPolicyFlagged()
        );

        verifyNoInteractions(
                messagePolicyMatchRepository
        );
    }

    @Test
    void sendMessageCannotWriteToAnotherUsersConversation() {
        when(conversationRepository.findByIdAndOwnerId(
                10L,
                otherUser.getId()
        )).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () ->
                        conversationService.sendMessage(
                                10L,
                                "Injected message",
                                otherUser.getId()
                        )
        );

        verify(
                conversationMessageRepository,
                never()
        ).save(any());

        verify(
                messagePolicyMatchRepository,
                never()
        ).save(any());

        verifyNoInteractions(
                policyKeywordRepository
        );
    }

    @Test
    void sendMessageFlagsConversationWhenPolicyKeywordMatches() {
        Conversation conversation =
                Conversation.builder()
                        .id(10L)
                        .title("Existing Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .policyFlagged(false)
                        .messages(new ArrayList<>())
                        .build();

        PolicyKeyword keyword =
                PolicyKeyword.builder()
                        .id(1L)
                        .content("password")
                        .build();

        when(conversationRepository.findByIdAndOwnerId(
                10L,
                owner.getId()
        )).thenReturn(Optional.of(conversation));

        when(policyKeywordRepository.findAll())
                .thenReturn(List.of(keyword));

        conversationService.sendMessage(
                10L,
                "My password is secret",
                owner.getId()
        );

        assertTrue(
                conversation.getPolicyFlagged()
        );

        ArgumentCaptor<ConversationMessage> messageCaptor =
                ArgumentCaptor.forClass(
                        ConversationMessage.class
                );

        verify(
                conversationMessageRepository,
                times(2)
        ).save(messageCaptor.capture());

        ConversationMessage userMessage =
                messageCaptor
                        .getAllValues()
                        .get(0);

        assertTrue(
                userMessage.getPolicyFlagged()
        );

        ArgumentCaptor<MessagePolicyMatch> matchCaptor =
                ArgumentCaptor.forClass(
                        MessagePolicyMatch.class
                );

        verify(messagePolicyMatchRepository)
                .save(matchCaptor.capture());

        assertEquals(
                keyword,
                matchCaptor
                        .getValue()
                        .getPolicy()
        );
    }

    @Test
    void getConversationsForUserUsesOwnerScopedQuery() {
        Conversation first =
                Conversation.builder()
                        .id(10L)
                        .title("First")
                        .owner(owner)
                        .build();

        Conversation second =
                Conversation.builder()
                        .id(11L)
                        .title("Second")
                        .owner(owner)
                        .build();

        when(
                conversationRepository
                        .findByOwnerOrderByCreatedOnDesc(
                                owner
                        )
        ).thenReturn(
                List.of(
                        first,
                        second
                )
        );

        List<Conversation> result =
                conversationService
                        .getConversationsForUser(owner);

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                first,
                result.get(0)
        );

        assertEquals(
                second,
                result.get(1)
        );

        verify(conversationRepository)
                .findByOwnerOrderByCreatedOnDesc(
                        owner
                );
    }
}
