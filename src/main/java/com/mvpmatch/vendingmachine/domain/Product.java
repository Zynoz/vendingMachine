package com.mvpmatch.vendingmachine.domain;

import com.mvpmatch.vendingmachine.dto.ProductDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Product {
    private static final BigDecimal DOLLAR_TO_CENT_CONVERSION_RATE = BigDecimal.valueOf(100);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal cost;
    private Integer amountAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    /**
     * Setting the cost of the product. There can be cases when a seller is selling a product with 1.5, which means that
     * the product is 1 Dollar and 50 cents. We are setting that the cost is 150 cents because a buyer can only buy by inserting
     * cents
     *
     */
    public void setCostInCents(ProductDTO productDTO) {
        BigDecimal costInCents = productDTO.getCostInCents().setScale(2, RoundingMode.CEILING);
        if (costInCents.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) > 0) {
            costInCents = costInCents.multiply(DOLLAR_TO_CENT_CONVERSION_RATE);
        }
        this.setCost(costInCents);
    }
}
