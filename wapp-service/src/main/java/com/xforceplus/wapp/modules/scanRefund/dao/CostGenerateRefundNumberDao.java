package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface CostGenerateRefundNumberDao extends BaseDao<GenerateRefundNumberEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<GenerateRefundNumberEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
  /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<GenerateRefundNumberEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    List<GenerateRefundNumberEntity> queryListAlls(@Param("map") Map<String, Object> map);

    List<GenerateRefundNumberEntity> epsDetaList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);



    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 生成退单号
     * @param id,rebateNo
     * @return
     */
    int rebatenobyId(@Param("schemaLabel") String schemaLabel, @Param("id") Long id, @Param("rebateNo") String rebateNo);


    //查询退单号
    GenerateRefundNumberEntity queryrebateno(@Param("id") Long id);

  //查询uuid
  GenerateRefundNumberEntity queryuuid1(@Param("id") Long id);
  int rebatenobyuuid(@Param("uuid") String uuid);


  //查询最大退单号
    GenerateRefundNumberEntity querymaxrebateno();

    //eps单号查询id
    List<GenerateRefundNumberEntity> queryepsno(@Param("epsNo") String epsNo);
}
