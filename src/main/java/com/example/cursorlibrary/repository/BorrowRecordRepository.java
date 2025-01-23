package com.example.cursorlibrary.repository;

import com.example.cursorlibrary.model.BorrowRecord;
import com.example.cursorlibrary.model.BorrowStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "借阅记录管理", description = "借阅记录的增删改查接口")
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    /**
     * 查找用户的借阅记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 查找图书的借阅记录
     * @param bookId 图书ID
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    Page<BorrowRecord> findByBookId(Long bookId, Pageable pageable);
    
    /**
     * 按状态查找借阅记录
     * @param status 借阅状态
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);
    
    /**
     * 统计特定状态的借阅记录数量
     * @param status 借阅状态
     * @return 记录数量
     */
    long countByStatus(BorrowStatus status);
    
    /**
     * 获取最近的借阅记录
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    @Query("SELECT b FROM BorrowRecord b ORDER BY b.borrowDate DESC")
    Page<BorrowRecord> findRecentBorrows(Pageable pageable);
    
    /**
     * 获取用户的借阅记录（包含详细信息）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    @Query("SELECT b FROM BorrowRecord b LEFT JOIN FETCH b.book LEFT JOIN FETCH b.user WHERE b.user.id = :userId ORDER BY b.borrowDate DESC")
    Page<BorrowRecord> findUserBorrows(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取图书的借阅记录（包含详细信息）
     * @param bookId 图书ID
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    @Query("SELECT b FROM BorrowRecord b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.book WHERE b.book.id = :bookId ORDER BY b.borrowDate DESC")
    Page<BorrowRecord> findBookBorrows(@Param("bookId") Long bookId, Pageable pageable);
    
    /**
     * 查找用户的逾期借阅记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    @Query("SELECT b FROM BorrowRecord b WHERE b.user.id = :userId AND b.status = 'OVERDUE' ORDER BY b.dueDate ASC")
    Page<BorrowRecord> findOverdueBorrowsByUser(@Param("userId") Long userId, Pageable pageable);
    
    long countByStatusAndDueDateBefore(BorrowStatus status, LocalDateTime date);
    
    List<BorrowRecord> findTop5ByOrderByBorrowDateDesc();
} 