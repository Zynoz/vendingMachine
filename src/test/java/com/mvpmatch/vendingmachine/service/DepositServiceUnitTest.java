package com.mvpmatch.vendingmachine.service;


import com.mvpmatch.vendingmachine.domain.Deposit;
import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.domain.VendingMachine;
import com.mvpmatch.vendingmachine.dto.DepositDTO;
import com.mvpmatch.vendingmachine.repository.DepositRepository;
import com.mvpmatch.vendingmachine.repository.VendingMachineRepository;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DepositServiceUnitTest {

    @InjectMocks
    private DepositService depositService;

    @Mock
    private SessionService sessionService;

    @Mock
    private DepositRepository mockDepositRepository;

    @Mock
    private VendingMachineRepository mockVendingMachineRepository;

    private final User authenticatedUser = new User();

    private final Deposit userDeposit = new Deposit();
    private final VendingMachine vendingMachine = new VendingMachine();

    @Before
    public void setup() {
        authenticatedUser.setId(1L);
        userDeposit.setUserId(1L);
    }

    @Test
    public void test_depositForCurrentUser_returnsNewDepositWithGivenAmount() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        Mockito.when(mockVendingMachineRepository.findById(1L))
                .thenReturn(Optional.of(vendingMachine));

        assertThat(depositService.depositForCurrentUser(BigDecimal.TEN)).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void test_depositForCurrentUser_WithExistingDeposit_returnsSumOfBoth() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        userDeposit.setDepositAmount(BigDecimal.TEN);
        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.of(userDeposit));

        assertThat(depositService.depositForCurrentUser(BigDecimal.TEN)).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    public void test_getCurrentUserDeposit_WithNonExistingDeposit_returnsDefault_0_Value() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        assertThat(depositService.getCurrentUserDepositedAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void test_getCurrentUserDeposit_WithExistingDeposit_returnsValueOfDeposit() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        userDeposit.setDepositAmount(BigDecimal.TEN);
        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.of(userDeposit));

        assertThat(depositService.getCurrentUserDepositedAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void test_substractFromUserDeposit_WithExistingDeposit_returnsCorrectSum() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        userDeposit.setDepositAmount(BigDecimal.TEN);
        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.of(userDeposit));

        assertThat(depositService.substractFromUserDeposit(BigDecimal.ONE)).isEqualTo(BigDecimal.valueOf(9));

    }

    @Test
    public void test_resetForCurrentUser() {
        Mockito.when(sessionService.getCurrentUserLoggedIn())
                .thenReturn(authenticatedUser);

        userDeposit.setDepositAmount(BigDecimal.TEN);
        Mockito.when(mockDepositRepository.findByUserId(1L))
                .thenReturn(Optional.of(userDeposit));

        Deposit reset = new Deposit();
        reset.setDepositAmount(BigDecimal.ZERO);
        reset.setId(1L);
        Mockito.when(mockDepositRepository.save(userDeposit))
                .thenReturn(reset);

        DepositDTO resetForCurrentUser = depositService.resetForCurrentUser();
        assertThat(resetForCurrentUser.getDepositAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(resetForCurrentUser.getId()).isEqualTo(1L);
    }

}
