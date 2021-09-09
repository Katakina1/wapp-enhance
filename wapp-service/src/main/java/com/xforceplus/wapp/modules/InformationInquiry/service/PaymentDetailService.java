package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailExcelEntity1;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PaymentDetailService {

    /**
     * 查询
     * @param map
     * @return
     */
    List<PaymentDetailEntity> findPayList(Map<String, Object> map);

    /**
     * 查询订单信息条数
     * @param map
     * @return
     */
    Integer paylistCount(Map<String, Object> map);

    List<PaymentDetailExcelEntity> selectFindPayList(Map<String,Object> params);

    List<PaymentDetailExcelEntity1> selectFindPayListGF(Map<String,Object> params);

    void upload(MultipartFile file);
}
