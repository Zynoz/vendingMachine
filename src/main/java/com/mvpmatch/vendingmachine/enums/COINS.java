package com.mvpmatch.vendingmachine.enums;

/**
 * 5, 10, 20, 50 and 100 cent coins
 */
public enum COINS {
    CENTS_5("5 cents"),
    CENTS_10("10 cents"),
    CENTS_20("20 cents"),
    CENTS_50("50 cents"),
    CENTS_100("100 cents");

    private String message;

    COINS(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
