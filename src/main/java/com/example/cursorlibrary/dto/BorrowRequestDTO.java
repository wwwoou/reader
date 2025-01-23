package com.example.cursorlibrary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequestDTO {
    @NotNull
    private Long bookId;
    
    @NotNull
    private Long userId;
    
    private String remarks;
} 