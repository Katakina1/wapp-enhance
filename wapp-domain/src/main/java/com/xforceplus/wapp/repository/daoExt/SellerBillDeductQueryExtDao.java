package com.xforceplus.wapp.repository.daoExt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import com.xforceplus.wapp.repository.entity.TXfBillItemRefDetailExtEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Describe: 供应商-业务单查询
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
public interface SellerBillDeductQueryExtDao extends BaseMapper<TXfBillDeductEntity> {


    int countBill(
            @Param("ids") List<Long> ids,
            @Param("businessNo") String businessNo,
            @Param("businessType") Integer businessType,
            @Param("sellerNo") String sellerNo,
            @Param("deductDateStart") String deductDateStart,
            @Param("deductDateEnd") String deductDateEnd,
            @Param("deductInvoice") String deductInvoice,
            @Param("verdictDateStart") String verdictDateStart,
            @Param("verdictDateEnd") String verdictDateEnd,
            @Param("purchaserNo") String purchaserNo,
            @Param("createTimeStart") String createTimeStart,
            @Param("createTimeEnd") String createTimeEnd,
            @Param("settlementNo") String settlementNo,
            @Param("redNotificationNo") String redNotificationNo,
            @Param("taxRate") BigDecimal taxRate,
            @Param("queryTab") IQueryTab queryTab,
            @Param("redNotificationStatus") List<Integer> redNotificationStatus,
            @Param("exceptionReportCodes") List<String> exceptionReportCodes,
            @Param("settlementStatusList") List<Integer> settlementStatusList
    );

    List<TXfBillDeductExtEntity> listBill(
            @Param("offset") Integer offset,
            @Param("next") Integer next,
            @Param("ids") List<Long> ids,
            @Param("businessNo") String businessNo,
            @Param("businessType") Integer businessType,
            @Param("sellerNo") String sellerNo,
            @Param("deductDateStart") String deductDateStart,
            @Param("deductDateEnd") String deductDateEnd,
            @Param("deductInvoice") String deductInvoice,
            @Param("verdictDateStart") String verdictDateStart,
            @Param("verdictDateEnd") String verdictDateEnd,
            @Param("purchaserNo") String purchaserNo,
            @Param("createTimeStart") String createTimeStart,
            @Param("createTimeEnd") String createTimeEnd,
            @Param("settlementNo") String settlementNo,
            @Param("redNotificationNo") String redNotificationNo,
            @Param("taxRate") BigDecimal taxRate,
            @Param("queryTab") IQueryTab queryTab,
            @Param("redNotificationStatus") List<Integer> redNotificationStatus,
            @Param("exceptionReportCodes") List<String> exceptionReportCodes,
            @Param("settlementStatusList") List<Integer> settlementStatusList
    );


}
