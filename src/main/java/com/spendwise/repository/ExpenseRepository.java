
package com.spendwise.repository;

import com.spendwise.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUser_Id(Long userId);

    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);

    List<Expense> findByUser_IdAndTitleContainingIgnoreCase(
            Long userId, String title);

    List<Expense> findByUser_IdAndDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
        """)
    BigDecimal getTotalSpending(@Param("userId") Long userId);

    @Query("""
        SELECT e.category, SUM(e.amount)
        FROM Expense e
        WHERE e.user.id = :userId
        GROUP BY e.category
        ORDER BY e.category
        """)
    List<Object[]> getCategoryWiseSpending(
            @Param("userId") Long userId);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
          AND e.date >= :startDate
          AND e.date < :endDate
        """)
    BigDecimal getSpendingBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
