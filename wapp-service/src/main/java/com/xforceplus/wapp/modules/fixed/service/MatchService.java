package com.xforceplus.wapp.modules.fixed.service;

import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MatchService {
    /**
     * 获取订单信息列表
     * @param params
     * @return
     */
    List<OrderEntity> queryOrderList(Map<String, Object> params);

    /**
     * 获取税率信息
     * @return
     */
    List<SelectionOptionEntity> getRate();

    /**
     * 获取发票信息
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    InvoiceEntity searchInvoice(String invoiceCode, String invoiceNo);


    List<InvoiceEntity> searchInvoiceQuery(String invoiceQueryDate1, String invoiceQueryDate2, String invoiceNo,String gfTaxNo,String orgid);

    /**
     * 保存匹配信息
     * @param match
     */
    void submitAll(MatchQueryEntity match);

    /**
     * 上传问题单文件
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * 保存问题单信息
     * @param entity
     * @return
     */
    Integer saveFile(FileEntity entity);



}
