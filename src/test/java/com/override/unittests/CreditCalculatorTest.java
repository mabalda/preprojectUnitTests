package com.override.unittests;

import com.override.unittests.enums.ClientType;
import com.override.unittests.exception.CannotBePayedException;
import com.override.unittests.exception.CentralBankNotRespondingException;
import com.override.unittests.service.CentralBankService;
import com.override.unittests.service.CreditCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditCalculatorTest {

    @InjectMocks
    private CreditCalculator creditCalculator;

    @Mock
    private CentralBankService centralBankService;

    @Test
    public void calculateOverpaymentGovermentTest() {
        when(centralBankService.getKeyRate()).thenReturn(10d);
        double amount = 100000d;
        double monthPaymentAmount = 10000d;
        double result = creditCalculator.calculateOverpayment(amount, monthPaymentAmount, ClientType.GOVERMENT);
        Assertions.assertEquals(10000d, result);
    }

    @Test
    public void calculateOverpaymentBusinessTest() {
        when(centralBankService.getKeyRate()).thenReturn(10d);
        double amount = 100000d;
        double monthPaymentAmount = 10000d;
        double result = creditCalculator.calculateOverpayment(amount, monthPaymentAmount, ClientType.BUSINESS);
        Assertions.assertEquals(11000d, result);
    }

    @Test
    public void calculateOverpaymentIndividualTest() {
        when(centralBankService.getKeyRate()).thenReturn(10d);
        double amount = 100000d;
        double monthPaymentAmount = 10000d;
        double result = creditCalculator.calculateOverpayment(amount, monthPaymentAmount, ClientType.INDIVIDUAL);
        Assertions.assertEquals(12000d, result);
    }

    @ParameterizedTest
    @EnumSource(ClientType.class)
    public void calculateOverpaymentOnTooBigAmountTest(ClientType clientType) {
        when(centralBankService.getKeyRate()).thenReturn(10d);
        double amount = 1000000000d;
        double monthPaymentAmount = 10000d;
        assertThrows(CannotBePayedException.class, () -> creditCalculator.calculateOverpayment(amount, monthPaymentAmount, clientType));
    }

    @ParameterizedTest
    @MethodSource("provideOverpaymentOnManyYearCreditArguments")
    public void calculateOverpaymentOnManyYearCreditTest(ClientType clientType, double monthPaymentAmount, double expectedResult) {
        when(centralBankService.getKeyRate()).thenReturn(10d);
        double amount = 100000d;
        double result = creditCalculator.calculateOverpayment(amount, monthPaymentAmount, clientType);
        Assertions.assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> provideOverpaymentOnManyYearCreditArguments() {
        return Stream.of(
                Arguments.of(ClientType.GOVERMENT, 1000d, 125681.8191031709d),
                Arguments.of(ClientType.BUSINESS, 1000d, 185825.84928000634d),
                Arguments.of(ClientType.INDIVIDUAL, 2000d, 46931.85925939199d)
        );
    }

    @ParameterizedTest
    @EnumSource(ClientType.class)
    public void calculateOverpaymentWhenNoConnectionTest(ClientType clientType) {
        when(centralBankService.getKeyRate()).thenThrow(new CentralBankNotRespondingException());
        when(centralBankService.getDefaultCreditRate()).thenReturn(30d);
        double amount = 100000d;
        double monthPaymentAmount = 10000d;
        double result = creditCalculator.calculateOverpayment(amount, monthPaymentAmount, clientType);
        Assertions.assertEquals(33000d, result);
    }
}
