package com.xforceplus.wapp.modules.preinvoice.service;

import com.xforceplus.wapp.BaseUnitTest;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/09/13 9:50
 **/
public class PreInvoiceDaoServiceTest extends BaseUnitTest {

    @Autowired
    PreInvoiceDaoService preInvoiceDaoService;

    @Test
    public void test() {
        preInvoiceDaoService.deletePreInvoice(Lists.newArrayList(111665968924913664L, 111665969067520000L), 12L);
    }
}
