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
	public boolean createCategory(CreateCategoryDto dto, User user) {
		String name = dto.name().trim();
		if (promptCategoryRepository.existsByNameIgnoreCase(name)) {
			return false;
		}
		PromptCategory category = PromptCategory.builder()
				.name(name)
				.description(dto.description().trim())
				.createdBy(user)
				.build();
		promptCategoryRepository.save(category);
		log.info("created category id={}", category.getId());
		return true;
	}

	@Transactional
	public boolean editCategory(Long id, CreateCategoryDto dto, User user) {
		PromptCategory category = promptCategoryRepository.findById(id).orElseThrow();
		String name = dto.name().trim();
		Optional<PromptCategory> duplicate = promptCategoryRepository.findByName(name);
		if (duplicate.isPresent() && duplicate.get().getId() != category.getId()) {
			return false;
		}
		category.setName(name);
		category.setDescription(dto.description().trim());
		category.setUpdatedBy(user);
		log.info("edited category id={} by userId={}", category.getId(), user.getId());
		return true;
	}

	@Transactional
	public void deleteCategory(Long id) {
		promptRepository.nullifyCategoryReferences(id);
		promptCategoryRepository.deleteById(id);
		log.info("deleted category {}", id);
	}
}
