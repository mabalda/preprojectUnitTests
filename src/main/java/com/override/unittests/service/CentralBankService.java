package com.override.unittests.service;

import com.override.unittests.exception.CentralBankNotRespondingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CentralBankService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Возвращает ключевую ставку Центрального банка
     **/
    public double getKeyRate() {
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity("http://web.cbr.ru/DailyInfo/rate", String.class);
        } catch (Exception e) {
            throw new CentralBankNotRespondingException();
        }
        return Double.parseDouble(response.getBody());
    }

    public double getDefaultCreditRate() {
        return 30d;
    }
}

