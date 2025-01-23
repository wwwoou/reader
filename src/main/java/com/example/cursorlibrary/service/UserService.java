package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.UserDTO;

public interface UserService {
    UserDTO getUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 