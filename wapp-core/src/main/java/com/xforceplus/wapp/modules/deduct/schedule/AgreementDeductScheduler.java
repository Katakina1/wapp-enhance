package com.xforceplus.wapp.modules.deduct.schedule;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.job.executor.AgreementBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.ClaimBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.EpdBillJobExecutor;
import com.xforceplus.wapp.modules.job.generator.ClaimBillJobGenerator;
import com.xforceplus.wapp.modules.job.generator.EpdBillJobGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AgreementDeductScheduler {
   // @PostConstruct
    public void initData() {
        Long no = 21001121107l;
        /**
         * 索赔单 主信息
         */
        int amount = 10;
        List<DeductBillBaseData> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ClaimBillData deductBillBaseData = new ClaimBillData();
            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)+i));
            deductBillBaseData.setBusinessNo(no+"");
            deductBillBaseData.setAmountWithTax(new BigDecimal(10));
            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
            deductBillBaseData.setBatchNo("BT112312312312");
            deductBillBaseData.setDeductDate(new Date());
            deductBillBaseData.setPurchaserNo("PT");
            deductBillBaseData.setSellerNo("172164");
            deductBillBaseData.setRemark("索赔");
            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
            no = no + 1;
            deductBillBaseData.setBusinessNo(no + StringUtils.EMPTY);
            deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
            deductBillBaseData.setStoreType("smas");
            deductBillBaseData.setVerdictDate(new Date());
            deductBillBaseData.setInvoiceReference("invoice00022222");
            dataList.add(deductBillBaseData);
        }
        List<ClaimBillItemData> res = new ArrayList<>();
        amount = 6;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("172164");
            claimBillItemData.setDeptNbr("PT");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.13"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("WI");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 5;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("172164");
            claimBillItemData.setDeptNbr("PT");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("WI");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 4;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("172164");
            claimBillItemData.setDeptNbr("PT");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("114");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 3;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("172164");
            claimBillItemData.setDeptNbr("PT");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("114");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        //receiveItemData(res, "");
        // receiveData(dataList, XFDeductionBusinessTypeEnum.CLAIM_BILL);
        // receiveDone(XFDeductionBusinessTypeEnum.CLAIM_BILL);
//        int amount = 10;
//        List<DeductBillBaseData> dataList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            AgreementBillData deductBillBaseData = new AgreementBillData();
//            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)-i).negate());
//            deductBillBaseData.setBusinessNo(idSequence.nextId().toString());
//            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.getType());
//            deductBillBaseData.setBatchNo("BT112312312312");
//            deductBillBaseData.setDeductDate(new Date());
//            deductBillBaseData.setPurchaserNo("PT");
//            deductBillBaseData.setSellerNo("172164");
//            deductBillBaseData.setRemark("索赔");
//            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
//             deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
//            deductBillBaseData.setAmountWithTax(deductBillBaseData.getAmountWithoutTax().add(deductBillBaseData.getTaxAmount()));
//
//            deductBillBaseData.setMemo("172164");
//            deductBillBaseData.setReasonCode("reasonCode" + i);
//            deductBillBaseData.setReferenceType("ko");
//            deductBillBaseData.setDocumentNo("DocumentNo" + i);
//            deductBillBaseData.setDocumentType("LK" );
//            deductBillBaseData.setTaxCode("tx");
//            dataList.add(deductBillBaseData);
//        }
        // receiveData(dataList, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
        //receiveDone(XFDeductionBusinessTypeEnum.AGREEMENT_BILL);



        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);

    }
    @Autowired
    private AgreementBillService agreementBillService;
   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
     }
}
