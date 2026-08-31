package com.yasirceltik.promptvault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.dto.CreatePolicyKeywordDto;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyKeywordService {
	private final PolicyKeywordRepository policyKeywordRepository;

	public List<PolicyKeyword> getAllKeywords() {
		return policyKeywordRepository.findAll();
	}

	public Optional<PolicyKeyword> getKeywordById(Long id) {
		return policyKeywordRepository.findById(id);
	}

	@Transactional
	public boolean createKeyword(CreatePolicyKeywordDto dto, User user) {
		String normalized = dto.content().trim().toLowerCase(java.util.Locale.ROOT);
		if (policyKeywordRepository.existsByContentIgnoreCase(normalized)) {
			return false;
		}
		PolicyKeyword keyword = PolicyKeyword.builder()
				.content(normalized)
				.createdBy(user)
				.build();
		policyKeywordRepository.save(keyword);
		log.info("created policy keyword id={}", keyword.getId());
		return true;
	}

	@Transactional
	public boolean editKeyword(Long id, CreatePolicyKeywordDto dto, User user) {
		PolicyKeyword keyword = policyKeywordRepository.findById(id).orElseThrow();
		String normalized = dto.content().trim().toLowerCase(java.util.Locale.ROOT);
		Optional<PolicyKeyword> duplicate = policyKeywordRepository.findByContent(normalized);
		if (duplicate.isPresent() && duplicate.get().getId() != keyword.getId()) {
			return false;
		}
		keyword.setContent(normalized);
		keyword.setUpdatedBy(user);
		log.info("edited policy keyword id={} by userId={}", keyword.getId(), user.getId());
		return true;
	}

	@Transactional
	public void deleteKeyword(Long id) {
		policyKeywordRepository.deleteById(id);
		log.info("deleted policy keyword {}", id);
	}
}
