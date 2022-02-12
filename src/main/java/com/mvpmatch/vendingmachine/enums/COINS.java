package com.mvpmatch.vendingmachine.enums;

import java.math.BigDecimal;

/**
 * 5, 10, 20, 50 and 100 cent coins
 */
public enum COINS {
    CENTS_5("5 cents", BigDecimal.valueOf(5)),
    CENTS_10("10 cents", BigDecimal.valueOf(10)),
    CENTS_20("20 cents", BigDecimal.valueOf(20)),
    CENTS_50("50 cents", BigDecimal.valueOf(50)),
    CENTS_100("100 cents", BigDecimal.valueOf(100));

    private String message;
    private BigDecimal amount;

    COINS(String message, BigDecimal amount) {
        this.message = message;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return message;
    }
}
