package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface OverseasinvoiceService {
    /**
     * 查询海外发票信息
     * @param map
     * @return
     */
    List<OverseasInvoiceEntity> list(Map<String, Object> map);
    /**
     * 查询海外发票信息条数
     * @param map
     * @return
     */
    Integer listCount(Map<String, Object> map);

    /**
     * 海外发票导入
     * @return
     */
    Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName);

    Integer delete(String loginname);

    List<OverseasInvoiceEntity> failedlist(String loginname);
}
