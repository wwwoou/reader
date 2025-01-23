package com.example.cursorlibrary.controller;

import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BorrowRequestDTO;
import com.example.cursorlibrary.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BorrowRecordDTO> createBorrowRequest(
            @Valid @RequestBody BorrowRequestDTO requestDTO) {
        return ResponseEntity.ok(borrowService.createBorrowRequest(requestDTO));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BorrowRecordDTO> approveBorrowRequest(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.approveBorrowRequest(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BorrowRecordDTO> rejectBorrowRequest(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.rejectBorrowRequest(id));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BorrowRecordDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.returnBook(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BorrowRecordDTO> getBorrowRecord(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.getBorrowRecord(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BorrowRecordDTO>> getAllBorrowRecords(Pageable pageable) {
        return ResponseEntity.ok(borrowService.getAllBorrowRecords(pageable));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<BorrowRecordDTO>> getUserBorrowRecords(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(borrowService.getUserBorrowRecords(userId, pageable));
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BorrowRecordDTO>> getBookBorrowRecords(
            @PathVariable Long bookId, Pageable pageable) {
        return ResponseEntity.ok(borrowService.getBookBorrowRecords(bookId, pageable));
    }
} 