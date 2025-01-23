package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.AuthRequest;
import com.example.cursorlibrary.dto.AuthResponse;
import com.example.cursorlibrary.dto.RegisterRequest;

public interface AuthService {
    /**
     * 用户登录
     * @param request 登录请求
     * @return 认证响应
     */
    AuthResponse login(AuthRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     * @return 认证响应
     */
    AuthResponse register(RegisterRequest request);
} 