package com.example.cursorlibrary.service.impl;

import com.example.cursorlibrary.dto.BorrowRecordDTO;
import com.example.cursorlibrary.dto.BookDTO;
import com.example.cursorlibrary.dto.StatsDTO;
import com.example.cursorlibrary.model.BorrowRecord;
import com.example.cursorlibrary.model.Book;
import com.example.cursorlibrary.model.BorrowStatus;
import com.example.cursorlibrary.repository.BookRepository;
import com.example.cursorlibrary.repository.BorrowRecordRepository;
import com.example.cursorlibrary.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Override   log.debug("Getting system statistics");

        @Cacheable("stats")
        public StatsDTO getStats() {

            long totalBooks = bookRepository.count();
        long totalBorrows = borrowRecordRepository.count();
        long currentBorrows = borrowRecordRepository.countByStatus(BorrowStatus.BORROWED);
        long overdueBorrows = borrowRecordRepository.countByStatusAndDueDateBefore(
            BorrowStatus.BORROWED, 
            LocalDateTime.now()
        );

        return StatsDTO.builder()
            .totalBooks(totalBooks)
            .totalBorrows(totalBorrows)
            .currentBorrows(currentBorrows)
            .overdueBorrows(overdueBorrows)
            .build();
    }

    @Override
    @Cacheable("recentBorrows")
    public List<BorrowRecordDTO> getRecentBorrows() {
        log.debug("Getting recent borrows");
        return borrowRecordRepository.findTop5ByOrderByBorrowDateDesc()
            .stream()
            .map(this::convertToBorrowRecordDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable("popularBooks")
    public List<BookDTO> getPopularBooks() {
        log.debug("Getting popular books");
        List<Book> popularBooks = bookRepository.findPopularBooks(PageRequest.of(0, 5));
        return popularBooks.stream()
                .map(this::convertToBookDTO)
                .collect(Collectors.toList());
    }

    private BorrowRecordDTO convertToBorrowRecordDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
            .id(record.getId())
            .bookId(record.getBook().getId())
            .bookTitle(record.getBook().getTitle())
            .userId(record.getUser().getId())
            .username(record.getUser().getUsername())
            .borrowDate(record.getBorrowDate())
            .dueDate(record.getDueDate())
            .returnDate(record.getReturnDate())
            .status(record.getStatus())
            .build();
    }

    private BookDTO convertToBookDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .categoryId(book.getCategory().getId())
                .publisher(book.getPublisher())
                .publicationYear(book.getPublicationYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .description(book.getDescription())
                .coverImage(book.getCoverImage())
                .borrowCount(book.getBorrowCount() != null ? (Integer) book.getBorrowCount() : 0)
                .build();
    }
} 