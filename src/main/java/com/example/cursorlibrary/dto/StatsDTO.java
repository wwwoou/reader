package com.example.cursorlibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsDTO {
    private Long totalBooks;
    private Long totalBorrows;
    private Long currentBorrows;
    private Long overdueBorrows;
} 