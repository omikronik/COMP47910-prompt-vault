package com.yasirceltik.promptvault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.dto.CreateCategoryDto;
import com.yasirceltik.promptvault.model.PromptCategory;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.PromptCategoryRepository;
import com.yasirceltik.promptvault.repository.PromptRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
	private final PromptCategoryRepository promptCategoryRepository;
	private final PromptRepository promptRepository;

	public List<PromptCategory> getAllCategories() {
		return promptCategoryRepository.findAll();
	}

	public Optional<PromptCategory> getCategoryById(Long id) {
		return promptCategoryRepository.findById(id);
	}

	@Transactional
	public void createCategory(CreateCategoryDto dto, User user) {
		PromptCategory category = PromptCategory.builder()
				.name(dto.name())
				.description(dto.description())
				.createdBy(user)
				.build();
		promptCategoryRepository.save(category);
		log.info("created category '{}'", category.getName());
	}

	@Transactional
	public void editCategory(Long id, CreateCategoryDto dto, User user) {
		PromptCategory category = promptCategoryRepository.findById(id).orElseThrow();
		category.setName(dto.name());
		category.setDescription(dto.description());
		category.setUpdatedBy(user);
		log.info("edited category '{}'", category.getName());
	}

	@Transactional
	public void deleteCategory(Long id) {
		promptRepository.nullifyCategoryReferences(id);
		promptCategoryRepository.deleteById(id);
		log.info("deleted category {}", id);
	}
}
