package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: ChenHang
 * @Date: 2023/8/11 15:17
 */
public class NoneBusinessServiceTest extends BaseUnitTest {

    @Autowired
    private NoneBusinessService noneBusinessService;

    @Test
    public void test01() {
        TXfNoneBusinessUploadDetailEntity tXfNoneBusinessUploadDetailEntity = new TXfNoneBusinessUploadDetailEntity();
        tXfNoneBusinessUploadDetailEntity.setId(1683381756527058946L);
        tXfNoneBusinessUploadDetailEntity.setInvoiceNo("23442000000012536231");
        tXfNoneBusinessUploadDetailEntity.setGoodsName("*蔬菜*菜心");
        noneBusinessService.saveOrUpdate(tXfNoneBusinessUploadDetailEntity);
    }

}
