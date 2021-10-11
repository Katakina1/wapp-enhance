package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.repository.entity.XfBillDeductDO;
import com.xforceplus.wapp.mapper.XfBillDeductMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
public class XfBillDeductServiceTest {
    @Autowired
    private XfBillDeductMapper xfBillDeductMapper;

    @Test
    public void testXfBillDeductList(){
        List<XfBillDeductDO> list = xfBillDeductMapper.selectList();
        log.info("测试==="+JSON.toJSONString(list));
    }
}
