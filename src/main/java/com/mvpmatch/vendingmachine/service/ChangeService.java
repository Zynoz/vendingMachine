package com.mvpmatch.vendingmachine.service;

import com.mvpmatch.vendingmachine.dto.ReceiptDTO;
import com.mvpmatch.vendingmachine.enums.COINS;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ChangeService {

    /**
     * Set on the current receipt the money to give back to user in allowed coins
     */
    public void setChangeOnReceipt(ReceiptDTO receiptDTO, BigDecimal restToGiveToUserInCents) {
        int[] cashArr = new int[]{0, 0, 0, 0, 0};


        receiptDTO.setYourChangeInCents(restToGiveToUserInCents);

        while (restToGiveToUserInCents.compareTo(BigDecimal.ZERO) > 0) {
            restToGiveToUserInCents = getReturn(cashArr, restToGiveToUserInCents, 100, 4);
            restToGiveToUserInCents = getReturn(cashArr, restToGiveToUserInCents, 50, 3);
            restToGiveToUserInCents = getReturn(cashArr, restToGiveToUserInCents, 20, 2);
            restToGiveToUserInCents = getReturn(cashArr, restToGiveToUserInCents, 10, 1);
            restToGiveToUserInCents = getReturn(cashArr, restToGiveToUserInCents, 5, 0);

        }

        receiptDTO.putChange(COINS.CENTS_100, cashArr[4]);
        receiptDTO.putChange(COINS.CENTS_50, cashArr[3]);
        receiptDTO.putChange(COINS.CENTS_20, cashArr[2]);
        receiptDTO.putChange(COINS.CENTS_10, cashArr[1]);
        receiptDTO.putChange(COINS.CENTS_5, cashArr[0]);
    }

    private static BigDecimal getReturn(int[] cashArr, BigDecimal restToGiveToUser, double v, int position) {
        while (restToGiveToUser.compareTo(BigDecimal.valueOf(v)) >= 0) {
            restToGiveToUser = restToGiveToUser.subtract(BigDecimal.valueOf(v));
            cashArr[position]++;
        }
        return restToGiveToUser;
    }
}
