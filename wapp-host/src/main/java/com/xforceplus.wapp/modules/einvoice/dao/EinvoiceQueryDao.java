package com.xforceplus.wapp.modules.einvoice.dao;

import com.xforceplus.wapp.modules.einvoice.entity.EinvoiceQueryEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.sys.dao.SysBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Max.han on 2018/04/12.
 * 电票查询控制层
 */
@Mapper
public interface EinvoiceQueryDao extends SysBaseDao<EinvoiceQueryEntity> {

    /**
     * 根据条件查询电票信息列表
     *
     * @param query       ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询的结果
     */
    List<ElectronInvoiceEntity> queryInvoiceList(@Param("schemaLabel") String schemaLabel, @Param("query") EinvoiceQueryEntity query);

    /**
     * 根据条件查询电票信息列表的总条数
     *
     * @param query       ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 总条数
     */
    int queryInvoiceListCount(@Param("schemaLabel") String schemaLabel, @Param("query") EinvoiceQueryEntity query);

    /**
     * 查询需要导出的电票数据
     *
     * @param query       ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询的结果
     */
    List<ElectronInvoiceEntity> queryInvoiceListForExport(@Param("schemaLabel") String schemaLabel, @Param("query") EinvoiceQueryEntity query);

    /**
     * 根据当前登录用户的id，查询用户所关联的税号
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param userId      当前登录用户的id
     * @return
     */
    List<String> selectGfTaxNo(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);
}
