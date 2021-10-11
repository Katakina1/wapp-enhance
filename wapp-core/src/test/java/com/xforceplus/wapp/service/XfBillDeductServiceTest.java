package com.xforceplus.wapp.service;

import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.repository.dao.TXBillDeductDao;
import com.xforceplus.wapp.repository.entity.TXBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
public class XfBillDeductServiceTest {

    @Autowired
    private TXBillDeductDao xfBillDeductDao;

    @Test
    public void testXfBillDeductList(){
        TXBillDeductEntity xfBillDeductEntity = xfBillDeductDao.selectById(1);
        System.out.println(xfBillDeductEntity);
    }
}
