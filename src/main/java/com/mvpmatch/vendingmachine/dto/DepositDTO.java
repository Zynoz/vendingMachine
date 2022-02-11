package com.mvpmatch.vendingmachine.dto;

import java.math.BigDecimal;

public class DepositDTO {

    private BigDecimal depositAmount;

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    @Override
    public String toString() {
        return "DepositDTO{" +
                "depositAmount=" + depositAmount +
                '}';
    }
}
