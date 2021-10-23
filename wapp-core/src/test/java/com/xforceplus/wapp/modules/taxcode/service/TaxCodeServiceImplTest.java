package com.xforceplus.wapp.modules.taxcode.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


public class TaxCodeServiceImplTest extends BaseUnitTest {
    @Autowired
    private TaxCodeServiceImpl taxCodeService;

    @Test
    public void page() {
    }

    @Test
    public void getTaxCodeByItemNo() {
        Optional<TaxCode> taxCodeByItemNo = taxCodeService.getTaxCodeByItemNo("");
        System.out.println(taxCodeByItemNo);
    }

    @Test
    public void tree() {
    }
}