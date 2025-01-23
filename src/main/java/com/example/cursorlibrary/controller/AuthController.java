package com.example.cursorlibrary.controller;

import com.example.cursorlibrary.dto.AuthRequest;
import com.example.cursorlibrary.dto.AuthResponse;
import com.example.cursorlibrary.dto.RegisterRequest;
import com.example.cursorlibrary.dto.UserDTO;
import com.example.cursorlibrary.security.JwtTokenProvider;
import com.example.cursorlibrary.service.AuthService;
import com.example.cursorlibrary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j  // 使用 Lombok 注解来自动创建日志记录器
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Received login request for user: {}", request.getUsername());
        try {
            AuthResponse response = authService.login(request);
            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            // 打印日志并返回详细错误信息
            log.error("Error during registration: ", e);
            // 使用 Builder 模式构建 AuthResponse
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .status("ERROR")
                            .message("Registration failed")
                            .token(null)  // 可以选择 null 或者不返回 token
                            .build());
        }
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserDTO userDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(userDTO);
    }

}
