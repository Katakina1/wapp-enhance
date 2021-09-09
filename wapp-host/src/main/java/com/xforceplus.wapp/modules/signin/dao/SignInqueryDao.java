package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@Mapper
public interface SignInqueryDao {
    /**
     * 根据登录人的id获取关联的税号--页面购方税号下拉框数据
     * @param schemaLabel
     * @param userId
     * @return List
     */
    List<OptionEntity> searchGf(@Param("schemaLabel") String schemaLabel,@Param("userId") Long userId);

    /**
     * 根据条件统计所有的数据总数
     * @param schemaLabel
     * @param query
     * @return int
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 根据条件数据查询扫描表所有符合条件分页数据
     * @param schemaLabel
     * @param query
     * @return List
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 根据条件数据查询扫描表所有符合条件数据（导出使用）
     * @param schemaLabel
     * @param params
     * @return List
     */
    List<RecordInvoiceEntity> queryAllList(@Param("schemaLabel") String schemaLabel,@Param("params") Map<String, Object> params);
}
