package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/14
 * 已签收未认证报表
 */
@Mapper
public interface SignedUncertifiedDao {

    /**
     * 获取签收未认证统计数据
     *
     * @param map 查询条件
     * @return 签收未认证统计数据
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取签收未认证统计的数量
     *
     * @param map 查询条件
     * @return 签收未认证统计数量
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取已签收未认证报表金额 税额
     *
     * @param map 查询条件
     * @return 已签收未认证报表
     */
    ReportStatisticsEntity queryTotalResult(@Param("condition") Map<String, Object> map, @Param("schemaLabel") String schemaLabel);


    /**
     * 查询购方名称
     *
     * @param userId 登陆者id
     * @return List<OptionEntity>
     */
    List<OptionEntity> searchGf(@Param("userId") Long userId, @Param("schemaLabel") String schemaLabel);

    /**
     * 查询销方名称
     *
     * @param map 查询条件
     * @return 销方名称
     */
    List<String> searchXf(@Param("condition") Map<String, Object> map, @Param("schemaLabel") String schemaLabel);


}
