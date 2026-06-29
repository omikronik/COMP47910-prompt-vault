package com.yasirceltik.promptvault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.model.ChatRole;
import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;
import com.yasirceltik.promptvault.model.MessagePolicyMatch;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.ConversationMessageRepository;
import com.yasirceltik.promptvault.repository.ConversationRepository;
import com.yasirceltik.promptvault.repository.MessagePolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConversationService {
	private final ConversationRepository conversationRepository;
	private final ConversationMessageRepository conversationMessageRepository;
	private final PromptRepository promptRepository;
	private final PolicyKeywordRepository policyKeywordRepository;
	private final MessagePolicyMatchRepository messagePolicyMatchRepository;

	public List<Conversation> getConversationsForUser(User user) {
		return conversationRepository.findByOwnerOrderByCreatedOnDesc(user);
	}

	public Optional<Conversation> getConversationById(Long id) {
		return conversationRepository.findById(id);
	}

	public List<ConversationMessage> getMessages(Long conversationId) {
		Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();
		return conversationMessageRepository.findByConversationOrderByCreatedOnAsc(conversation);
	}

	public List<Conversation> getFlaggedConversations() {
		return conversationRepository.findByPolicyFlaggedTrue();
	}

	@Transactional
	public Conversation createConversation(User user, Long promptId) {
		Prompt prompt = null;
		String title = "New Conversation";
		if (promptId != null) {
			prompt = promptRepository.findById(promptId).orElse(null);
			if (prompt != null) {
				title = prompt.getTitle();
			}
		}
		Conversation conversation = Conversation.builder()
				.title(title)
				.owner(user)
				.createdBy(user)
				.prompt(prompt)
				.policyFlagged(false)
				.build();
		Conversation saved = conversationRepository.save(conversation);
		log.info("created conversation '{}' for user {}", saved.getTitle(), user.getId());
		return saved;
	}

	@Transactional
	public void sendMessage(Long conversationId, String content, User user) {
		Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();

		List<PolicyKeyword> keywords = policyKeywordRepository.findAll();
		String contentLower = content.toLowerCase();
		List<PolicyKeyword> matches = keywords.stream()
				.filter(kw -> contentLower.contains(kw.getContent().toLowerCase()))
				.toList();

		if (conversation.getPrompt() == null && conversation.getMessages().isEmpty()) {
			String title = content.length() > 60 ? content.substring(0, 60).trim() + "…" : content;
			conversation.setTitle(title);
		}

		boolean flagged = !matches.isEmpty();

		ConversationMessage userMsg = ConversationMessage.builder()
				.conversation(conversation)
				.content(content)
				.role(ChatRole.USER)
				.policyFlagged(flagged)
				.build();
		conversationMessageRepository.save(userMsg);

		if (flagged) {
			conversation.setPolicyFlagged(true);
			for (PolicyKeyword kw : matches) {
				messagePolicyMatchRepository.save(MessagePolicyMatch.builder()
						.message(userMsg)
						.policy(kw)
						.build());
			}
			log.info("message in conversation {} flagged for {} keyword(s)", conversationId, matches.size());
		}

		ConversationMessage aiMsg = ConversationMessage.builder()
				.conversation(conversation)
				.content("I am a robot. beep. boop.")
				.role(ChatRole.AGENT)
				.policyFlagged(false)
				.build();
		conversationMessageRepository.save(aiMsg);
	}

	@Transactional
	public void deleteConversation(Long id, User user) {
		Conversation conversation = conversationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));

		if (conversation.getOwner().getId() != user.getId()) {
			throw new RuntimeException(user.getId() + " attempted to delete conversation not owned");
		}

		conversationRepository.delete(conversation);
	}

	@Transactional
	public void adminDeleteConversation(Long id) {
		messagePolicyMatchRepository.deleteByConversationId(id);
		conversationRepository.deleteById(id);
	}
}
