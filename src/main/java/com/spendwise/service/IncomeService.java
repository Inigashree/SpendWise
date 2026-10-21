
package com.spendwise.service;

import com.spendwise.entity.User;
import com.spendwise.model.Income;
import com.spendwise.repository.IncomeRepository;
import com.spendwise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeService(
            IncomeRepository incomeRepository,
            UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Income addIncome(String email, Income income) {
        User user = getUser(email);
        income.setUser(user);
        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomes(String email) {
        User user = getUser(email);
        return incomeRepository.findAllByUser_Id(user.getId());
    }

    public Income getIncomeById(String email, Long id) {
        User user = getUser(email);
        return incomeRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Income not found"));
    }

    public Income updateIncome(
            String email, Long id, Income updatedIncome) {
        Income existing = getIncomeById(email, id);

        existing.setSource(updatedIncome.getSource());
        existing.setType(updatedIncome.getType());
        existing.setAmount(updatedIncome.getAmount());
        existing.setDate(updatedIncome.getDate());
        existing.setFrequency(updatedIncome.getFrequency());

        return incomeRepository.save(existing);
    }

    public void deleteIncome(String email, Long id) {
        Income existing = getIncomeById(email, id);
        incomeRepository.delete(existing);
    }
}
