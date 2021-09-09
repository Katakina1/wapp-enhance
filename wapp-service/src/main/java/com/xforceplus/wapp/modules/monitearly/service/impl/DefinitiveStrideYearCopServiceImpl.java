package com.xforceplus.wapp.modules.monitearly.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.monitearly.dao.DefinitiveStrideYearCopDao;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.service.DefinitiveStrideYearCopService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 普票跨年度预警业务层
 * Created by alfred.zong on 2018/04/16.
 */
@Service
public class DefinitiveStrideYearCopServiceImpl  implements DefinitiveStrideYearCopService {

    private DefinitiveStrideYearCopDao definitiveStrideYearCopDao;

    @Autowired
    public DefinitiveStrideYearCopServiceImpl(DefinitiveStrideYearCopDao definitiveStrideYearCopDao){
        this.definitiveStrideYearCopDao=definitiveStrideYearCopDao;
    }

    /**  根据多条件查询普票跨年度发票进行预警
     * @param map 查询条件    gfTaxNo 购方税号
     *                        signForDate1 签收日期起
     *                        signForDate2 签收日期止
     *                        billingDate1 开票日期起
     *                        billingDate2 开票日期止
     *                        invoiceNo 发票号码
     * @return  普票跨年度预警发票的集合
     */
    @Override
    public PagedQueryResult<RecordInvoiceEntity> queryDefinitiveList(Query map){
        //创建一个PagedQueryResult集合
        final PagedQueryResult<RecordInvoiceEntity> pagedQueryResultlist = new PagedQueryResult<>();

        //查询有多少条记录
        final ReportStatisticsEntity result =definitiveStrideYearCopDao.queryDefinitiveNumber(map);

        //创建一个接收返回结果的一个集合
        List<RecordInvoiceEntity>  definitiveList= newArrayList();
        if(result.getTotalCount() >0){
            definitiveList=definitiveStrideYearCopDao.queryDefinitiveList(map);
        }

        pagedQueryResultlist.setTotalCount(result.getTotalCount());
        pagedQueryResultlist.setResults(definitiveList);
        pagedQueryResultlist.setTotalAmount(result.getTotalAmount());
        pagedQueryResultlist.setTotalTax(result.getTotalTax());

        return pagedQueryResultlist;
    }

    /**  根据多条件查询普票跨年度发票用于导出
     * @param map 查询条件    gfTaxNo 购方税号
     *                        signForDate1 签收日期起
     *                        signForDate2 签收日期止
     *                        billingDate1 开票日期起
     *                        billingDate2 开票日期止
     *                        invoiceNo 发票号码
     * @return  普票跨年度预警发票的集合
     */
    @Override
    public List<RecordInvoiceEntity> queryDefinitiveInfoList(Map<String,Object> map){
       return definitiveStrideYearCopDao.queryDefinitiveInfoList(map);
    }
}
