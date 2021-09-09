package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface EnterPackageNumberService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map);

    /**
     * 录入邮包号
     *
     * @param rebateNos,rebateExpressno
     */
    void  inputrebateExpressno(String schemaLabel, String[] rebateNos ,String rebateExpressno ,String mailDate ,String mailCompany);

    /**
     * 查询邮包号
     *
     * @param rebateExpressno
     */
    int  queryrebateexpressno(String rebateExpressno);

    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName);
}
