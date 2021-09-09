package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.text.ParseException;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/17.
 **/
public interface SweepCodeService {


    /**
     * 普票、汽车等签收 --查验接口
     * @param schemaLabel
     * @param invoiceNo
     * @return
     * @throws ParseException
     */
    RecordInvoiceEntity ReceiptInvoice(String schemaLabel, Map<String, Object> invoiceNo) throws  Exception;

    /**
     * 专票、通行费、电票的签收 --查验抵账表
     * @param schemaLabel
     * @param params
     * @return
     * @throws ParseException
     */
    RecordInvoiceEntity ReceiptInvoiceTwo(String schemaLabel, Map<String, Object> params) throws  Exception;





    /**
     * 根据扫描表的uuid查询抵账表的数据id
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Long getInvoiceId(String schemaLabel, String uuid);

    /**
     * 根据uuid惊醒删除（删除扫描表数据并备份）
     * @param schemaLabel
     * @param uuid
     * @return
     * @throws Exception
     */
    Boolean deleteInvoiceData(String schemaLabel, String uuid, UserEntity user) throws Exception;

    /**
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Long getInvoiceData(String schemaLabel, String uuid);
}
