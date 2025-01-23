package com.example.cursorlibrary.config;

import com.example.cursorlibrary.model.Role;
import com.example.cursorlibrary.model.User;
import com.example.cursorlibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Override
    public void run(String... args) {
        init();
    }

    @PostConstruct
    public void init() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .fullName("System Administrator")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                userRepository.save(admin);
                log.info("Default admin user created");
            }
        } catch (Exception e) {
            log.error("Error creating admin user", e);
        }
    }
} 