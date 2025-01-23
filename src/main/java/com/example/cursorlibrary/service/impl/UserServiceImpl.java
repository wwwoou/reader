package com.example.cursorlibrary.service.impl;

import com.example.cursorlibrary.dto.UserDTO;
import com.example.cursorlibrary.exception.ResourceNotFoundException;
import com.example.cursorlibrary.model.User;
import com.example.cursorlibrary.repository.UserRepository;
import com.example.cursorlibrary.service.UserService;
import com.example.cursorlibrary.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 