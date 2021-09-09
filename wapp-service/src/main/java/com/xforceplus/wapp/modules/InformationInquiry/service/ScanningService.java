package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningExcelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface ScanningService {
    /**
     * 查询扫描信息
     */
    List<ScanningEntity> scanningList( Map<String, Object> map);
    /**
     * 查询扫描信息条数
     */
    Integer scanningCount( Map<String, Object> map);
    /**
     * 查询当前登录人所属机构
     */
    String serachUserOrgType(Map<String, Object> map);

    List<ScanningExcelEntity> selectScanningList(Map<String,Object> params);
}
