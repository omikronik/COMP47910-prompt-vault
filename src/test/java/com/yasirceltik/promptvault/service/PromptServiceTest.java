package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.yasirceltik.promptvault.dto.CreatePromptRequestDto;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptCategory;
import com.yasirceltik.promptvault.model.PromptPolicyMatch;
import com.yasirceltik.promptvault.model.PromptVisibility;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.ConversationRepository;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
import com.yasirceltik.promptvault.repository.PromptPolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;
import com.yasirceltik.promptvault.exception.PromptNotFoundException;

class PromptServiceTest {

    private PromptRepository promptRepository;
    private PromptCategoryRepository promptCategoryRepository;
    private PolicyKeywordRepository policyKeywordRepository;
    private PromptPolicyMatchRepository promptPolicyMatchRepository;
    private ConversationRepository conversationRepository;

    private PromptService promptService;

    private User owner;
    private User otherUser;
    private User admin;

    @BeforeEach
    void setUp() {
        promptRepository = mock(PromptRepository.class);
        promptCategoryRepository =
                mock(PromptCategoryRepository.class);
        policyKeywordRepository =
                mock(PolicyKeywordRepository.class);
        promptPolicyMatchRepository =
                mock(PromptPolicyMatchRepository.class);
        conversationRepository =
                mock(ConversationRepository.class);

        promptService = new PromptService(
                        promptRepository,
                        promptCategoryRepository,
                        policyKeywordRepository,
                        promptPolicyMatchRepository,
                        conversationRepository
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

        admin = User.builder()
                .id(3L)
                .username("admin")
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .active(true)
                .build();
    }

    @Test
    void getPromptsForUserReturnsOnlyUsersPrompts() {
            Prompt first = prompt(10L, owner, "First");
            Prompt second = prompt(11L, owner, "Second");

            when(promptRepository.findAllByOwner(owner))
                    .thenReturn(List.of(first, second));

            List<Prompt> result =
                    promptService.getPromptsForUser(owner);

            assertEquals(2, result.size());
            assertEquals(first, result.get(0));
            assertEquals(second, result.get(1));

            verify(promptRepository)
                    .findAllByOwner(owner);
    }

    @Test
    void getPromptByIdAndOwnerReturnsPromptForOwner() {
            Prompt prompt = prompt(
                            10L,
                            owner,
                            "Owner Prompt"
                            );

            when(promptRepository.findByIdAndOwner(10L, owner))
                    .thenReturn(Optional.of(prompt));

            Prompt result =
                    promptService.getPromptByIdAndOwner(
                                    10L,
                                    owner
                                    );

            assertEquals(prompt, result);

            verify(promptRepository)
                    .findByIdAndOwner(10L, owner);
    }

    @Test
    void getPromptByIdAndOwnerThrowsForDifferentUser() {
            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    otherUser
                                    )).thenReturn(Optional.empty());

            assertThrows(
                            PromptNotFoundException.class,
                            () -> promptService.getPromptByIdAndOwner(
                                                                      10L,
                                                                      otherUser
                                                                     )
                        );

            verify(promptRepository)
                    .findByIdAndOwner(
                                    10L,
                                    otherUser
                                    );
    }

    @Test
    void editPromptSucceedsForOwner() {
            Prompt existingPrompt = Prompt.builder()
                    .id(10L)
                    .title("Old Title")
                    .content("Old Content")
                    .visibility(PromptVisibility.PRIVATE)
                    .owner(owner)
                    .policyFlagged(false)
                    .build();

            PromptCategory category =
                    PromptCategory.builder()
                    .id(5L)
                    .name("Coding")
                    .description("Coding prompts")
                    .build();

            CreatePromptRequestDto dto =
                    new CreatePromptRequestDto(
                                    "Updated Title",
                                    "Updated Content",
                                    PromptVisibility.SHARED,
                                    5L
                                    );

            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    owner
                                    )).thenReturn(Optional.of(existingPrompt));

            when(promptCategoryRepository.findById(5L))
                    .thenReturn(Optional.of(category));

            when(policyKeywordRepository.findAll())
                    .thenReturn(List.of());

            promptService.editPrompt(
                            10L,
                            dto,
                            owner
                            );

            assertEquals(
                            "Updated Title",
                            existingPrompt.getTitle()
                        );

            assertEquals(
                            "Updated Content",
                            existingPrompt.getContent()
                        );

            assertEquals(
                            PromptVisibility.SHARED,
                            existingPrompt.getVisibility()
                        );

            assertEquals(
                            category,
                            existingPrompt.getCategory()
                        );

            assertFalse(existingPrompt.getPolicyFlagged());

            verify(promptRepository)
                    .findByIdAndOwner(
                                    10L,
                                    owner
                                    );

            verify(promptPolicyMatchRepository)
                    .deleteByPrompt(existingPrompt);
    }

    @Test
    void editPromptCannotEditAnotherUsersPrompt() {
            Prompt victimPrompt = Prompt.builder()
                    .id(10L)
                    .title("Victim Title")
                    .content("Victim Content")
                    .visibility(PromptVisibility.PRIVATE)
                    .owner(owner)
                    .policyFlagged(false)
                    .build();

            CreatePromptRequestDto dto =
                    new CreatePromptRequestDto(
                                    "Attacker Title",
                                    "Attacker Content",
                                    PromptVisibility.SHARED,
                                    null
                                    );

            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    otherUser
                                    )).thenReturn(Optional.empty());

            assertThrows(
                            PromptNotFoundException.class,
                            () -> promptService.editPrompt(
                                                           10L,
                                                           dto,
                                                           otherUser
                                                          )
                        );

            assertEquals(
                            "Victim Title",
                            victimPrompt.getTitle()
                        );

            assertEquals(
                            "Victim Content",
                            victimPrompt.getContent()
                        );

            assertEquals(
                            PromptVisibility.PRIVATE,
                            victimPrompt.getVisibility()
                        );

            verify(promptRepository)
                    .findByIdAndOwner(
                                    10L,
                                    otherUser
                                    );

            verify(promptPolicyMatchRepository, never())
                    .deleteByPrompt(any());

            verify(promptPolicyMatchRepository, never())
                    .saveAll(any());

            verify(promptRepository, never())
                    .save(any());
    }

    @Test
    void editPromptUpdatesPolicyFlagWhenKeywordMatches() {
            Prompt existingPrompt = Prompt.builder()
                    .id(10L)
                    .title("Old Title")
                    .content("Old Content")
                    .visibility(PromptVisibility.PRIVATE)
                    .owner(owner)
                    .policyFlagged(false)
                    .build();

            PolicyKeyword passwordKeyword =
                    PolicyKeyword.builder()
                    .id(1L)
                    .content("password")
                    .build();

            CreatePromptRequestDto dto =
                    new CreatePromptRequestDto(
                                    "Updated",
                                    "My password is secret",
                                    PromptVisibility.PRIVATE,
                                    null
                                    );

            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    owner
                                    )).thenReturn(Optional.of(existingPrompt));

            when(policyKeywordRepository.findAll())
                    .thenReturn(
                                    List.of(passwordKeyword)
                               );

            promptService.editPrompt(
                            10L,
                            dto,
                            owner
                            );

            assertTrue(existingPrompt.getPolicyFlagged());

            verify(promptPolicyMatchRepository)
                    .deleteByPrompt(existingPrompt);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<PromptPolicyMatch>> captor =
            ArgumentCaptor.forClass(List.class);

            verify(promptPolicyMatchRepository)
                    .saveAll(captor.capture());

            List<PromptPolicyMatch> matches =
                    captor.getValue();

            assertEquals(1, matches.size());

            assertEquals(
                            existingPrompt,
                            matches.get(0).getPrompt()
                        );

            assertEquals(
                            passwordKeyword,
                            matches.get(0).getPolicy()
                        );
    }

    @Test
    void deletePromptSucceedsForOwner() {
            Prompt existingPrompt = prompt(
                            10L,
                            owner,
                            "Owner Prompt"
                            );

            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    owner
                                    )).thenReturn(Optional.of(existingPrompt));

            promptService.deletePrompt(
                            10L,
                            owner
                            );

            verify(promptRepository)
                    .findByIdAndOwner(
                                    10L,
                                    owner
                                    );

            verify(conversationRepository)
                    .nullifyPromptReferences(10L);

            verify(promptPolicyMatchRepository)
                    .deleteByPrompt(existingPrompt);

            verify(promptRepository)
                    .deleteById(10L);
    }

    @Test
    void deletePromptCannotDeleteAnotherUsersPrompt() {
            when(promptRepository.findByIdAndOwner(
                                    10L,
                                    otherUser
                                    )).thenReturn(Optional.empty());

            assertThrows(
                            PromptNotFoundException.class,
                            () -> promptService.deletePrompt(
                                                             10L,
                                                             otherUser
                                                            )
                        );

            verify(promptRepository)
                    .findByIdAndOwner(
                                    10L,
                                    otherUser
                                    );

            verify(conversationRepository, never())
                    .nullifyPromptReferences(anyLong());

            verify(promptPolicyMatchRepository, never())
                    .deleteByPrompt(any());

            verify(promptRepository, never())
                    .deleteById(anyLong());
    }
    @Test
    void adminCanDeletePromptWithoutOwningIt() {
            Prompt existingPrompt = prompt(
                            10L,
                            owner,
                            "Owner Prompt"
                            );

            when(promptRepository.findById(10L))
                    .thenReturn(
                                    Optional.of(existingPrompt)
                               );

            promptService.deletePrompt(
                            10L,
                            admin
                            );

            verify(promptRepository)
                    .findById(10L);

            verify(conversationRepository)
                    .nullifyPromptReferences(10L);

            verify(promptPolicyMatchRepository)
                    .deleteByPrompt(existingPrompt);

            verify(promptRepository)
                    .deleteById(10L);
    }

    @Test
    void createPromptSetsAuthenticatedUserAsOwner() {
            CreatePromptRequestDto dto =
                    new CreatePromptRequestDto(
                                    "New Prompt",
                                    "Prompt content",
                                    PromptVisibility.PRIVATE,
                                    null
                                    );

            when(policyKeywordRepository.findAll())
                    .thenReturn(List.of());

            promptService.createPrompt(
                            dto,
                            owner
                            );

            ArgumentCaptor<Prompt> captor =
                    ArgumentCaptor.forClass(Prompt.class);

            verify(promptRepository)
                    .save(captor.capture());

            Prompt savedPrompt =
                    captor.getValue();

            assertEquals(
                            owner,
                            savedPrompt.getOwner()
                        );

            assertEquals(
                            "New Prompt",
                            savedPrompt.getTitle()
                        );

            assertEquals(
                            "Prompt content",
                            savedPrompt.getContent()
                        );

            assertEquals(
                            PromptVisibility.PRIVATE,
                            savedPrompt.getVisibility()
                        );

            assertFalse(savedPrompt.getPolicyFlagged());
    }

    private Prompt prompt(
                    long id,
                    User owner,
                    String title) {

            return Prompt.builder()
                    .id(id)
                    .title(title)
                    .content("Content")
                    .owner(owner)
                    .visibility(PromptVisibility.PRIVATE)
                    .policyFlagged(false)
                    .build();
                    }
}
