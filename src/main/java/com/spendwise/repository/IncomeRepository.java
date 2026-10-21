
package com.spendwise.repository;

import com.spendwise.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByUser_Id(Long userId);

    Optional<Income> findByIdAndUser_Id(Long id, Long userId);
}
