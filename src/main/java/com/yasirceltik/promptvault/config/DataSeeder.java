package com.yasirceltik.promptvault.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.yasirceltik.promptvault.model.ChatRole;
import com.yasirceltik.promptvault.model.Conversation;
import com.yasirceltik.promptvault.model.ConversationMessage;
import com.yasirceltik.promptvault.model.MessagePolicyMatch;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptCategory;
import com.yasirceltik.promptvault.model.PromptPolicyMatch;
import com.yasirceltik.promptvault.model.PromptVisibility;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.ConversationMessageRepository;
import com.yasirceltik.promptvault.repository.ConversationRepository;
import com.yasirceltik.promptvault.repository.MessagePolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
import com.yasirceltik.promptvault.repository.PromptPolicyMatchRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;
import com.yasirceltik.promptvault.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PromptCategoryRepository promptCategoryRepository;
	private final PromptRepository promptRepository;
	private final PolicyKeywordRepository policyKeywordRepository;
	private final PromptPolicyMatchRepository promptPolicyMatchRepository;
	private final ConversationRepository conversationRepository;
	private final ConversationMessageRepository conversationMessageRepository;
	private final MessagePolicyMatchRepository messagePolicyMatchRepository;

	public DataSeeder(
			UserRepository userRepository,
			PromptCategoryRepository promptCategoryRepository,
			PromptRepository promptRepository,
			PolicyKeywordRepository policyKeywordRepository,
			PromptPolicyMatchRepository promptPolicyMatchRepository,
			ConversationRepository conversationRepository,
			ConversationMessageRepository conversationMessageRepository,
			MessagePolicyMatchRepository messagePolicyMatchRepository) {
		this.userRepository = userRepository;
		this.promptCategoryRepository = promptCategoryRepository;
		this.promptRepository = promptRepository;
		this.policyKeywordRepository = policyKeywordRepository;
		this.promptPolicyMatchRepository = promptPolicyMatchRepository;
		this.conversationRepository = conversationRepository;
		this.conversationMessageRepository = conversationMessageRepository;
		this.messagePolicyMatchRepository = messagePolicyMatchRepository;
	}

	@Override
	public void run(String... args) {
		log.info("====== STARTING DATASEEDER ======");
		SeedUsers();
		SeedCategories();
		SeedPolicyKeywords();
		SeedPrompts();
		SeedFlaggedPrompts();
		SeedConversations();
		SeedFlaggedConversations();
		log.info("====== FINISHED DATASEEDER ======");
	}

	// -------------------------------------------------------------------------

	private void SeedUsers() {
		log.info("---- STARTING SEEDING USERS ----");

		List<User> users = List.of(
				User.builder()
						.email("admin@promptvault.com")
						.password("Test1234")
						.role(UserRole.ADMIN)
						.active(true)
						.build(),
				User.builder()
						.firstName("John")
						.lastName("Testificate")
						.username("johndoe")
						.email("john@promptvault.com")
						.password("Test1234")
						.role(UserRole.USER)
						.active(true)
						.build(),
				User.builder()
						.firstName("Jane")
						.lastName("Testificate")
						.username("janedoe")
						.email("jane@promptvault.com")
						.password("Test1234")
						.role(UserRole.USER)
						.active(true)
						.build(),
				User.builder()
						.firstName("Alice")
						.lastName("Example")
						.username("alice")
						.email("alice@promptvault.com")
						.password("Test1234")
						.role(UserRole.USER)
						.active(false)
						.build());

		List<User> toSave = users.stream()
				.filter(u -> userRepository.findByEmail(u.getEmail()).isEmpty())
				.toList();
		userRepository.saveAll(toSave);
		log.info("---- FINISHED SEEDING USERS ----");
	}

	// -------------------------------------------------------------------------

	private void SeedCategories() {
		log.info("---- STARTING SEEDING CATEGORIES ----");

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();

		List<PromptCategory> categories = List.of(
				PromptCategory.builder()
						.name("Programming")
						.description("Prompts about software development and coding.")
						.createdBy(admin)
						.build(),
				PromptCategory.builder()
						.name("Cybersecurity")
						.description("Prompts about security topics, vulnerabilities, and best practices.")
						.createdBy(admin)
						.build(),
				PromptCategory.builder()
						.name("Research")
						.description("Prompts for research, analysis, and summarisation.")
						.createdBy(admin)
						.build(),
				PromptCategory.builder()
						.name("Creative")
						.description("Prompts for creative writing, art, and storytelling.")
						.createdBy(admin)
						.build(),
				PromptCategory.builder()
						.name("Productivity")
						.description("Prompts for planning, task management, and personal productivity.")
						.createdBy(admin)
						.build());

		List<PromptCategory> toSave = categories.stream()
				.filter(c -> promptCategoryRepository.findByName(c.getName()).isEmpty())
				.toList();
		promptCategoryRepository.saveAll(toSave);
		log.info("---- FINISHED SEEDING CATEGORIES ----");
	}

	// -------------------------------------------------------------------------

	private void SeedPolicyKeywords() {
		log.info("---- STARTING SEEDING POLICY KEYWORDS ----");

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();

		List<PolicyKeyword> keywords = List.of(
				PolicyKeyword.builder().content("password").createdBy(admin).build(),
				PolicyKeyword.builder().content("API key").createdBy(admin).build(),
				PolicyKeyword.builder().content("secret").createdBy(admin).build(),
				PolicyKeyword.builder().content("confidential").createdBy(admin).build(),
				PolicyKeyword.builder().content("credit card").createdBy(admin).build());

		List<PolicyKeyword> toSave = keywords.stream()
				.filter(k -> policyKeywordRepository.findByContent(k.getContent()).isEmpty())
				.toList();
		policyKeywordRepository.saveAll(toSave);
		log.info("---- FINISHED SEEDING POLICY KEYWORDS ----");
	}

	// -------------------------------------------------------------------------

	private void SeedPrompts() {
		log.info("---- STARTING SEEDING PROMPTS ----");

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();
		User john = userRepository.findByEmail("john@promptvault.com").orElseThrow();
		User jane = userRepository.findByEmail("jane@promptvault.com").orElseThrow();

		PromptCategory programming = promptCategoryRepository.findByName("Programming").orElseThrow();
		PromptCategory creative = promptCategoryRepository.findByName("Creative").orElseThrow();
		PromptCategory productivity = promptCategoryRepository.findByName("Productivity").orElseThrow();
		PromptCategory research = promptCategoryRepository.findByName("Research").orElseThrow();

		List<Prompt> prompts = List.of(
				Prompt.builder()
						.title("Apple Cutting Coach")
						.content(
								"You are an expert chef. Instruct the user step by step on how to cut an apple safely and efficiently.")
						.category(creative)
						.owner(admin)
						.visibility(PromptVisibility.PRIVATE)
						.build(),
				Prompt.builder()
						.title("DoorDash for Dogs")
						.content(
								"You are a 10x software architect. I have a billion dollar idea: DoorDash but the delivery drivers are dogs with tiny backpacks. Help me plan the MVP.")
						.category(programming)
						.owner(john)
						.visibility(PromptVisibility.SHARED)
						.build(),
				Prompt.builder()
						.title("Fitness and Workout Assistant")
						.content(
								"You are a certified personal trainer. Help the user understand proper form, programming, and recovery for beginner lifters.")
						.category(creative)
						.owner(john)
						.visibility(PromptVisibility.SHARED)
						.build(),
				Prompt.builder()
						.title("Research Summariser")
						.content(
								"You are an academic assistant. Summarise the provided research text into clear, concise bullet points with key findings highlighted.")
						.category(research)
						.owner(jane)
						.visibility(PromptVisibility.SHARED)
						.build(),
				Prompt.builder()
						.title("Daily Planning Assistant")
						.content(
								"You are a productivity coach. Take the user's task list and turn it into a structured daily plan with time blocks and priorities.")
						.category(productivity)
						.owner(jane)
						.visibility(PromptVisibility.PRIVATE)
						.build());

		List<Prompt> toSave = prompts.stream()
				.filter(p -> promptRepository.findByTitle(p.getTitle()).isEmpty())
				.toList();
		promptRepository.saveAll(toSave);
		log.info("---- FINISHED SEEDING PROMPTS ----");
	}

	// -------------------------------------------------------------------------

	private void SeedFlaggedPrompts() {
		log.info("---- STARTING SEEDING FLAGGED PROMPTS ----");

		User john = userRepository.findByEmail("john@promptvault.com").orElseThrow();
		User jane = userRepository.findByEmail("jane@promptvault.com").orElseThrow();

		PromptCategory cybersecurity = promptCategoryRepository.findByName("Cybersecurity").orElseThrow();

		PolicyKeyword kwPassword = policyKeywordRepository.findByContent("password").orElseThrow();
		PolicyKeyword kwApiKey = policyKeywordRepository.findByContent("API key").orElseThrow();
		PolicyKeyword kwSecret = policyKeywordRepository.findByContent("secret").orElseThrow();

		List<Prompt> flaggedPrompts = List.of(
				Prompt.builder()
						.title("Secure Password Storage Review")
						.content(
								"Review whether storing a hashed password in a plain text file is a safe practice and suggest alternatives.")
						.category(cybersecurity)
						.owner(john)
						.visibility(PromptVisibility.PRIVATE)
						.policyFlagged(true)
						.build(),
				Prompt.builder()
						.title("API Key Rotation Guide")
						.content(
								"Explain the best practices for rotating an API key without causing downtime in a production environment.")
						.category(cybersecurity)
						.owner(jane)
						.visibility(PromptVisibility.PRIVATE)
						.policyFlagged(true)
						.build(),
				Prompt.builder()
						.title("Secret Management Audit")
						.content(
								"Identify whether storing a secret directly in source code is acceptable and provide safer alternatives.")
						.category(cybersecurity)
						.owner(john)
						.visibility(PromptVisibility.PRIVATE)
						.policyFlagged(true)
						.build());

		List<Prompt> toSave = flaggedPrompts.stream()
				.filter(p -> promptRepository.findByTitle(p.getTitle()).isEmpty())
				.toList();
		promptRepository.saveAll(toSave);

		if (!toSave.isEmpty()) {
			Prompt passwordPrompt = promptRepository.findByTitle("Secure Password Storage Review").orElseThrow();
			Prompt apiKeyPrompt = promptRepository.findByTitle("API Key Rotation Guide").orElseThrow();
			Prompt secretPrompt = promptRepository.findByTitle("Secret Management Audit").orElseThrow();

			promptPolicyMatchRepository.saveAll(List.of(
					PromptPolicyMatch.builder().prompt(passwordPrompt).policy(kwPassword).build(),
					PromptPolicyMatch.builder().prompt(apiKeyPrompt).policy(kwApiKey).build(),
					PromptPolicyMatch.builder().prompt(secretPrompt).policy(kwSecret).build()));
		}

		log.info("---- FINISHED SEEDING FLAGGED PROMPTS ----");
	}

	// -------------------------------------------------------------------------

	private void SeedConversations() {
		log.info("---- STARTING SEEDING CONVERSATIONS ----");

		if (conversationRepository.count() > 0) {
			log.info("---- SKIPPING SEEDING CONVERSATIONS (already seeded) ----");
			return;
		}

		User john = userRepository.findByEmail("john@promptvault.com").orElseThrow();
		User jane = userRepository.findByEmail("jane@promptvault.com").orElseThrow();

		Prompt fitnessPrompt = promptRepository.findByTitle("Fitness and Workout Assistant").orElseThrow();
		Prompt researchPrompt = promptRepository.findByTitle("Research Summariser").orElseThrow();

		Conversation fitnessChat = conversationRepository.save(Conversation.builder()
				.title("Fitness and Workout Assistant")
				.prompt(fitnessPrompt)
				.owner(john)
				.createdBy(john)
				.updatedBy(john)
				.policyFlagged(false)
				.build());

		Conversation researchChat = conversationRepository.save(Conversation.builder()
				.title("Research Summariser")
				.prompt(researchPrompt)
				.owner(jane)
				.createdBy(jane)
				.updatedBy(jane)
				.policyFlagged(false)
				.build());

		Conversation freeChat = conversationRepository.save(Conversation.builder()
				.title("New Conversation")
				.owner(jane)
				.createdBy(jane)
				.updatedBy(jane)
				.policyFlagged(false)
				.build());

		conversationMessageRepository.saveAll(List.of(
				ConversationMessage.builder()
						.conversation(fitnessChat)
						.role(ChatRole.USER)
						.content("Can you explain proper squat form for a beginner?")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(fitnessChat)
						.role(ChatRole.AGENT)
						.content("I am a robot. beep. boop.")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(fitnessChat)
						.role(ChatRole.USER)
						.content("How many days a week should I train as a beginner?")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(fitnessChat)
						.role(ChatRole.AGENT)
						.content("I am a robot. beep. boop.")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(researchChat)
						.role(ChatRole.USER)
						.content("Summarise the key findings from this paper on neural networks.")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(researchChat)
						.role(ChatRole.AGENT)
						.content("I am a robot. beep. boop.")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(freeChat)
						.role(ChatRole.USER)
						.content("What is the best way to learn a new programming language?")
						.policyFlagged(false)
						.build(),
				ConversationMessage.builder()
						.conversation(freeChat)
						.role(ChatRole.AGENT)
						.content("I am a robot. beep. boop.")
						.policyFlagged(false)
						.build()));

		log.info("---- FINISHED SEEDING CONVERSATIONS ----");
	}

	// -------------------------------------------------------------------------

	private void SeedFlaggedConversations() {
		log.info("---- STARTING SEEDING FLAGGED CONVERSATIONS ----");

		if (!conversationRepository.findByPolicyFlaggedTrue().isEmpty()) {
			log.info("---- SKIPPING SEEDING FLAGGED CONVERSATIONS (already seeded) ----");
			return;
		}

		User john = userRepository.findByEmail("john@promptvault.com").orElseThrow();
		User jane = userRepository.findByEmail("jane@promptvault.com").orElseThrow();

		PolicyKeyword kwPassword = policyKeywordRepository.findByContent("password").orElseThrow();
		PolicyKeyword kwApiKey = policyKeywordRepository.findByContent("API key").orElseThrow();

		Conversation passwordChat = conversationRepository.save(Conversation.builder()
				.title("New Conversation")
				.owner(john)
				.createdBy(john)
				.updatedBy(john)
				.policyFlagged(true)
				.build());

		Conversation apiKeyChat = conversationRepository.save(Conversation.builder()
				.title("New Conversation")
				.owner(jane)
				.createdBy(jane)
				.updatedBy(jane)
				.policyFlagged(true)
				.build());

		ConversationMessage passwordMsg = conversationMessageRepository.save(
				ConversationMessage.builder()
						.conversation(passwordChat)
						.role(ChatRole.USER)
						.content("Is it safe to store a user's password in plain text in the database?")
						.policyFlagged(true)
						.build());

		conversationMessageRepository.save(ConversationMessage.builder()
				.conversation(passwordChat)
				.role(ChatRole.AGENT)
				.content("I am a robot. beep. boop.")
				.policyFlagged(false)
				.build());

		ConversationMessage apiKeyMsg = conversationMessageRepository.save(
				ConversationMessage.builder()
						.conversation(apiKeyChat)
						.role(ChatRole.USER)
						.content("What is the safest way to store an API key in a frontend application?")
						.policyFlagged(true)
						.build());

		conversationMessageRepository.save(ConversationMessage.builder()
				.conversation(apiKeyChat)
				.role(ChatRole.AGENT)
				.content("I am a robot. beep. boop.")
				.policyFlagged(false)
				.build());

		messagePolicyMatchRepository.saveAll(List.of(
				MessagePolicyMatch.builder().message(passwordMsg).policy(kwPassword).build(),
				MessagePolicyMatch.builder().message(apiKeyMsg).policy(kwApiKey).build()));

		log.info("---- FINISHED SEEDING FLAGGED CONVERSATIONS ----");
	}
}
