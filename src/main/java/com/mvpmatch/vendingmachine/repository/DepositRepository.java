package com.mvpmatch.vendingmachine.repository;

import com.mvpmatch.vendingmachine.domain.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findByUserId(Long userId);
}
