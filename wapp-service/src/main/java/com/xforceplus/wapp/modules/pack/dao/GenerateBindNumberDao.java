package com.xforceplus.wapp.modules.pack.dao;

import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.QueryFlowTypeEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface GenerateBindNumberDao extends BaseDao<GenerateBindNumberEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<GenerateBindNumberEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 生成装订册号
     * @param id,bindingNo
     * @return
     */
//    int bbindingnobyId(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids,@Param("bbindingNo") String bbindingNo);
    int bbindingnobyId(@Param("schemaLabel") String schemaLabel, @Param("id") Long id,@Param("bbindingNo") String bbindingNo);

    //查询装订册号
    GenerateBindNumberEntity querybbindingno(@Param("id")Long id);

    //查询最大装订册号
    GenerateBindNumberEntity querymaxbbindingno();

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<GenerateBindNumberEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询PO表明细的信息
     * @param params
     * @return
     */
    List<GenerateBindNumberEntity> getPOList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查PO明细的条数
     * @param params
     * @return
     */
    Integer getPOListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询索赔表明细的信息
     * @param params
     * @return
     */
    List<GenerateBindNumberEntity> getClaimList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查索赔明细的条数
     * @param params
     * @return
     */
    Integer getClaimListCount(@Param("map") Map<String, Object> params);

        /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<GenerateBindNumberEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);


    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询业务类型
     * @param
     * @return
     */
    List<QueryFlowTypeEntity> searchFlowType();


}
