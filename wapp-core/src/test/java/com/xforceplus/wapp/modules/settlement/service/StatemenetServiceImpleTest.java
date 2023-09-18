package com.xforceplus.wapp.modules.settlement.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : TODO
 * @date : 2022/10/28 11:37
 **/
public class StatemenetServiceImpleTest extends BaseUnitTest {

    @Autowired
    StatementServiceImpl statementService;

    @Test
    public void testUpdateSettlement() {
        boolean b = statementService.updateSettlementStatus(11L, TXfSettlementStatusEnum.WAIT_CONFIRM, null, null, SettlementApproveStatusEnum.APPROVING, null);
        System.err.println(b);
    }
}
