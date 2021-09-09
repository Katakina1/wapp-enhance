package com.xforceplus.wapp.modules.pack.service;


import com.xforceplus.wapp.modules.pack.entity.BindNumberExcelEntity;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.QueryFlowTypeEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface GenerateBindNumberService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<GenerateBindNumberEntity> queryList(String schemaLabel, Map<String, Object> map);


    /**
     * 生成装订册号
     *
     * @param id,bbindingNo
     */
//    void  bbindingnobyId(String schemaLabel, Long[] ids ,String bbindingNo);
    void  bbindingnobyId(String schemaLabel, Long id ,String bbindingNo);
    //查询装订册号
    GenerateBindNumberEntity  querybbindingno(Long id);

    //查询最大装订册号
    GenerateBindNumberEntity  querymaxbbindingno();

    /**
     *
     * 查询抵账表明细的信息
     * @param params
     * @return
     */
    List<GenerateBindNumberEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查抵账明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询po表明细的信息
     * @param params
     * @return
     */
    List<GenerateBindNumberEntity> getPOList(@Param("map") Map<String, Object> params);

    /**
     *
     * 查po明细的条数
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
    List<GenerateBindNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map);


    List<QueryFlowTypeEntity> searchFlowType();

    List<BindNumberExcelEntity> transformExcle(List<GenerateBindNumberEntity> list);
    Map<String, Object> parseExcel(MultipartFile multipartFile);
}
