package com.xforceplus.wapp.service;

import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.repository.dao.TXBillDeductDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
@ActiveProfiles("unit")
public class XfBillDeductServiceTest {

    @Autowired
    private TXBillDeductDao xfBillDeductDao;

    @Test
    public void testXfBillDeductList() {
        // List<XfBillDeductEntity> list = xfBillDeductDao.selectList();
        // log.info("测试==="+JSON.toJSONString(list));
    }
}
