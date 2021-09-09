package com.xforceplus.wapp.modules.monitearly.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;


/**
 * 逾期预警DAO层
 * Created by alfred.zong on 2018/04/12.
 */
@Mapper
public interface RecordInvoiceDao {

    /**根据条件筛选未认证的发票进行预警
     * @param map 查询条件 gfTaxNo 购方税号
     *                     numDate  距离逾期的天数
     *@return 快逾期未认证发票的集合信息
     */
    List<RecordInvoiceEntity> queryInvoice(Query map);

    /**根据条件筛选未认证的发票进行数量查询
     * @param map 查询条件 gfTaxNo 购方税号
     *                     numDate  距离逾期的天数
     *@return 快逾期未认证发票的数量
     */
    List<RecordInvoiceEntity> queryInvoiceToExcel(Map<String,Object> map);

    /**根据条件筛选未认证的发票进行数量查询
     * @param map 查询条件 gfTaxNo 购方税号
     *                     numDate  距离逾期的天数
     *@return 快逾期未认证发票的数量
     */
    ReportStatisticsEntity geyQueryInvoiceNum(Query map);

    /**根据传过来的实体对象进行批量更新操作
     * @param list 更新条件 Id 发票的ID
     *                     SchemaLabel  分库的参数
     *                     Userid   当前用户的ID
     *                     Orgid   购方税号
     *@return 更新的数量
     */
    Integer  updateInvoiceList(@Param("list")List<UserTaxnoEntity> list);

    Integer  updateInvoice(UserEntity userEntity);

    String getRzhBDate(@Param("schemaLabel") String schemaLabel ,@Param("ogid")String ogid);
}
