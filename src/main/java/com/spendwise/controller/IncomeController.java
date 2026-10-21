
package com.spendwise.controller;

import com.spendwise.model.Income;
import com.spendwise.service.IncomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "http://localhost:5173")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public Income addIncome(
            @RequestBody Income income,
            Authentication authentication) {
        return incomeService.addIncome(
                authentication.getName(), income);
    }

    @GetMapping
    public List<Income> getAllIncomes(Authentication authentication) {
        return incomeService.getAllIncomes(authentication.getName());
    }

    @GetMapping("/{id}")
    public Income getIncomeById(
            @PathVariable Long id,
            Authentication authentication) {
        return incomeService.getIncomeById(
                authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public Income updateIncome(
            @PathVariable Long id,
            @RequestBody Income income,
            Authentication authentication) {
        return incomeService.updateIncome(
                authentication.getName(), id, income);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(
            @PathVariable Long id,
            Authentication authentication) {
        incomeService.deleteIncome(authentication.getName(), id);
    }
}
