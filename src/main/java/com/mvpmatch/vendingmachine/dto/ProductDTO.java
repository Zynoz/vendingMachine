package com.mvpmatch.vendingmachine.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @NotEmpty
    private BigDecimal costInCents;

    @NotNull
    @NotEmpty
    private Integer amountAvailable;

    private long sellerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCostInCents() {
        return costInCents;
    }

    public void setCostInCents(BigDecimal costInCents) {
        this.costInCents = costInCents;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", costInCents=" + costInCents +
                ", amountAvailable=" + amountAvailable +
                ", sellerId=" + sellerId +
                '}';
    }
}
