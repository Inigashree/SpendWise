
package com.spendwise.service;

import com.spendwise.entity.User;
import com.spendwise.model.Expense;
import com.spendwise.repository.ExpenseRepository;
import com.spendwise.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Expense addExpense(String email, Expense expense) {
        User user = getUser(email);
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses(String email) {
        User user = getUser(email);
        return expenseRepository.findAllByUser_Id(user.getId());
    }

    public Expense getExpenseById(String email, Long id) {
        User user = getUser(email);
        return expenseRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public Expense updateExpense(
            String email, Long id, Expense updatedExpense) {
        Expense existing = getExpenseById(email, id);

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());

        return expenseRepository.save(existing);
    }

    public void deleteExpense(String email, Long id) {
        Expense existing = getExpenseById(email, id);
        expenseRepository.delete(existing);
    }

    public List<Expense> searchExpenses(String email, String title) {
        User user = getUser(email);
        return expenseRepository
                .findByUser_IdAndTitleContainingIgnoreCase(user.getId(), title);
    }

    public List<Expense> getExpensesByDateRange(
            String email, LocalDate startDate, LocalDate endDate) {
        User user = getUser(email);
        return expenseRepository.findByUser_IdAndDateBetween(
                user.getId(), startDate, endDate);
    }

    public BigDecimal getTotalSpending(String email) {
        User user = getUser(email);
        return expenseRepository.getTotalSpending(user.getId());
    }

    public List<Object[]> getCategoryWiseSpending(String email) {
        User user = getUser(email);
        return expenseRepository.getCategoryWiseSpending(user.getId());
    }

    public BigDecimal getMonthlySpending(
            String email, int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        User user = getUser(email);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        return expenseRepository.getSpendingBetweenDates(
                user.getId(), startDate, endDate);
    }
}
