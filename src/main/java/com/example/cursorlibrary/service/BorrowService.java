package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BorrowRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowService {
    BorrowRecordDTO createBorrowRequest(BorrowRequestDTO requestDTO);
    BorrowRecordDTO approveBorrowRequest(Long id);
    BorrowRecordDTO rejectBorrowRequest(Long id);
    BorrowRecordDTO returnBook(Long id);
    BorrowRecordDTO getBorrowRecord(Long id);
    Page<BorrowRecordDTO> getAllBorrowRecords(Pageable pageable);
    Page<BorrowRecordDTO> getUserBorrowRecords(Long userId, Pageable pageable);
    Page<BorrowRecordDTO> getBookBorrowRecords(Long bookId, Pageable pageable);
} 