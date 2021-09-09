package com.xforceplus.wapp.modules.monitearly.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import java.util.List;
import java.util.Map;

/**
 * 普票跨年度预警Service接口
 * Created by alfred.zong on 2018/04/16.
 */
public interface DefinitiveStrideYearCopService {

    /**  根据多条件查询普票跨年度发票进行预警
     * @param map 查询条件    gfTaxNo 购方税号
     *                        signForDate1 签收日期起
     *                        signForDate2 签收日期止
     *                        billingDate1 开票日期起
     *                        billingDate2 开票日期止
     *                        invoiceNo 发票号码
     * @return  普票跨年度预警发票的集合
     */
    PagedQueryResult<RecordInvoiceEntity> queryDefinitiveList(Query map);

    /**  根据多条件查询普票跨年度发票用于导出
     * @param map 查询条件    gfTaxNo 购方税号
     *                        signForDate1 签收日期起
     *                        signForDate2 签收日期止
     *                        billingDate1 开票日期起
     *                        billingDate2 开票日期止
     *                        invoiceNo 发票号码
     * @return  普票跨年度预警发票的集合
     */
    List<RecordInvoiceEntity> queryDefinitiveInfoList(Map<String,Object> map);

}
