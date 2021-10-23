package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AgreementDeductScheduler {
    @PostConstruct
    public void initData() {
        Long amount = 101111223322l;
        List<DeductBillBaseData> dataList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            AgreementBillData deductBillBaseData = new AgreementBillData();
//            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)-i).negate());
//            deductBillBaseData.setReference((amount+1)+"");
//            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
//            deductBillBaseData.setBatchNo("BT112312312312");
//            deductBillBaseData.setDeductDate(new Date());
//            deductBillBaseData.setPurchaserNo("UA");
//            deductBillBaseData.setSellerNo("060751");
//            deductBillBaseData.setRemark("协议");
//            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
//            deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
//            deductBillBaseData.setAmountWithTax(deductBillBaseData.getAmountWithoutTax().add(deductBillBaseData.getTaxAmount()));
//
//            deductBillBaseData.setMemo("060751");
//            deductBillBaseData.setReasonCode("reasonCode" + i);
//            deductBillBaseData.setReferenceType("ko");
//            deductBillBaseData.setDocumentNo("DocumentNo" + i);
//            deductBillBaseData.setDocumentType("LK" );
//            deductBillBaseData.setTaxCode("tx");
//            dataList.add(deductBillBaseData);
//        }

        for (int i = 0; i < 20; i++) {
            AgreementBillData deductBillBaseData = new AgreementBillData();
            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)+i));
            deductBillBaseData.setReference((amount+1)+"");
            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
            deductBillBaseData.setBatchNo("BT112312312312");
            deductBillBaseData.setDeductDate(new Date());
            deductBillBaseData.setPurchaserNo("UA");
            deductBillBaseData.setSellerNo("060751");
            deductBillBaseData.setRemark("协议");
            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
            deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
            deductBillBaseData.setAmountWithTax(deductBillBaseData.getAmountWithoutTax().add(deductBillBaseData.getTaxAmount()));

            deductBillBaseData.setMemo("060751");
            deductBillBaseData.setReasonCode("reasonCode" + i);
            deductBillBaseData.setReferenceType("ko");
            deductBillBaseData.setDocumentNo("DocumentNo" + i);
            deductBillBaseData.setDocumentType("LK" );
            deductBillBaseData.setTaxCode("tx");
            dataList.add(deductBillBaseData);
        }
       // agreementBillService. receiveData(dataList, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);

        List<Long> ids = new ArrayList<>();
         ids.add(1191l);
        ids.add(1171l);

       agreementBillService.mergeSettlementByManual(ids, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
       // agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);

    }
    @Autowired
    private AgreementBillService agreementBillService;
   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
     }
}
