package com.xforceplus.wapp.junit5.service;

import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/09/15 10:56
 **/
public class ClaimBillServiceTest {

    @InjectMocks
    ClaimBillService claimBillService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * @see ClaimBillService#getCanUseQuantity(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)
     */
    @ParameterizedTest
    @CsvSource({
            "10, 666, 1000, 7",
            "10, 990, 1000, 9",
            "-10, 990, 1000, -9",
            "10, 10, 1000, 1",
            "-10, 10, 1000, -1",
            "-10, 666, 1000, -7",
            "10, 300, 334, 2",
            "-10, 300, 334, -2",
            "10, 30, 34, 1",
            "-10, 30, 34, -1",
            "10, 4, 4, 1",
            "-10, 4, 4, -1",
            "10.10, 666, 666, 6.7266",
            "10.10, 400, 666, 4.04",
            "10.10, 990, 990, 9.999",
            "-1, 39.92, 41.05, -1",
    })
    void testGetCanUseQuantity(BigDecimal quantity, BigDecimal userAmount, BigDecimal remainingAmount, BigDecimal expectedQuantity) {
        BigDecimal amountWithTax = new BigDecimal("1000");

        BigDecimal canUseQuantity = claimBillService.getCanUseQuantity(remainingAmount, amountWithTax, quantity, userAmount);
        assertEquals(0, canUseQuantity.compareTo(expectedQuantity));
    }
}
