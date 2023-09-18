package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class CommAgreementBillServiceTest extends BaseUnitTest {

	@Autowired
    private CommAgreementService commAgreementService;
    @Mock
    private TXfPreInvoiceItemDao xfPreInvoiceItemDao;
    @Autowired
    private AgreementBillService agreementBillService;

    @Test
    public void test_DestroyAgreementSettlement() {
        commAgreementService.destroyAgreementSettlement(108767563588272128L);
    }

    @Test
    public void testAgainSplitPreInvoice() {
        LambdaQueryWrapper<TXfPreInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfPreInvoiceItemEntity::getPreInvoiceId, 42815579128119296L);
        List<TXfPreInvoiceItemEntity> preInvoiceItemEntityList = xfPreInvoiceItemDao.selectList(queryWrapper);
        Assert.assertNotNull(preInvoiceItemEntityList);

        commAgreementService.againSplitPreInvoice(42815579128119296L, preInvoiceItemEntityList);
        Mockito.verify(commAgreementService, Mockito.times(1)).againSplitPreInvoice(42815579128119296L, preInvoiceItemEntityList);
    }

    @Test
    public void testSplitPreInvoice() {
        commAgreementService.splitPreInvoice(42815579128119296L);
        Mockito.verify(commAgreementService, Mockito.times(1)).splitPreInvoice(42815579128119296L);
    }
    
/*    @Test
    public void test_mergeEPDandAgreementSettlement() {
    	agreementBillService.mergeEPDandAgreementSettlement(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, 
    			TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
    }*/
}
