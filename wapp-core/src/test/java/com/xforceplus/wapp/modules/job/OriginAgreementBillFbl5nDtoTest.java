package com.xforceplus.wapp.modules.job;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.job.dto.OriginAgreementBillFbl5nDto;
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
public class OriginAgreementBillFbl5nDtoTest extends BaseUnitTest {

    @Autowired
    private Validator validator;

    @Test
    public void test1() {
        OriginAgreementBillFbl5nDto data = new OriginAgreementBillFbl5nDto();
        data.setCompanyCode("11111111111111111111111111111111111111111111111111111111111111111111111111111");
        Set<ConstraintViolation<OriginAgreementBillFbl5nDto>> violations = validator.validate(data);
        assertNotNull(violations);
        assertEquals(violations.size(), 1);
    }
}
