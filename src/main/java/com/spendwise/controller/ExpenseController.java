
package com.spendwise.controller;

import com.spendwise.model.Expense;
import com.spendwise.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public Expense addExpense(
            @RequestBody Expense expense,
            Authentication authentication) {
        return expenseService.addExpense(
                authentication.getName(), expense);
    }

    @GetMapping
    public List<Expense> getAllExpenses(Authentication authentication) {
        return expenseService.getAllExpenses(authentication.getName());
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(
            @PathVariable Long id,
            Authentication authentication) {
        return expenseService.getExpenseById(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody Expense expense,
            Authentication authentication) {
        return expenseService.updateExpense(
                authentication.getName(), id, expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {
        expenseService.deleteExpense(authentication.getName(), id);
    }

    @GetMapping("/search")
    public List<Expense> searchExpenses(
            @RequestParam String title,
            Authentication authentication) {
        return expenseService.searchExpenses(
                authentication.getName(), title);
    }

    @GetMapping("/filter")
    public List<Expense> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            Authentication authentication) {
        return expenseService.getExpensesByDateRange(
                authentication.getName(), startDate, endDate);
    }

    @GetMapping("/summary/total")
    public Object getTotalSpending(Authentication authentication) {
        return expenseService.getTotalSpending(authentication.getName());
    }

    @GetMapping("/summary/category")
    public List<Object[]> getCategoryWiseSpending(
            Authentication authentication) {
        return expenseService.getCategoryWiseSpending(
                authentication.getName());
    }

    @GetMapping("/summary/monthly")
    public Object getMonthlySpending(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        return expenseService.getMonthlySpending(
                authentication.getName(), year, month);
    }
}
