package com.example.cursorlibrary.controller;

import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BookDTO;
import com.example.cursorlibrary.dto.StatsDTO;
import com.example.cursorlibrary.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "统计管理", description = "系统统计数据接口")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final Logger log = LoggerFactory.getLogger(StatsController.class);

    @Operation(summary = "获取系统统计数据")
    @GetMapping
    public ResponseEntity<StatsDTO> getStats() {
        log.debug("REST request to get system statistics");
        return ResponseEntity.ok(statsService.getStats());
    }

    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> testStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("message", "Stats endpoint is accessible");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-borrows")
    @Operation(summary = "获取最近借阅记录")
    public ResponseEntity<List<BorrowRecordDTO>> getRecentBorrows() {
        log.debug("REST request to get recent borrows");
        return ResponseEntity.ok(statsService.getRecentBorrows());
    }

    @GetMapping("/popular-books")
    @Operation(summary = "获取热门图书")
    public ResponseEntity<List<BookDTO>> getPopularBooks() {
        log.debug("REST request to get popular books");
        return ResponseEntity.ok(statsService.getPopularBooks());
    }
} 