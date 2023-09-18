package com.xforceplus.wapp.repository.daoExt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Describe: 针对业务单查询 扩展dao
 * PS:
 * 之前的查询代码过于复杂，并且直接在方法上注解拼接，不便于理解 ，老的查询方法：
 * {@link TXfBillDeductExtDao#queryBillPage(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal)}
 * <p>
 * 本次基于产品新的状态tab 区分，把不同的查询条件统一拆分解耦，鉴于数据表存储结果，关联查询暂时无法避免
 *
 * @Author xiezhongyong
 * @Date 2022-09-07
 */
public interface BillDeductQueryExtDao extends BaseMapper<TXfBillDeductEntity> {


    int countBill(
            @Param("ids") List<Long> ids,
            @Param("businessNo") String businessNo,
            @Param("businessType") Integer businessType,
            @Param("sellerNo") String sellerNo,
            @Param("sellerName") String sellerName,
            @Param("deductStartDate") String deductStartDate,
            @Param("deductEndDate") String deductEndDate,
            @Param("purchaserNo") String purchaserNo,
            @Param("createTimeEnd") String createTimeEnd,
            @Param("createTimeBegin") String createTimeBegin,
            @Param("settlementNo") String settlementNo,
            @Param("redNotificationNo") String redNotificationNo,
            @Param("taxRate") BigDecimal taxRate,
            @Param("queryTab") IQueryTab queryTab,
            @Param("redNotificationStatus") List<Integer>  redNotificationStatus,
            @Param("exceptionReportCodes") List<String> exceptionReportCodes,
            @Param("itemTaxRate") BigDecimal itemTaxRate
    );

    List<TXfBillDeductExtEntity> listBill(
            @Param("offset") Integer offset,
            @Param("next") Integer next,
            @Param("ids") List<Long> ids,
            @Param("businessNo") String businessNo,
            @Param("businessType") Integer businessType,
            @Param("sellerNo") String sellerNo,
            @Param("sellerName") String sellerName,
            @Param("deductStartDate") String deductStartDate,
            @Param("deductEndDate") String deductEndDate,
            @Param("purchaserNo") String purchaserNo,
            @Param("createTimeEnd") String createTimeEnd,
            @Param("createTimeBegin") String createTimeBegin,
            @Param("settlementNo") String settlementNo,
            @Param("redNotificationNo") String redNotificationNo,
            @Param("taxRate") BigDecimal taxRate,
            @Param("queryTab") IQueryTab queryTab,
            @Param("redNotificationStatus") List<Integer> redNotificationStatus,
            @Param("exceptionReportCodes") List<String> exceptionReportCodes,
            @Param("itemTaxRate") BigDecimal itemTaxRate
    );


    /**
     * 查询业务单明细关联信息
     * @param deductItemIds`
     */
    List<TXfBillItemRefDetailExtEntity> getBillItemRefDetail(@Param("deductItemIds") List<Long> deductItemIds);


    /**
     * 通过预制发票ID 获取对于的红字及业务单关系数据
     * @param preInvoiceIds
     * @return
     */
    List<TXfDeductPreInvoiceEntity> getBillRefByPreInvoiceIds(@Param("preInvoiceIds") List<Long> preInvoiceIds);

    /**
     * 通过业务单ID 获取对于的红字及业务单关系数据
     * @param billIds
     * @return
     */
    List<TXfDeductPreInvoiceEntity> getBillRefByBillIds(@Param("billIds") List<Long> billIds);
}
