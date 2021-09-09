package com.xforceplus.wapp.modules.fixed.service;



import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface InvoiceImportExportService {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<InvoiceImportAndExportEntity> invoiceImportAndExportlist(Map<String, Object> map);

    List<InvoiceImportAndExportEntity> invoiceImportAndExportlistAll(Map<String, Object> map);

    Integer invoiceImportAndExportlistCount(Map<String, Object> map);

    Map<String,Object> importInvoice(Map<String, Object> params, MultipartFile multipartFile);
}
