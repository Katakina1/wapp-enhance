package com.xforceplus.wapp.modules.pack.service;



import com.xforceplus.wapp.modules.pack.entity.InputPackNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.InputPackNumberExcelEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface InputPackNumberService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<InputPackNumberEntity> queryList(String schemaLabel, Map<String, Object> map);

    List<InputPackNumberEntity> getListAll(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    int queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<InputPackNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map);

    /**
     * 录入装箱号
     *
     * @param bbindingNos,packingNo
     */
    void  inputpackingno(String schemaLabel, String[] bbindingNos ,String packingNo);

    /**
     * 查询装箱号
     *
     * @param packingNo
     */
    int  querypackingno(String schemaLabel,String packingNo);

    /**
     *
     * 通过序列号查询退货表的信息
     * @param params
     * @return
     */
    List<InputPackNumberEntity> getBindingnoList(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过序列号查询退货表的条数
     * @param params
     * @return
     */
    Integer getBindingnoListCount(@Param("map") Map<String, Object> params);

    List<InputPackNumberExcelEntity> transformExcle(List<InputPackNumberEntity> inputPackNumberEntity);

    Map<String, Object> parseExcel(MultipartFile multipartFile);
}
