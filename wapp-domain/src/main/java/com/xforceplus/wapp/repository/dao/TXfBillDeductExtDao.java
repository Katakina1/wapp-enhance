package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
* <p>
* 业务单据信息 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-12
*/
public interface TXfBillDeductExtDao extends BaseMapper<TXfBillDeductEntity> {
    /**
     *查询折扣单列表
     * @param startDate
     * @param limit
     * @param billType
     * @param status
     * @return
     */
       @Select("<script>"+"select top ${limit} * from t_xf_bill_deduct " +
            "where id> #{id} " +
               "<if test='startDate!=null'>"+
               "and  create_time >= #{startDate} "+
               "</if>"+
               "  and business_type = #{billType} and status = #{status} and ref_settlement_no = '' " +
            "order by id asc </script>")
    List<TXfBillDeductEntity> queryUnMatchBill(@Param("id") Long id,
                                               @Param("startDate") Date startDate,
                                               @Param("limit") Integer limit,
                                               @Param("billType") Integer billType,
                                               @Param("status") Integer status );
    /**
     * check 当月的匹配结果，定时处理，如果存在为匹配的，进行重新追加或清理
     * @param startDate
     */
    @Select("select bill.purchaser_no,bill.seller_no,bill.tax_rate,(bill.amount_without_tax-res.amount) as amount_without_tax"+
            "from (select deduct_id, sum(use_amount) amount\n"+
            "      from t_xf_bill_deduct_item_ref\n"+
            "      where create_time => #{startDate} and create_time <= #{endDate} \n"+
            "      group by deduct_id) res,\n"+
            "     t_xf_bill_deduct bill\n"+
            "where bill.id = res.deduct_id and bill.business_type = 1\n"+
            "  and bill.amount_without_tax <> res.amount \n"+
            "\n"+
            "\n")
    public List<TXfBillDeductEntity> checkClaimMatch(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    /** 查询超期正数的勾选和税率
     * @param referenceDate
     * @return
     */
    @Select("select sum(deduct.amount_without_tax) as amount_without_tax,sum(deduct.amount_with_tax) as amount_with_tax,sum(deduct.tax_amount) as tax_amount , deduct.seller_no,deduct.purchaser_no, deduct.tax_rate\n" +
            "from t_xf_bill_deduct deduct left join t_xf_overdue overdue on overdue.seller_no = deduct.seller_no\n" +
            "where  deduct.deduct_date >  IIF(  overdue.overdue_day is null, convert(varchar(10),DATEADD(d, 0 - #{referenceDate}, GETDATE()),120), convert(  varchar(10),DATEADD(d, 0 - (overdue.overdue_day), GETDATE()))) \n" +
            "  and business_type = #{type} and status = #{status} and amount_without_tax > 0 and  lock_flag = #{flag}\n" +
            "group by deduct.purchaser_no, deduct.seller_no,deduct.tax_rate\n")
    public List<TXfBillDeductEntity> querySuitablePositiveBill(@Param("referenceDate") Integer referenceDate,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag);

    /**
     *  更加ID列表查询同购销对 同税率的 单据合并信息
     * @param ids
     * @param type
     * @param status
     * @param flag
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,sum(amount_with_tax) as amount_with_tax,seller_no,purchaser_no, tax_rate   from t_xf_bill_deduct where id in ${ids}  and business_type = #{type} and status = #{status} and   lock_flag = #{flag} group by purchaser_no, seller_no,tax_rate")
    public List<TXfBillDeductEntity> querySuitableBillById(@Param("ids") String ids,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag);

    /**
     *  更加ID列表查询同购销对 同税率的 单据合并信息
     * @param ids
     * @param type
     * @param status
     * @param flag
     * @return
     */
    @Update("update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo}  where id  in  ${ids} and status = #{status} and  lock_flag = #{flag} and ref_settlement_no = ''")
    public List<TXfBillDeductEntity> updateBillById(@Param("ids") String ids,@Param("settlementNo") String settlementNo,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag,@Param("targetStatus") Integer targetStatus);



    /**
     *  查询同维度下 负数金额 的总和
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount, sum(amount_with_tax) as amount_with_tax,seller_no,purchaser_no, tax_rate   from t_xf_bill_deduct where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and  lock_flag = #{flag} and ref_settlement_no = '' and tax_rate = #{taxRate} and amount_without_tax < 0")
    public TXfBillDeductEntity querySpecialNegativeBill(@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("type") Integer type,@Param("status") Integer status,  @Param("flag") Integer flag);

    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount  from t_xf_bill_deduct   where ref_settlement_no  = #{settlementNo} and status = #{status} and  lock_flag = #{flag}")
    public TXfBillDeductEntity queryBillBySettlementNo(@Param("settlementNo") String settlementNo, @Param("status") Integer status, @Param("flag") Integer flag );


    @Update("update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo}  where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax < 0 and ref_settlement_no = '' and  lock_flag = #{flag} ")
    public int updateMergeNegativeBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    @Update(" update t_xf_bill_deduct set status =#{targetStatus},ref_settlement_no=#{settlementNo}  where purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and create_time >= #{referenceDate} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax > 0 and ref_settlement_no = '' and  lock_flag = #{flag}")
    public int updateMergePositiveBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("referenceDate") Date referenceDate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    /**
     * 索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no   from t_xf_bill_deduct where    business_type = #{type} and status = #{status}  group by purchaser_no,seller_no ")
    public List<TXfBillDeductEntity> querySuitableClaimBill( @Param("type")Integer type,@Param("status")Integer status);

    /**
     * 索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Update(" update t_xf_bill_deduct set status = #{targetStatus},ref_settlement_no=#{settlementNo}    where  purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and  business_type = #{type} and status = #{status} ")
    public Integer updateSuitableClaimBill(@Param("type")Integer type, @Param("status")Integer status, @Param("targetStatus") Integer targetStatus, @Param("settlementNo")String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo);

    @Select("<script>"+
            "select d.id, d.business_no, d.business_type, d.ref_settlement_no, d.verdict_date, d.deduct_date, d.deduct_invoice, d.tax_rate, d.agreement_reason_code, d.agreement_reference, d.agreement_tax_code, d.agreement_memo, d.agreement_document_number, d.agreement_document_type, d.tax_amount, d.remark, d.status, d.create_time, d.update_time, d.purchaser_no, d.seller_no, d.seller_name, d.amount_without_tax, d.amount_with_tax, d.lock_flag, d.batch_no, d.source_id, d.purchaser_name,s.invoice_type," +
            "sum(di.tax_amount)item_tax_amount,sum(di.amount_with_tax)item_without_amount,sum(di.tax_amount)+sum(di.amount_with_tax)item_with_amount\n"+
            "from t_xf_bill_deduct d\n" +
            "left outer join t_xf_bill_deduct_item_ref di on di.deduct_id  = d.id\n"+
            "left outer join t_xf_settlement s on d.ref_settlement_no = s.settlement_no\n" +
            "where 1=1\n" +
            "<if test='businessNo!=null'>"+
            "and d.business_no = #{businessNo}\n"+
             "</if>"+
            "<if test='businessType!=null'>"+
            "and d.business_type = #{businessType}\n"+
            "</if>"+
            "<if test='sellerNo!=null'>"+
            "and d.seller_no = #{sellerNo}\n"+
            "</if>"+
            "<if test='sellerName!=null'>"+
            "and d.seller_name = #{sellerName}\n"+
            "</if>"+
            "<if test='businessNo!=null'>"+
            "and d.business_no = #{businessNo}\n"+
            "</if>"+
            "<if test='deductStartDate!=null'>"+
            "and d.deduct_date &gt;=  #{deductStartDate}\n"+
            "</if>"+
            "<if test='deductEndDate!=null'>"+
            "and d.deduct_date &lt;=  #{deductEndDate}\n"+
            "</if>"+
            "<if test='purchaserNo!=null'>"+
            "and d.purchaser_no= #{purchaserNo}\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==1'>"+
            "and d.status = 104\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==2'>"+
            "and d.status = 205\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==3'>"+
            "and d.status = 303\n"+
            "</if>"+
            "<if test='key == 1'>"+
            "and s.settlement_status = 2\n"+
            "and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 3) &lt; (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no)\n"+
            "</if>"+
            "<if test='key == 2'>"+
            "and s.settlement_status = 2\n"+
            "and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 3) = (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no)\n"+
            "</if>"+
            "<if test='key == 3'>"+
            "and s.settlement_status = 4\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==1'>"+
            "and d.status = 108\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==2'>"+
            "and d.status = 206\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==3'>"+
            "and d.status = 304\n"+
            "</if>"+
            "group by d.id, d.business_no, d.business_type, d.ref_settlement_no, d.verdict_date, d.deduct_date, d.deduct_invoice, d.tax_rate, d.agreement_reason_code, d.agreement_reference, d.agreement_tax_code, d.agreement_memo, d.agreement_document_number, d.agreement_document_type, d.tax_amount, d.remark, d.status, d.create_time, d.update_time, d.purchaser_no, d.seller_no, d.seller_name, d.amount_without_tax, d.amount_with_tax, d.lock_flag, d.batch_no, d.source_id, d.purchaser_name,s.invoice_type\n"+
            "<if test='offset != null and next !=null'>"+
            "order by d.id desc offset #{offset} rows fetch next #{next} rows only\n"+
            "</if>"+
            "</script>")
    List<TXfBillDeductExtEntity> queryBillPage(@Param("offset")Integer offset, @Param("next")Integer next, @Param("businessNo")String businessNo, @Param("businessType")Integer businessType, @Param("sellerNo")String sellerNo, @Param("sellerName")String sellerName, @Param("deductStartDate") String deductStartDate, @Param("deductEndDate") String deductEndDate, @Param("purchaserNo")String purchaserNo, @Param("key")String key);

    @Select("<script>"+
            "select count(d.id) from t_xf_bill_deduct d\n" +
            "left outer join t_xf_settlement s on d.ref_settlement_no = s.settlement_no\n" +
            "where 1=1\n" +
            "<if test='businessNo!=null'>"+
            "and d.business_no = #{businessNo}\n"+
            "</if>"+
            "<if test='businessType!=null'>"+
            "and d.business_type = #{businessType}\n"+
            "</if>"+
            "<if test='sellerNo!=null'>"+
            "and d.seller_no = #{sellerNo}\n"+
            "</if>"+
            "<if test='sellerName!=null'>"+
            "and d.seller_name = #{sellerName}\n"+
            "</if>"+
            "<if test='businessNo!=null'>"+
            "and d.business_no = #{businessNo}\n"+
            "</if>"+
            "<if test='deductStartDate!=null'>"+
            "and d.deduct_date &gt;=  #{deductStartDate}\n"+
            "</if>"+
            "<if test='deductEndDate!=null'>"+
            "and d.deduct_date &lt;=  #{deductEndDate}\n"+
            "</if>"+
            "<if test='purchaserNo!=null'>"+
            "and d.purchaser_no= #{purchaserNo}\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==1'>"+
            "and d.status = 104\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==2'>"+
            "and d.status = 205\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==3'>"+
            "and d.status = 303\n"+
            "</if>"+
            "<if test='key == 1'>"+
            "and s.settlement_status = 2\n"+
            "and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 3) &lt; (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no)\n"+
            "</if>"+
            "<if test='key == 2'>"+
            "and s.settlement_status = 2\n"+
            "and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 3) = (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no)\n"+
            "</if>"+
            "<if test='key == 3'>"+
            "and s.settlement_status = 4\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==1'>"+
            "and d.status = 108\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==2'>"+
            "and d.status = 206\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==3'>"+
            "and d.status = 304\n"+
            "</if>"+
            "</script>")
    int countBillPage(@Param("businessNo")String businessNo,@Param("businessType")Integer businessType,@Param("sellerNo")String sellerNo,@Param("sellerName")String sellerName,@Param("deductStartDate") String deductStartDate,@Param("deductEndDate") String deductEndDate,@Param("purchaserNo")String purchaserNo,@Param("key")String key);
}
