package com.xforceplus.wapp.modules.job;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.job.dto.OriginAgreementBillDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @program: wapp-generator
 * @description:
 * @author: Kenny Wong
 * @create: 2021-10-27 16:27
 **/
public class OriginAgreementBillDtoTest extends BaseUnitTest {

    @Autowired
    private Validator validator;

    @Test
    public void test1() {
        OriginAgreementBillDto data = new OriginAgreementBillDto();
        data.setCompanyCode("11111111111111111111111111111111111111111111111111111111111111111111111111111");
        Set<ConstraintViolation<OriginAgreementBillDto>> violations = validator.validate(data);
        assertNotNull(violations);
        assertEquals(violations.size(), 1);
    }
}
