package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 缴税账单查询回参
 * @Author: ChenHang
 * @Date: 2023/6/27 16:16
 */
@Data
public class QueryTaxBillResult implements Serializable {

     /**
      * 科目
      */
     private String accountDesc;
     /**
      * PO号
      */
     private String contractNo;
     /**
      * 报关单编号
      */
     private String customsDocNo;
     /**
      * 完税价格
      * BigDecimal类型
      */
     private BigDecimal dutiablePrice;
     /**
      * 账单id
      */
     private String id;
     /**
      * 货物名称
      */
     private String materialDesc;
     /**
      * 物料号
      */
     private String materialId;
     /**
      * 税款金额
      * BigDecimal类型
      */
     private BigDecimal taxAmt;
     /**
      * 填发日期
      */
     private String taxDate;
     /**
      * 税单号
      */
     private String taxDocNo;
     /**
      * 税号
      */
     private String taxNo;
     /**
      * 税率%
      */
     private BigDecimal taxRate;

}
