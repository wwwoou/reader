package com.example.cursorlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CursorLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CursorLibraryApplication.class, args);
    }

}
