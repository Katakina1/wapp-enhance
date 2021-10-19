package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.util.Date;

/**
 * 类描述：接收数据清洗索赔单数据结构
 *
 * @ClassName AgreementBillData
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 10:26
 */
@Data
public class ClaimBillData extends  DeductBillBaseData {
    //店铺类型（Hyper或Sams）
    private String storeType;
    // 定案日期
    private Date verdictDate;
    // 所扣发票
    private String invoiceReference;
}
