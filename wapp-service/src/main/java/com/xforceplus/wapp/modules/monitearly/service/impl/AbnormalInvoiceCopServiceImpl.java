package com.xforceplus.wapp.modules.monitearly.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.monitearly.dao.AbnormalInvoiceCopDao;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.service.AbnormalInvoiceCopService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 异常发票预警业务层
 * Created by alfred.zong on 2018/04/13.
 */
@Service
@Transactional
public class AbnormalInvoiceCopServiceImpl  implements AbnormalInvoiceCopService {

    private AbnormalInvoiceCopDao abnormalinvoice;

    @Autowired
    public AbnormalInvoiceCopServiceImpl(AbnormalInvoiceCopDao abnormalinvoice){
        this.abnormalinvoice=abnormalinvoice;
    }

    /**  根据多条件查询异常发票进行预警
     * @param map 查询条件 gfTaxNo 购方税号
     *                      firstDate 异常日期起
     *                      lastDate 异常日期止
     *                      invoiceNo 发票号码
     *                      invoiceStatus 异常状态码
     * @return 异常发票集合信息
     */
    @Override
    public  PagedQueryResult<RecordInvoiceEntity> queryAbnormalInvoice(Query map){

        final PagedQueryResult<RecordInvoiceEntity> pagedQueryResultlist = new PagedQueryResult<>();
        final ReportStatisticsEntity result = abnormalinvoice.queryAbnormalNumber(map);

        //需要返回的集合
        List<RecordInvoiceEntity> abnormalArrayList = newArrayList();
        if (result.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            abnormalArrayList = abnormalinvoice.queryAbnormalInvoice(map);
        }

        pagedQueryResultlist.setTotalCount(result.getTotalCount());
        pagedQueryResultlist.setResults(abnormalArrayList);
        pagedQueryResultlist.setTotalAmount(result.getTotalAmount());
        pagedQueryResultlist.setTotalTax(result.getTotalTax());
        return pagedQueryResultlist;
    }

    /**  根据多条件查询异常发票用于导出
     * @param map 查询条件 gfTaxNo 购方税号
     *                      firstDate 异常日期起
     *                      lastDate 异常日期止
     *                      invoiceNo 发票号码
     *                      invoiceStatus 异常状态码
     * @return 异常发票集合信息
     */
    @Override
    public List<RecordInvoiceEntity> queryAbnormalInvoicelist(Map<String,Object> map){
        return abnormalinvoice.queryAbnormalInvoicelist(map);
    }
}
