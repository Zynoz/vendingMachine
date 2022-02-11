package com.mvpmatch.vendingmachine.repository;

import com.mvpmatch.vendingmachine.domain.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendingMachineRepository extends JpaRepository<VendingMachine, Long> {
}
