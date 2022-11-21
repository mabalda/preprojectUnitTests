package com.override.unittests;

import com.override.unittests.enums.ClientType;
import com.override.unittests.service.CreditCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditController {

    @Autowired
    private CreditCalculator creditCalculator;


    @GetMapping("/overpayment")
    public double getOverPayment() {
        return creditCalculator.calculateOverpayment(100000, 10000.0, ClientType.GOVERMENT);
    }
}
