package com.example.cursorlibrary.repository;

import com.example.cursorlibrary.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 定义按标题或作者进行搜索的方法
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author, Pageable pageable);

    // 定义按分类 ID 查询的方法
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b ORDER BY b.borrowCount DESC")
    List<Book> findPopularBooks(Pageable pageable);
}
