package com.yasirceltik.promptvault.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.dto.CreatePromptRequestDto;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptCategory;
import com.yasirceltik.promptvault.model.PromptVisibility;
import com.yasirceltik.promptvault.model.PromptPolicyMatch;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.ConversationRepository;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
import com.yasirceltik.promptvault.repository.PromptPolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptService {
	private final PromptRepository promptRepository;
	private final PromptCategoryRepository promptCategoryRepository;
	private final PolicyKeywordRepository policyKeywordRepository;
	private final PromptPolicyMatchRepository promptPolicyMatchRepository;
	private final ConversationRepository conversationRepository;

	public List<Prompt> getPromptsForUser(User user) {
		List<Prompt> prompts = promptRepository.findAllByOwner(user);
		log.info("retrieved {} prompts for user {}", prompts.size(), user.getId());
		return prompts;
	}

	public List<PromptCategory> getAllCategories() {
		return promptCategoryRepository.findAll();
	}

	public Optional<Prompt> getPromptById(Long id) {
		return promptRepository.findById(id);
	}

	public List<Prompt> getSharedPrompts() {
		return promptRepository.findAllByVisibility(PromptVisibility.SHARED);
	}

	public List<PromptPolicyMatch> getFlaggedPromptMatches() {
		return promptPolicyMatchRepository.findAll();
	}

	@Transactional
	public void createPrompt(CreatePromptRequestDto dto, User user) {
		PromptCategory category = null;
		if (dto.categoryId() != null) {
			category = promptCategoryRepository.findById(dto.categoryId()).orElse(null);
		}

		List<PolicyKeyword> keywords = policyKeywordRepository.findAll();
		String contentLowercase = dto.content().toLowerCase();

		List<PolicyKeyword> matches = keywords.stream()
				.filter(kw -> contentLowercase.contains(kw.getContent().toLowerCase()))
				.toList();

		Prompt prompt = Prompt.builder()
				.title(dto.title())
				.content(dto.content())
				.visibility(dto.visibility())
				.owner(user)
				.category(category)
				.policyFlagged(!matches.isEmpty())
				.build();

		promptRepository.save(prompt);

		if (!matches.isEmpty()) {
			List<PromptPolicyMatch> ppmList = new ArrayList<>();
			for (PolicyKeyword match : matches) {
				ppmList.add(PromptPolicyMatch.builder()
						.prompt(prompt)
						.policy(match)
						.build());

			}
			promptPolicyMatchRepository.saveAll(ppmList);
		}

		log.info("created prompt '{}' for user {}", prompt.getTitle(), user.getId());
	}

	@Transactional
	public void editPrompt(Long id, CreatePromptRequestDto dto, User user) {
		PromptCategory category = null;
		if (dto.categoryId() != null) {
			category = promptCategoryRepository.findById(dto.categoryId()).orElse(null);
		}

		List<PolicyKeyword> keywords = policyKeywordRepository.findAll();
		String contentLowercase = dto.content().toLowerCase();

		List<PolicyKeyword> matches = keywords.stream()
				.filter(kw -> contentLowercase.contains(kw.getContent().toLowerCase()))
				.toList();

		Prompt prompt = promptRepository.findById(id).orElseThrow();
		prompt.setTitle(dto.title());
		prompt.setContent(dto.content());
		prompt.setVisibility(dto.visibility());
		prompt.setCategory(category);
		prompt.setPolicyFlagged(!matches.isEmpty());

		promptPolicyMatchRepository.deleteByPrompt(prompt);
		if (!matches.isEmpty()) {
			List<PromptPolicyMatch> ppmList = new ArrayList<>();
			for (PolicyKeyword match : matches) {
				ppmList.add(PromptPolicyMatch.builder()
						.prompt(prompt)
						.policy(match)
						.build());
			}
			promptPolicyMatchRepository.saveAll(ppmList);
		}

		log.info("edited prompt '{}' for user {}", prompt.getTitle(), user.getId());
	}

	@Transactional
	public void deletePrompt(long id, User user) {
		Prompt prompt = getPromptById(id).orElseThrow();

		boolean isOwner = prompt.getOwner().getId() == user.getId();
		boolean isAdmin = user.getRole() == UserRole.ADMIN;
		if (!isOwner && !isAdmin) {
			return;
		}

		conversationRepository.nullifyPromptReferences(id);
		promptPolicyMatchRepository.deleteByPrompt(prompt);
		promptRepository.deleteById(id);
	}
}
