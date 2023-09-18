package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
* <p>
* 业务单发票明细关系表 Mapper 接口
* t_xf_bill_deduct_invoice_detail
* </p>
*
* @author malong@xforceplus.com
* @since 2022-09-27
*/
public interface TXfBillDeductInvoiceDetailDao extends BaseMapper<TXfBillDeductInvoiceDetailEntity> {

    @Select("select count(*) from t_xf_bill_deduct_invoice_detail bdid " +
            " inner join t_xf_bill_deduct bd " +
            "on bdid.deduct_id = bd.id " +
            "where bdid.status = 0 " +
            "and bd.ref_settlement_no = #{settlementNo}")
    public Integer queryCountJoin(@Param("settlementNo") String settlementNo);
    
    /**
     * <pre>
     * 根据发票明细ID查询
     * 只查询status= 0 状态（正常）
     * </pre>
     * @param invoiceItemId
     * @return
     */
    @Select("SELECT * from t_xf_bill_deduct_invoice_detail where invoice_detail_id = #{invoiceItemId} and status = 0")
    List<TXfBillDeductInvoiceDetailEntity> queryByInvoiceItemId(Long invoiceItemId);
    
    /**
     * <pre>
     * 根据发票明细ID查询
     * 只查询status= 0 状态（正常）
     * </pre>
     * @param invoiceItemId
     * @return
     */
    @Select("<script> "
    		+ "SELECT * from t_xf_bill_deduct_invoice_detail where status = 0 "
    		+ " and invoice_detail_id in <foreach collection=\"invoiceItemIds\" close=\")\" open=\"(\" separator=\",\" item=\"id\"> #{id}</foreach> "
    		+ "</script> ")
    List<TXfBillDeductInvoiceDetailEntity> queryByInvoiceItemIds(@Param("invoiceItemIds") List<Long> invoiceItemIds);
    
    /**
     * <pre>
     * 根据发票明细ID查询
     * 只查询status= 0 状态（正常）
     * </pre>
     * @param invoiceItemId
     * @return
     */
    @Select("SELECT * from t_xf_bill_deduct_invoice_detail where deduct_id = #{deductId} and status = 0")
    List<TXfBillDeductInvoiceDetailEntity> queryBydeductId(Long deductId);
    
    /**
     * <pre>
     * 根据发票明细ID查询
     * 只查询status= 0 状态（正常）
     * </pre>
     * @param invoiceItemId
     * @return
     */
    @Select("<script> "
    		+ "SELECT * from t_xf_bill_deduct_invoice_detail where status = 0 and deduct_id in <foreach collection=\"deductIds\" close=\")\" open=\"(\" separator=\",\" item=\"id\"> #{id}</foreach>"
    		+ "</script> ")
    List<TXfBillDeductInvoiceDetailEntity> queryBydeductIds(@Param("deductIds") List<Long> deductIds);


}
