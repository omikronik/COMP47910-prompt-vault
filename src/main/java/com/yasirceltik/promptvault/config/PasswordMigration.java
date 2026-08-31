package com.yasirceltik.promptvault.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordMigration implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        for (User user : userRepository.findAll()) {
            if (!isBcryptHash(user.getPassword())) {
                user.setPassword(
                    passwordEncoder.encode(user.getPassword())
                );

                userRepository.save(user);
                log.info(
                    "Migrated password hash for user id={}",
                    user.getId()
                );
            }
        }
    }

    private boolean isBcryptHash(String password) {
        return password != null
                && password.matches(
                    "^\\$2[aby]\\$\\d{2}\\$.{53}$"
                );
    }
}
