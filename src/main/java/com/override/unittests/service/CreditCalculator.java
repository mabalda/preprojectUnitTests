package com.override.unittests.service;

import com.override.unittests.enums.ClientType;
import com.override.unittests.exception.CentralBankNotRespondingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditCalculator {

    @Autowired
    private CentralBankService centralBankService;

    public double calculateOverpayment(double amount, double monthPaymentAmount, ClientType clientType) {
        double creditRate;
        try {
            creditRate = calculateCreditRate(clientType);
        } catch (CentralBankNotRespondingException e) {
            creditRate = centralBankService.getDefaultCreditRate();
        }

        int countsMonths = countMonths(amount, monthPaymentAmount, creditRate);
        int countYears = countsMonths / 12;
        double overPayment = amount * creditRate / 100.d;
        double amoutToPayWithPercent = amount + amount * creditRate / 100.d; //проценты за первый год
        for (int i = 0; i < countYears; i++) {
            amoutToPayWithPercent = amoutToPayWithPercent - monthPaymentAmount * 12;
            overPayment += amoutToPayWithPercent * creditRate / 100.d;
            amoutToPayWithPercent = amoutToPayWithPercent + amoutToPayWithPercent * creditRate / 100.d;
        }
        return overPayment;

    }

    public int countMonths(double amount, double monthPaymentAmount, double creditRate) {
        double amoutToPayWithPercent = amount + amount * creditRate / 100.d; //проценты за первый год
        if (monthPaymentAmount * 12 < (amoutToPayWithPercent - monthPaymentAmount * 12) * creditRate / 100.d) {
            throw new RuntimeException("Credit " + amount + " can not be payed with monthly pay " + monthPaymentAmount);
        }
        int countMonths = 0;
        while (amoutToPayWithPercent > 0) {
            amoutToPayWithPercent = amoutToPayWithPercent - monthPaymentAmount;
            countMonths++;
            if (countMonths % 12 == 0) {
                amoutToPayWithPercent = amoutToPayWithPercent + amoutToPayWithPercent * creditRate / 100.d;
            }
        }
        return countMonths;
    }

    private double calculateCreditRate(ClientType clientType) {
        double keyRate = centralBankService.getKeyRate();
        if (clientType == ClientType.INDIVIDUAL) {
            return keyRate + 2d;
        }
        if (clientType == ClientType.BUSINESS) {
            return keyRate + 1d;
        }
        if (clientType == ClientType.GOVERMENT) {
            return keyRate;
        }
        return keyRate;
    }

}

