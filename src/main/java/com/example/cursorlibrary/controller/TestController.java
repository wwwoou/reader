package com.example.cursorlibrary.controller;

import com.example.cursorlibrary.model.Role;
import com.example.cursorlibrary.model.User;
import com.example.cursorlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/db")
    public Map<String, Object> testDb() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 测试数据库连接
            boolean isConnected = dataSource.getConnection().isValid(5);
            response.put("dbConnection", isConnected ? "Success" : "Failed");
            
            // 测试查询
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            response.put("queryTest", result != null ? "Success" : "Failed");
            
            // 获取数据库信息
            String dbInfo = jdbcTemplate.queryForObject(
                "SELECT VERSION()", String.class);
            response.put("databaseInfo", dbInfo);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/category")
    public Map<String, Object> testAddCategory(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 插入测试分类
            String name = request.get("name");
            String description = request.get("description");
            
            jdbcTemplate.update(
                "INSERT INTO categories (name, description) VALUES (?, ?)",
                name, description
            );
            
            // 查询刚插入的数据
            List<Map<String, Object>> categories = jdbcTemplate.queryForList(
                "SELECT * FROM categories WHERE name = ?",
                name
            );
            
            response.put("message", "Category added successfully");
            response.put("data", categories);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/categories")
    public Map<String, Object> testGetCategories() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> categories = jdbcTemplate.queryForList(
                "SELECT * FROM categories"
            );
            
            response.put("data", categories);
            response.put("count", categories.size());
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/user")
    public Map<String, Object> testAddUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 创建测试用户
            User user = new User();
            user.setUsername(request.get("username"));
            user.setPassword(passwordEncoder.encode(request.get("password")));
            user.setEmail(request.get("email"));
            user.setFullName(request.get("fullName"));
            user.setRole(Role.ROLE_USER);
            user.setEnabled(true);
            
            User savedUser = userRepository.save(user);
            
            response.put("message", "User created successfully");
            response.put("userId", savedUser.getId());
            response.put("username", savedUser.getUsername());
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/users")
    public Map<String, Object> testGetUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, username, email, full_name, role, enabled FROM users"
            );
            
            response.put("data", users);
            response.put("count", users.size());
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/auth-test")
    public ResponseEntity<Map<String, Object>> testAuth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
} 