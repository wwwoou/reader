package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.StatsDTO;
import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BookDTO;

import java.util.List;

public interface StatsService {
    /**
     * 获取系统统计数据
     * @return 统计数据DTO
     */
    StatsDTO getStats();

    /**
     * 获取最近借阅记录
     * @return 最近5条借阅记录
     */
    List<BorrowRecordDTO> getRecentBorrows();

    /**
     * 获取热门图书
     * @return 借阅次数最多的5本图书
     */
    List<BookDTO> getPopularBooks();
} 