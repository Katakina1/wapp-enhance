package com.xforceplus.wapp.modules.taxcode.service;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeDto;

import io.vavr.control.Either;


public class TaxCodeServiceImplTest extends BaseUnitTest {
    @Autowired
    private TaxCodeServiceImpl taxCodeService;

    @Test
    public void page() {
    }

    @Test
    public void getTaxCodeByItemNo() {
        Optional<TaxCodeDto> taxCodeByItemNo = taxCodeService.getTaxCodeByItemNo("");
        System.out.println(taxCodeByItemNo);
    }

    @Test
    public void test_searchTaxCode() {
    	Either<String, List<TaxCodeBean>> result = taxCodeService.searchTaxCode("", "洗涤剂");
    	System.err.println(JSON.toJSONString(result.get()));
    }
    
    @Test
    public void tree() {
    }
}