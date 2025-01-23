package com.example.cursorlibrary.service.impl;

import com.example.cursorlibrary.dto.AuthRequest;
import com.example.cursorlibrary.dto.AuthResponse;
import com.example.cursorlibrary.dto.RegisterRequest;
import com.example.cursorlibrary.exception.UserAlreadyExistsException;
import com.example.cursorlibrary.model.Role;
import com.example.cursorlibrary.model.User;
import com.example.cursorlibrary.repository.UserRepository;
import com.example.cursorlibrary.security.JwtService;
import com.example.cursorlibrary.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())  // 添加 username
                    .role(user.getRole().name())
                    .build();
        } catch (Exception e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("开始注册用户: {}", request.getUsername());

        // 检查用户名是否存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("用户名已存在");
        }

        // 检查邮箱是否存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("邮箱已被使用");
        }

        try {
            // 构造用户实体
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .role(Role.ROLE_USER)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 保存用户
            User savedUser = userRepository.save(user);
            log.debug("用户注册成功: {}", savedUser.getUsername());

            // 生成 JWT
            String token = jwtService.generateToken(savedUser);

            // 返回成功响应
            return AuthResponse.builder()
                    .status("SUCCESS")
                    .message("注册成功")
                    .token(token)
                    .username(savedUser.getUsername())
                    .role(savedUser.getRole().name())
                    .build();
        } catch (Exception e) {
            log.error("用户注册失败", e);
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }

} 