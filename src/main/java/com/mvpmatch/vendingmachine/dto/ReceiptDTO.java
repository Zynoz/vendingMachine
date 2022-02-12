package com.mvpmatch.vendingmachine.dto;

import com.mvpmatch.vendingmachine.enums.COINS;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReceiptDTO {
    private BigDecimal totalSpent;
    private String purchasedProduct;

    private BigDecimal depositedAmountBeforePurchase;
    private BigDecimal yourChangeInCents;

    private final Map<COINS, Integer> yourChange = new LinkedHashMap<>();

    /**
     * Can be one of the following
     * 5, 10, 20, 50 and 100 cent coins
     */
    public ReceiptDTO() {
        yourChange.put(COINS.CENTS_5, 0);
        yourChange.put(COINS.CENTS_10, 0);
        yourChange.put(COINS.CENTS_20, 0);
        yourChange.put(COINS.CENTS_50, 0);
        yourChange.put(COINS.CENTS_100, 0);
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getPurchasedProduct() {
        return purchasedProduct;
    }

    public void setPurchasedProduct(String purchasedProduct) {
        this.purchasedProduct = purchasedProduct;
    }

    public BigDecimal getDepositedAmountBeforePurchase() {
        return depositedAmountBeforePurchase;
    }

    public void setDepositedAmountBeforePurchase(BigDecimal depositedAmountBeforePurchase) {
        this.depositedAmountBeforePurchase = depositedAmountBeforePurchase;
    }

    public BigDecimal getYourChangeInCents() {
        return yourChangeInCents;
    }

    public void setYourChangeInCents(BigDecimal yourChangeInCents) {
        this.yourChangeInCents = yourChangeInCents;
    }

    public Map<COINS, Integer> getYourChange() {
        return yourChange;
    }

    public void putChange(COINS coin, Integer amount) {
        yourChange.put(coin, amount);
    }
}
