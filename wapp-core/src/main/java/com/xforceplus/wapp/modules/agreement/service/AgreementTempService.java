package com.xforceplus.wapp.modules.agreement.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDetailDao;
import com.xforceplus.wapp.repository.daoExt.TXfBillDeductInvoiceDetaiExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AgreementTempService {
    @Autowired
    private TXfBillDeductInvoiceDetaiExtDao invoiceDetaiExtDao;
    @Autowired
    private TXfBillDeductInvoiceDetailDao invoiceDetailDao;

    /**
     * 修复t_xf_bill_deduct_invoice_detail.settlement_item_id字段为空
     */
    @Transactional(rollbackFor = Exception.class)
    public void repairSettlementItemId(){
        List<TXfBillDeductInvoiceDetailEntity> repairList = invoiceDetaiExtDao.queryRepairSettlementItemIdList();
        log.info("待修复结算单明细ID数据列表：{}", JSON.toJSONString(repairList));
        for (TXfBillDeductInvoiceDetailEntity repair : repairList){
            invoiceDetailDao.updateById(repair);
        }
    }


}
