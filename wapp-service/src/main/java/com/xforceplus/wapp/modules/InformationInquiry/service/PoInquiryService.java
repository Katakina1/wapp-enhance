package com.xforceplus.wapp.modules.InformationInquiry.service;


import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;

import java.util.List;
import java.util.Map;

public interface PoInquiryService {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<poEntity> polist(Map<String, Object> map);

    Integer polistCount(Map<String, Object> map);

    List<poExcelEntity> selectExcelpolist(Map<String,Object> params);
}
