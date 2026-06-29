package com.yasirceltik.promptvault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public void setActive(Long id, boolean active) {
		User user = userRepository.findById(id).orElseThrow();
		user.setActive(active);
		log.info("set user {} active={}", user.getEmail(), active);
	}
}
