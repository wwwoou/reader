package com.example.cursorlibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String status;
    private String message;
    private String token;
    private String username; // 用户名字段
    private String role;     // 角色字段

    // 全参构造器
    public AuthResponse(String status, String message, String token, String username, String role) {
        this.status = status;
        this.message = message;
        this.token = token;
        this.username = username;
        this.role = role;
    }

    // Builder 模式支持所有字段
    public static class AuthResponseBuilder {
        private String status;
        private String message;
        private String token;
        private String username;
        private String role;

        public AuthResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(status, message, token, username, role);
        }
    }
}
