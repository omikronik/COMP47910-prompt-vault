package com.yasirceltik.promptvault.config;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;
import com.yasirceltik.promptvault.repository.UserRepository;

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
		ArrayList<User> ul = new ArrayList<User>();
		ul.add(User.builder()
				.email("admin@promptvault.com")
				.password("Test1234")
				.role("ADMIN")
				.isActive(true)
				.build());
		ul.add(User.builder()
				.email("admin@promptvault.com")
				.password("Test1234")
				.role("ADMIN")
				.isActive(true)
				.build());
		if (userRepository.findByEmail("admin@promptvault.com").isEmpty()) {
		}
	}
}
