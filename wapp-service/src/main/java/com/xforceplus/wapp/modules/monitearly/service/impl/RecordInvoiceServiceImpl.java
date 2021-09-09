package com.xforceplus.wapp.modules.monitearly.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.monitearly.dao.RecordInvoiceDao;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.monitearly.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;


import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 逾期预警业务层
 * Created by alfred.zong on 2018/04/12.
 */
@Service
@Transactional
public class RecordInvoiceServiceImpl implements RecordInvoiceService {

    private RecordInvoiceDao recordInvoicedao;

    @Autowired
    public  RecordInvoiceServiceImpl(RecordInvoiceDao recordInvoicedao){
        this.recordInvoicedao=recordInvoicedao;

    }

    /**根据条件筛选未认证的发票进行数量查询
     * @param map 查询条件 gfTaxNo 购方税号
     *                     numDate  距离逾期的天数
     *@return 快逾期未认证发票的数量
     */
    @Override
    public PagedQueryResult<RecordInvoiceEntity> queryInvoice(Query map) {
        final PagedQueryResult<RecordInvoiceEntity> pagedQueryResultlist = new PagedQueryResult<>();
        final ReportStatisticsEntity result = recordInvoicedao.geyQueryInvoiceNum(map);

        //需要返回的集合
        List<RecordInvoiceEntity> recordArrayList = newArrayList();
        if (result.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            recordArrayList = recordInvoicedao.queryInvoice(map);
        }
        pagedQueryResultlist.setTotalCount(result.getTotalCount());
        pagedQueryResultlist.setResults(recordArrayList);
        pagedQueryResultlist.setTotalAmount(result.getTotalAmount());
        pagedQueryResultlist.setTotalTax(result.getTotalTax());

        return pagedQueryResultlist;
    }

    @Override
    public List<RecordInvoiceEntity> queryInvoiceToExcel(Map<String, Object> map) {
        return recordInvoicedao.queryInvoiceToExcel(map);
    }

    /**根据传过来的实体对象进行批量更新操作
     * @param list 更新条件 Id 发票的ID
     *                     SchemaLabel  分库的参数
     *                     Userid   当前用户的ID
     *                     Orgid   购方税号
     *@return 更新的数量
     */
    @Override
    public Integer  updateInvoiceList(List<UserTaxnoEntity> list){

        return recordInvoicedao.updateInvoiceList(list);
    }


    @Override
    public Integer updateInvoice(UserEntity userEntity) {

        return recordInvoicedao.updateInvoice(userEntity);
    }


    public String getRzhBDate(@Param("schemaLabel") String schemaLabel ,@Param("ogid")String ogid) {
        return recordInvoicedao.getRzhBDate(schemaLabel,ogid);
    }
}
