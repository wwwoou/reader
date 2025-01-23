package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    BookDTO getBook(Long id);
    Page<BookDTO> getAllBooks(Pageable pageable);
    Page<BookDTO> searchBooks(String keyword, Pageable pageable);
    Page<BookDTO> getBooksByCategory(Long categoryId, Pageable pageable);
} 