package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class DeductServiceTest {

    @Mock
    CompanyService companyService;
    @InjectMocks
    DeductService deductService;

    @Test
    void queryOrgInfo(){
        String taxNo="sss";
        TAcOrgEntity org=mock(TAcOrgEntity.class);
        when(companyService.getOrgInfoByTaxNo(anyString(),eq("8"))).thenReturn(org);

        final TAcOrgEntity tAcOrg = deductService.queryOrgInfo(taxNo, true);
        assertNotNull(tAcOrg);
        assertEquals(tAcOrg,org);


    }

}