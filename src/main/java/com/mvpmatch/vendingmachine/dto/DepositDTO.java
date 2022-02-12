package com.mvpmatch.vendingmachine.dto;

import java.math.BigDecimal;

public class DepositDTO {

    private Long id;

    private BigDecimal depositAmount;

    public DepositDTO() {

    }

    public DepositDTO(Long id, BigDecimal depositAmount) {
        this.id = id;
        this.depositAmount = depositAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DepositDTO{" +
                "id=" + id +
                ", depositAmount=" + depositAmount +
                '}';
    }
}
