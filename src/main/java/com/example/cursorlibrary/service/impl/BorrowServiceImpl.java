package com.example.cursorlibrary.service.impl;

import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BorrowRequestDTO;
import com.example.cursorlibrary.model.Book;
import com.example.cursorlibrary.model.BorrowRecord;
import com.example.cursorlibrary.model.BorrowStatus;
import com.example.cursorlibrary.model.User;
import com.example.cursorlibrary.repository.BookRepository;
import com.example.cursorlibrary.repository.BorrowRecordRepository;
import com.example.cursorlibrary.repository.UserRepository;
import com.example.cursorlibrary.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BorrowRecordDTO createBorrowRequest(BorrowRequestDTO requestDTO) {
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (book.getAvailableCopies() < 1) {
            throw new RuntimeException("No available copies");
        }

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setUser(user);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(14)); // 设置14天的借阅期限
        borrowRecord.setStatus(BorrowStatus.PENDING);
        borrowRecord.setRemarks(requestDTO.getRemarks());

        return convertToDTO(borrowRecordRepository.save(borrowRecord));
    }

    @Override
    @Transactional
    public BorrowRecordDTO approveBorrowRequest(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (record.getStatus() != BorrowStatus.PENDING) {
            throw new RuntimeException("Invalid status for approval");
        }

        Book book = record.getBook();
        if (book.getAvailableCopies() < 1) {
            throw new RuntimeException("No available copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        record.setStatus(BorrowStatus.BORROWED);
        return convertToDTO(borrowRecordRepository.save(record));
    }

    @Override
    @Transactional
    public BorrowRecordDTO rejectBorrowRequest(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (record.getStatus() != BorrowStatus.PENDING) {
            throw new RuntimeException("Invalid status for rejection");
        }

        record.setStatus(BorrowStatus.REJECTED);
        return convertToDTO(borrowRecordRepository.save(record));
    }

    @Override
    @Transactional
    public BorrowRecordDTO returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (record.getStatus() != BorrowStatus.BORROWED) {
            throw new RuntimeException("Invalid status for return");
        }

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnDate(LocalDateTime.now());
        return convertToDTO(borrowRecordRepository.save(record));
    }

    @Override
    public BorrowRecordDTO getBorrowRecord(Long id) {
        return convertToDTO(borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found")));
    }

    @Override
    public Page<BorrowRecordDTO> getAllBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<BorrowRecordDTO> getUserBorrowRecords(Long userId, Pageable pageable) {
        return borrowRecordRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<BorrowRecordDTO> getBookBorrowRecords(Long bookId, Pageable pageable) {
        return borrowRecordRepository.findByBookId(bookId, pageable)
                .map(this::convertToDTO);
    }

    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .username(record.getUser().getUsername())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .remarks(record.getRemarks())
                .build();
    }
} 