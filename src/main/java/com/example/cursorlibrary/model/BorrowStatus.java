package com.example.cursorlibrary.model;

public enum BorrowStatus {
    PENDING,    // 待审核
    APPROVED,   // 已批准
    REJECTED,   // 已拒绝
    BORROWED,   // 已借出
    RETURNED,   // 已归还
    OVERDUE     // 已逾期
} 