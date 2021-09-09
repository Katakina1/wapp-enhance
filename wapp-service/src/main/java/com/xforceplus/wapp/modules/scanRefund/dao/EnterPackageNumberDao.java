package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface EnterPackageNumberDao extends BaseDao<EnterPackageNumberEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 录入邮包号
     * @param rebateNos,rebateExpressno
     * @return
     */
    int inputrebateExpressno(@Param("schemaLabel") String schemaLabel, @Param("rebateNos") String[] rebateNos,@Param("rebateExpressno") String rebateExpressno,@Param("mailDate") String mailDate,@Param("mailCompany") String mailCompany);

    /**
     * 查询数量根据邮包号
     * @param rebateExpressno
     * @return
     */
    int queryrebateexpressno(@Param("rebateExpressno") String rebateExpressno);

    int queryrebateNo(@Param("rebateNo") String rebateNo);

    /**
     * 录入邮包号
     * @param rebateNo
     * @return
     */
    int inputrebateExpressnoBatch(@Param("rebateNo") String rebateNo,@Param("rebateExpressno") String rebateExpressno,@Param("mailCompany") String mailCompany,@Param("mailDate") String mailDate);

}
