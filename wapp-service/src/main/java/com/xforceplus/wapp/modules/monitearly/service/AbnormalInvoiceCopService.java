package com.xforceplus.wapp.modules.monitearly.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import java.util.List;
import java.util.Map;

/**
 * 异常发票预警Service接口
 * Created by alfred.zong on 2018/04/13.
 */
public interface AbnormalInvoiceCopService {

    /**  根据多条件查询异常发票进行预警
     * @param map 查询条件 gfTaxNo 购方税号
     *                      firstDate 异常日期起
     *                      lastDate 异常日期止
     *                      invoiceNo 发票号码
     *                      invoiceStatus 异常状态码
     * @return 异常发票集合信息
     */
     PagedQueryResult<RecordInvoiceEntity> queryAbnormalInvoice(Query map);

    /**  根据多条件查询异常发票用于导出
     * @param map 查询条件 gfTaxNo 购方税号
     *                      firstDate 异常日期起
     *                      lastDate 异常日期止
     *                      invoiceNo 发票号码
     *                      invoiceStatus 异常状态码
     * @return 异常发票集合信息
     */
    List<RecordInvoiceEntity> queryAbnormalInvoicelist(Map<String,Object> map);
}
