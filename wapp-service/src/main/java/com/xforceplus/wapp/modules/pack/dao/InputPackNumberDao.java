package com.xforceplus.wapp.modules.pack.dao;

import com.xforceplus.wapp.modules.pack.entity.InputPackNumberEntity;
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
public interface InputPackNumberDao extends BaseDao<InputPackNumberEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<InputPackNumberEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    List<InputPackNumberEntity> getListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<InputPackNumberEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    int queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 录入装箱号
     * @param bbindingNo,packingNo
     * @return
     */
    int inputpackingno(@Param("schemaLabel") String schemaLabel, @Param("bbindingNo") String bbindingNo,@Param("packingNo") String packingNo,@Param("packingAddress") String packingAddress);

    /**
     * 查询数量根据装箱号
     * @param packingNo
     * @return
     */
    int querypackingno(@Param("schemaLabel") String schemaLabel,@Param("packingNo") String packingNo);

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

}
