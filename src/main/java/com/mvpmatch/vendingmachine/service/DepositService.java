package com.mvpmatch.vendingmachine.service;

import com.mvpmatch.vendingmachine.domain.Deposit;
import com.mvpmatch.vendingmachine.domain.VendingMachine;
import com.mvpmatch.vendingmachine.repository.DepositRepository;
import com.mvpmatch.vendingmachine.repository.VendingMachineRepository;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private VendingMachineRepository vendingMachineRepository;

    @Autowired
    private SessionService sessionService;

    /**
     * Creates a new deposit if the vending machine has no deposit for the current user
     *
     * @param depositAmount - how much the user deposits in the vending machine
     * @return how much the user has in the vending machine
     */
    public BigDecimal depositForCurrentUser(BigDecimal depositAmount) {
        long id = sessionService.getCurrentUserLoggedIn().getId();
        Optional<Deposit> depositOfUser = depositRepository.findByUserId(id);
        if (!depositOfUser.isPresent()) {
            Deposit deposit = new Deposit();
            deposit.setUserId(id);
            deposit.setDepositAmount(depositAmount);

            Optional<VendingMachine> byId = vendingMachineRepository.findById(1L);
            deposit.setVendingMachine(byId.get());

            depositRepository.save(deposit);
            return depositAmount;
        }

        Deposit deposit = depositOfUser.get();
        BigDecimal sumForUser = deposit.getDepositAmount().add(depositAmount);
        deposit.setDepositAmount(sumForUser);
        depositRepository.save(deposit);
        return sumForUser;

    }

    public BigDecimal getCurrentUserDepositedAmount() {
        Optional<Deposit> depositOfUser = getCurrentUserDeposit();
        if (!depositOfUser.isPresent()) {
            return BigDecimal.ZERO;
        }

        return depositOfUser.get().getDepositAmount();
    }

    private Optional<Deposit> getCurrentUserDeposit() {
        long id = sessionService.getCurrentUserLoggedIn().getId();
        return depositRepository.findByUserId(id);
    }

    public BigDecimal updateClientDeposit(BigDecimal totalSpent) {
        if (!getCurrentUserDeposit().isPresent()) {
            return BigDecimal.ZERO;
        }

        Deposit deposit = getCurrentUserDeposit().get();
        BigDecimal valueLeftInDeposit = deposit.getDepositAmount().subtract(totalSpent);
        deposit.setDepositAmount(valueLeftInDeposit);
        depositRepository.save(deposit);
        return valueLeftInDeposit;
    }

    public String resetForCurrentClient() {
        Optional<Deposit> currentUserDeposit = getCurrentUserDeposit();
        if (!currentUserDeposit.isPresent()) {
            return "You didn't had any deposit yet.";
        }

        currentUserDeposit.get().setDepositAmount(BigDecimal.ZERO);
        depositRepository.save(currentUserDeposit.get());

        return "Your deposit is reset to 0. You should have received your money back if you had any deposited";
    }
}
