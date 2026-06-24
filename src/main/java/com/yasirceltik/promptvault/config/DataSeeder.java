package com.yasirceltik.promptvault.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.Prompt;
import com.yasirceltik.promptvault.model.PromptCategory;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.model.UserRole;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
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

	public DataSeeder(
			UserRepository userRepository,
			PromptCategoryRepository promptCategoryRepository,
			PromptRepository promptRepository,
			PolicyKeywordRepository policyKeywordRepository) {
		this.userRepository = userRepository;
		this.promptCategoryRepository = promptCategoryRepository;
		this.promptRepository = promptRepository;
		this.policyKeywordRepository = policyKeywordRepository;
	}

	@Override
	public void run(String... args) {
		log.info("====== STARTING DATASEEDER ======");
		SeedUsers();
		SeedCategories();
		SeedPolicyKeywords();
		SeedPrompts();
		log.info("====== FINISHED DATASEEDER ======");
	}

	private void SeedUsers() {
		log.info("---- STARTING SEEDING USERS ----");
		List<User> users = List.of(
				User.builder()
						.email("admin@promptvault.com")
						.password("Test1234")
						.role(UserRole.ADMIN)
						.isActive(true)
						.build(),
				User.builder()
						.email("johntestificate@bogus.com")
						.password("Test1234")
						.role(UserRole.USER)
						.isActive(true)
						.build(),
				User.builder()
						.email("janetestificate@wohoo.com")
						.password("Test1234")
						.role(UserRole.USER)
						.isActive(true)
						.build());

		List<User> usersToSave = users.stream()
				.filter(user -> userRepository.findByEmail(
						user.getEmail())
						.isEmpty())
				.toList();
		userRepository.saveAll(usersToSave);
		log.info("---- FINISHED SEEDING USERS ----");
	}

	private void SeedPolicyKeywords() {

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();

		log.info("---- STARTING SEEDING POLICYKEYWORDS ----");
		List<PolicyKeyword> keywords = List.of(
				PolicyKeyword.builder()
						.content("bomb")
						.createdBy(admin)
						.build(),
				PolicyKeyword.builder()
						.content("knife")
						.createdBy(admin)
						.build(),
				PolicyKeyword.builder()
						.content("gun")
						.createdBy(admin)
						.build(),
				PolicyKeyword.builder()
						.content("tank")
						.createdBy(admin)
						.build(),
				PolicyKeyword.builder()
						.content("nuke")
						.createdBy(admin)
						.build());

		List<PolicyKeyword> keywordsToSave = keywords.stream()
				.filter(keyword -> policyKeywordRepository.findByContent(
						keyword.getContent())
						.isEmpty())
				.toList();
		policyKeywordRepository.saveAll(keywordsToSave);
		log.info("---- FINISHED SEEDING POLICYKEYWORDS ----");
	}

	private void SeedCategories() {
		log.info("---- STARTING SEEDING CATEGORIES ----");

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();
		User john = userRepository.findByEmail("johntestificate@bogus.com").orElseThrow();
		User jane = userRepository.findByEmail("janetestificate@wohoo.com").orElseThrow();

		List<PromptCategory> categories = List.of(
				PromptCategory.builder()
						.name("programming")
						.description("prompts about programming")
						.createdBy(admin)
						.build(),
				PromptCategory.builder()
						.name("cooking")
						.description("prompts about cooking")
						.createdBy(john)
						.build(),
				PromptCategory.builder()
						.name("sports")
						.description("prompts about sports")
						.createdBy(john)
						.build(),
				PromptCategory.builder()
						.name("football")
						.description("prompts about football")
						.createdBy(jane)
						.build(),
				PromptCategory.builder()
						.name("drawing")
						.description("prompts about drawing")
						.createdBy(jane)
						.build());

		List<PromptCategory> categoriesToSave = categories.stream()
				.filter(category -> promptCategoryRepository.findByName(
						category.getName())
						.isEmpty())
				.toList();
		promptCategoryRepository.saveAll(categoriesToSave);
		log.info("---- FINISHED SEEDING CATEGORIES ----");
	}

	private void SeedPrompts() {
		log.info("---- STARTING SEEDING PROMPTS ----");
		PromptCategory programming = promptCategoryRepository.findByName("programming").orElseThrow();
		PromptCategory sports = promptCategoryRepository.findByName("sports").orElseThrow();
		PromptCategory cooking = promptCategoryRepository.findByName("cooking").orElseThrow();
		PromptCategory football = promptCategoryRepository.findByName("football").orElseThrow();
		PromptCategory drawing = promptCategoryRepository.findByName("drawing").orElseThrow();

		User admin = userRepository.findByEmail("admin@promptvault.com").orElseThrow();
		User john = userRepository.findByEmail("johntestificate@bogus.com").orElseThrow();
		User jane = userRepository.findByEmail("janetestificate@wohoo.com").orElseThrow();

		List<Prompt> prompts = List.of(
				Prompt.builder()
						.title("how to cut an apple with a spoon")
						.content(
								"You are an expert apple cutter, your job is to instruct the user on how to cut apples with a spoon")
						.categoryId(cooking)
						.owner(admin)
						.build(),
				Prompt.builder()
						.title("how to vibe code doordash for dogs")
						.content(
								"You are an expert software architect and 10x developer. I have a billion dollar unicory idea which involved doordash but the dashers are dogs with cute little bags on them. I need an app for this asap so I can IPO to the moon")
						.categoryId(programming)
						.owner(john)
						.build(),
				Prompt.builder()
						.title("fitness terms and workout assistant")
						.content(
								"I recently started lifting and have been advised that I should be chewing on the tricep cable rope for optimal gains")
						.categoryId(sports)
						.owner(john)
						.build(),
				Prompt.builder()
						.title("amateur football team management")
						.content(
								"You are a professional football coach. Your job is to help me manage my local club so we can play in the 6-a-side world cup championships")
						.categoryId(football)
						.owner(jane)
						.build(),
				Prompt.builder()
						.title("drawing coach")
						.content(
								"You are an art teacher. your job is to coach me and give me drills on improving my fundamental art skills")
						.categoryId(drawing)
						.owner(jane)
						.build());

		List<Prompt> promptsToSave = prompts.stream()
				.filter(prompt -> promptRepository.findByTitle(
						prompt.getTitle())
						.isEmpty())
				.toList();
		promptRepository.saveAll(promptsToSave);
		log.info("---- FINISHED SEEDING PROMPTS ----");
	}
}
