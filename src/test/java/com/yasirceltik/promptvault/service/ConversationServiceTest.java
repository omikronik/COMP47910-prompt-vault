package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.yasirceltik.promptvault.model.ChatRole;
import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;
import com.yasirceltik.promptvault.model.MessagePolicyMatch;
import com.yasirceltik.promptvault.model.PolicyKeyword;
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
    void sendMessageSucceedsForConversationOwner() {
        Conversation conversation = Conversation.builder()
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

        verify(conversationMessageRepository, times(2))
                .save(messageCaptor.capture());

        List<ConversationMessage> savedMessages =
                messageCaptor.getAllValues();

        ConversationMessage userMessage =
                savedMessages.get(0);

        ConversationMessage agentMessage =
                savedMessages.get(1);

        assertEquals(
                conversation,
                userMessage.getConversation()
        );

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
                conversation,
                agentMessage.getConversation()
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
                NoSuchElementException.class,
                () -> conversationService.sendMessage(
                        10L,
                        "Injected message",
                        otherUser.getId()
                )
        );

        verify(conversationRepository)
                .findByIdAndOwnerId(
                        10L,
                        otherUser.getId()
                );

        verifyNoInteractions(
                conversationMessageRepository
        );

        verifyNoInteractions(
                messagePolicyMatchRepository
        );

        verifyNoInteractions(
                policyKeywordRepository
        );
    }

    @Test
    void deniedMessageDoesNotChangeVictimConversationFlag() {
        Conversation victimConversation =
                Conversation.builder()
                        .id(10L)
                        .title("Victim Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .policyFlagged(false)
                        .messages(new ArrayList<>())
                        .build();

        when(conversationRepository.findByIdAndOwnerId(
                10L,
                otherUser.getId()
        )).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> conversationService.sendMessage(
                        10L,
                        "my password is secret",
                        otherUser.getId()
                )
        );

        assertFalse(
                victimConversation.getPolicyFlagged()
        );

        assertTrue(
                victimConversation
                        .getMessages()
                        .isEmpty()
        );

        verifyNoInteractions(
                conversationMessageRepository
        );

        verifyNoInteractions(
                messagePolicyMatchRepository
        );

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

        verify(conversationMessageRepository, times(2))
                .save(messageCaptor.capture());

        List<ConversationMessage> messages =
                messageCaptor.getAllValues();

        ConversationMessage userMessage =
                messages.get(0);

        ConversationMessage agentMessage =
                messages.get(1);

        assertTrue(
                userMessage.getPolicyFlagged()
        );

        assertEquals(
                ChatRole.USER,
                userMessage.getRole()
        );

        assertFalse(
                agentMessage.getPolicyFlagged()
        );

        assertEquals(
                ChatRole.AGENT,
                agentMessage.getRole()
        );

        ArgumentCaptor<MessagePolicyMatch> matchCaptor =
                ArgumentCaptor.forClass(
                        MessagePolicyMatch.class
                );

        verify(messagePolicyMatchRepository)
                .save(matchCaptor.capture());

        MessagePolicyMatch savedMatch =
                matchCaptor.getValue();

        assertEquals(
                userMessage,
                savedMatch.getMessage()
        );

        assertEquals(
                keyword,
                savedMatch.getPolicy()
        );
    }

    @Test
    void sendMessageCreatesOnePolicyMatchForEachMatchedKeyword() {
        Conversation conversation =
                Conversation.builder()
                        .id(10L)
                        .title("Existing Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .policyFlagged(false)
                        .messages(new ArrayList<>())
                        .build();

        PolicyKeyword password =
                PolicyKeyword.builder()
                        .id(1L)
                        .content("password")
                        .build();

        PolicyKeyword secret =
                PolicyKeyword.builder()
                        .id(2L)
                        .content("secret")
                        .build();

        when(conversationRepository.findByIdAndOwnerId(
                10L,
                owner.getId()
        )).thenReturn(Optional.of(conversation));

        when(policyKeywordRepository.findAll())
                .thenReturn(
                        List.of(
                                password,
                                secret
                        )
                );

        conversationService.sendMessage(
                10L,
                "My password is secret",
                owner.getId()
        );

        verify(
                messagePolicyMatchRepository,
                times(2)
        ).save(any(MessagePolicyMatch.class));

        assertTrue(
                conversation.getPolicyFlagged()
        );
    }

    @Test
    void firstMessageUpdatesNewConversationTitle() {
        Conversation conversation =
                Conversation.builder()
                        .id(10L)
                        .title("New Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .prompt(null)
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
                "This should become the conversation title",
                owner.getId()
        );

        assertEquals(
                "This should become the conversation title",
                conversation.getTitle()
        );
    }

    @Test
    void firstLongMessageTruncatesConversationTitle() {
        Conversation conversation =
                Conversation.builder()
                        .id(10L)
                        .title("New Conversation")
                        .owner(owner)
                        .createdBy(owner)
                        .prompt(null)
                        .policyFlagged(false)
                        .messages(new ArrayList<>())
                        .build();

        String content =
                "This is a very long message that should exceed sixty characters "
                        + "and therefore be truncated";

        when(conversationRepository.findByIdAndOwnerId(
                10L,
                owner.getId()
        )).thenReturn(Optional.of(conversation));

        when(policyKeywordRepository.findAll())
                .thenReturn(List.of());

        conversationService.sendMessage(
                10L,
                content,
                owner.getId()
        );

        assertEquals(
                content.substring(0, 60).trim() + "…",
                conversation.getTitle()
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
