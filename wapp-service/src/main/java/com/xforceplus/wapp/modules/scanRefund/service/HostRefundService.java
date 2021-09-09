package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface HostRefundService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<GroupRefundEntity> queryList(Map<String, Object> map);
    List<GroupRefundEntity> queryRzhList(Map<String, Object> map);

    /**
     * 改变状态
     * @param uuid
     * @return
     */
    void  inputrefundyesno(String uuid,String refundReason);


    /**
     * 查询uuid
     * @param id
     * @return
     */
    List<GroupRefundEntity> queryuuid(Long id);


    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map);
    ReportStatisticsEntity queryRzhTotalResult(Map<String, Object> map);

    /**
     * 查看是否删除
     * @param uuid
     * @return
     */
    GroupRefundEntity queryisdel(String uuid);

    Integer getuuidCount(String uuid);

    int saveInvoice(GenerateBindNumberEntity entity);

    GenerateBindNumberEntity queryListUuid(String uuid);


}
