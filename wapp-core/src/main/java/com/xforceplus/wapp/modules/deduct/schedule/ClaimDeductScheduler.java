package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
@Slf4j
public class ClaimDeductScheduler {
    //@PostConstruct
    public void initData() {

        int no = 1001121107;
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
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("114");
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
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("114");
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
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("WI2");
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
            claimBillItemData.setSellerNo("279771");
            claimBillItemData.setDeptNbr("WI");
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

    }
    @Autowired
    private ClaimBillService claimBillService;

   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){

        claimBillService.matchClaimBill();

        claimBillService.claimMatchBlueInvoice();
    }

}
