package com.xforceplus.wapp.modules.InformationInquiry.service.impl;



import com.xforceplus.wapp.modules.InformationInquiry.dao.PaymentInvoiceQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.PaymentInvoiceUploadDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PaymentInvoiceQueryServiceImpl implements PaymentInvoiceQueryService {

    @Autowired
    private PaymentInvoiceQueryDao paymentInvoiceQueryDao;

    @Override
    public List<PaymentInvoiceUploadEntity> queryList(Map<String, Object> map) {
        return paymentInvoiceQueryDao.queryList(map);
    }
    @Override
    public ReportStatisticsEntity queryTotalResult( Map<String, Object> map) {

        return paymentInvoiceQueryDao.queryTotalResult(map);
    }
    @Override
    public List<PaymentInvoiceUploadEntity> queryListAll(Map<String, Object> map) {

        return paymentInvoiceQueryDao.queryListAll(map);
    }

    @Override
    public List<PaymentInvoiceUploadExcelEntity> transformExcle(List<PaymentInvoiceUploadEntity> list) {
        List<PaymentInvoiceUploadExcelEntity> list2=new ArrayList<>();
        for(int i = 0 ; i < list.size() ; i++) {
            PaymentInvoiceUploadEntity entity=list.get(i);
            PaymentInvoiceUploadExcelEntity paymentInvoiceUploadExcelEntity=new PaymentInvoiceUploadExcelEntity();

//序号
            paymentInvoiceUploadExcelEntity.setIndexNo(  String.valueOf(i+1));
            //扣款公司
            paymentInvoiceUploadExcelEntity.setJvcode(  entity.getJvcode());
            //供应商号
            paymentInvoiceUploadExcelEntity.setSupplierAssociation(  entity.getSupplierAssociation());
            //类型
            paymentInvoiceUploadExcelEntity.setCaseType(  entity.getCaseType());
            //备注
            paymentInvoiceUploadExcelEntity.setRemark( entity.getRemark());
            //换货号
            paymentInvoiceUploadExcelEntity.setExchangeNo( entity.getExchangeNo());
            //索赔号
            paymentInvoiceUploadExcelEntity.setReturnGoodsCode(  entity.getReturnGoodsCode());
            //定案日期
            paymentInvoiceUploadExcelEntity.setReturnGoodsDate(  entity.getReturnGoodsDate());
            //成本金额
            paymentInvoiceUploadExcelEntity.setReturnCostAmount(fixed(entity.getReturnCostAmount()));
            //供应商结款发票号
            paymentInvoiceUploadExcelEntity.setPaymentInvoiceNo(  entity.getPaymentInvoiceNo());
            //扣款日期
            paymentInvoiceUploadExcelEntity.setDeductionDate(  entity.getDeductionDate());
            //沃尔玛扣款发票号
            paymentInvoiceUploadExcelEntity.setPurchaseInvoiceNo(  entity.getPurchaseInvoiceNo());
            //税率
            paymentInvoiceUploadExcelEntity.setTaxRate( entity.getTaxRate());
            //含税金额
            paymentInvoiceUploadExcelEntity.setTaxAmount( fixed( entity.getTaxAmount()));
            //发送日期
            paymentInvoiceUploadExcelEntity.setSendDate( entity.getSendDate());
            //邮寄时间
            paymentInvoiceUploadExcelEntity.setMailData(  entity.getMailData());
            //快递单号
            paymentInvoiceUploadExcelEntity.setExpressNo(  entity.getExpressNo());
            //快递公司
            paymentInvoiceUploadExcelEntity.setExpressName(  entity.getExpressName());
            list2.add(paymentInvoiceUploadExcelEntity);
        }

        return list2;
    }
    public String fixed(String d) {
        BigDecimal bg = new BigDecimal(d);
        String d3 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        System.out.println(d3);
        return  d3;
    }

}
