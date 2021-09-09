package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;

import java.util.List;
import java.util.Map;


public interface CostListService {
    /**
     * 查询扫描信息
     */
    List<ScanningEntity> scanningList(Map<String, Object> map);
    /**
     * 查询扫描信息条数
     */
    Integer scanningCount(Map<String, Object> map);
}
