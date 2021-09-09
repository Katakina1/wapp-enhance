package com.xforceplus.wapp.modules.invoiceBorrow.dao;

import com.xforceplus.wapp.modules.invoiceBorrow.entity.BorrowEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 发票借阅Dao
 */
@Mapper
public interface InvoiceBorrowDao {

    /**
     * 获得发票借阅数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getInvoiceBorrowCount(Map<String, Object> map);

    /**
     * 获得发票借阅集合
     * @param map 查询条件
     * @return 发票借阅集合
     */
    List<ComprehensiveInvoiceQueryEntity> queryInvoiceBorrowList(Map<String, Object> map);

    /**
     * 获得发票借阅数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getBorrowRecordCount(Map<String, Object> map);

    /**
     * 获得发票借阅集合
     * @param map 查询条件
     * @return 发票借阅集合
     */
    List<BorrowEntity> queryBorrowRecordList(Map<String, Object> map);

    /**
     * 获取所有金额，税额合计
     * @param map 参数
     * @return 金额 税额汇总
     */
    Map<String, BigDecimal> getInvoiceBorrowSumAmount(Map<String, Object> map);

    /**
     * 保存借阅或归还记录
     * @param borrowEntity 借阅人，借阅时间，借阅原因,操作类型
     * @return
     */
    int save(@Param("entity")BorrowEntity borrowEntity);

    /**
     * 修改发票借阅状态(0-未借阅，1-已借阅)
     * @param status 状态
     * @param borrowDate 借阅时间
     * @return
     */
    int updateBorrowStatus(@Param("uuid")String uuid,@Param("status")String status,@Param("borrowDate")String borrowDate);
    int updateBorrowjy(@Param("uuid")String uuid,@Param("status")String status,@Param("borrowDate")String borrowDate,@Param("borrowUser")String borrowUser,@Param("borrowReason")String borrowReason,@Param("borrowDept")String borrowDept);
    int updateBorrowgh(@Param("uuid")String uuid,@Param("status")String status,@Param("borrowReturnDate")String borrowReturnDate,@Param("borrowReturnUser")String borrowReturnUser);

    ComprehensiveInvoiceQueryEntity getDataByuuid(@Param("uuid")String uuid);
}
