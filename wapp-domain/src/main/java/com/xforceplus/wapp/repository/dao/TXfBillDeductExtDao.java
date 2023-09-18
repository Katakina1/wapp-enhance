package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import com.xforceplus.wapp.repository.vo.BillDeductLeftSettlementVo;
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
     * 1、查询折扣单列表
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

    @Select("<script>" +
            "SELECT t.settlement_status, txbd.* " +
            "from t_xf_bill_deduct txbd left join " +
            "( SELECT txs.settlement_status, txs.settlement_no FROM t_xf_settlement txs) t " +
            "on t.settlement_no = txbd.ref_settlement_no " +
            "${ew.getCustomSqlSegment()} " +
            "</script>")
    <T, W> Page<BillDeductLeftSettlementVo> selectJoinSettlement(Page<T> page, @Param("ew") QueryWrapper<W> ew);
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
            "from t_xf_bill_deduct deduct left join t_xf_overdue overdue on overdue.seller_no = deduct.seller_no AND overdue.type = #{type} AND overdue.delete_flag  is null \n" +
            "where  deduct.create_time <=  IIF(  overdue.overdue_day is null, convert(varchar(10),DATEADD(d, 0 - #{referenceDate}, GETDATE()),120), convert(  varchar(10),DATEADD(d, 0 - (overdue.overdue_day), GETDATE()),120)) \n" +
            "  and business_type = #{type} and status = #{status} and amount_without_tax > 0 and  lock_flag = #{flag}\n" +
            "group by deduct.purchaser_no, deduct.seller_no,deduct.tax_rate")
    public List<TXfBillDeductEntity> querySuitablePositiveBill(@Param("referenceDate") Integer referenceDate,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag);

    /** 查询超期正数的业务单列表
     * @param referenceDate
     * @return
     */
    @Select("select " +
            "deduct.id,"+ "deduct.agreement_document_number,"+ "deduct.agreement_document_type,"+ "deduct.agreement_memo,"+
            "deduct.agreement_reason_code,"+ "deduct.agreement_reference,"+ "deduct.agreement_tax_code,"+ "deduct.amount_with_tax,"+
            "deduct.amount_without_tax,"+ "deduct.batch_no,"+ "deduct.business_no,"+ "deduct.business_type,"+ "deduct.create_time,"+
            "deduct.deduct_date,"+ "deduct.deduct_invoice,"+ "deduct.lock_flag,"+ "deduct.purchaser_name,"+ "deduct.purchaser_no,"+
            "deduct.ref_settlement_no,"+ "deduct.remark,"+ "deduct.seller_name,"+ "deduct.seller_no,"+ "deduct.source_id,"+
            "deduct.status,"+ "deduct.tax_amount,"+ "deduct.tax_rate,"+ "deduct.update_time,"+ "deduct.verdict_date "+
            "from t_xf_bill_deduct deduct left join t_xf_overdue overdue on overdue.seller_no = deduct.seller_no AND overdue.type = #{type} AND overdue.delete_flag  is null \n" +
            "where  deduct.create_time <=  IIF(  overdue.overdue_day is null, convert(varchar(10),DATEADD(d, 0 - #{referenceDate}, GETDATE()),120), convert(  varchar(10),DATEADD(d, 0 - (overdue.overdue_day), GETDATE()),120)) \n" +
            "  and business_type = #{type} and status = #{status} and amount_without_tax > 0 and  lock_flag = #{flag}\n")
    public List<TXfBillDeductEntity> querySuitablePositiveBillList(@Param("referenceDate") Integer referenceDate,
                                                                   @Param("type") Integer type,
                                                                   @Param("status") Integer status,
                                                                   @Param("flag") Integer flag);

    /**
     *  查询同维度下 负数业务单列表
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @return
     */
    @Select("<script>"+
            "select " +
            "deduct.id,"+ "deduct.agreement_document_number,"+ "deduct.agreement_document_type,"+ "deduct.agreement_memo,"+
            "deduct.agreement_reason_code,"+ "deduct.agreement_reference,"+ "deduct.agreement_tax_code,"+ "deduct.amount_with_tax,"+
            "deduct.amount_without_tax,"+ "deduct.batch_no,"+ "deduct.business_no,"+ "deduct.business_type,"+ "deduct.create_time,"+
            "deduct.deduct_date,"+ "deduct.deduct_invoice,"+ "deduct.lock_flag,"+ "deduct.purchaser_name,"+ "deduct.purchaser_no,"+
            "deduct.ref_settlement_no,"+ "deduct.remark,"+ "deduct.seller_name,"+ "deduct.seller_no,"+ "deduct.source_id,"+
            "deduct.status,"+ "deduct.tax_amount,"+ "deduct.tax_rate,"+ "deduct.update_time,"+ "deduct.verdict_date "+
            "from t_xf_bill_deduct deduct where deduct.purchaser_no  = #{purchaserNo} and deduct.seller_no = #{sellerNo} "+
            "and deduct.business_type = #{type} and deduct.status = #{status} and  deduct.lock_flag = #{flag} "+
            "and deduct.ref_settlement_no = '' and deduct.tax_rate = #{taxRate} and deduct.amount_without_tax &lt; 0 "+
            "<if test='agreementTaxCode!=null'> "+
            " and deduct.agreement_tax_code = #{agreementTaxCode} "+
            "</if>"+
            "</script>")
    List<TXfBillDeductEntity> querySpecialNegativeBillList(@Param("purchaserNo") String purchaserNo,
                                                                  @Param("sellerNo") String sellerNo,
                                                                  @Param("taxRate") BigDecimal taxRate,
                                                                  @Param("agreementTaxCode") String agreementTaxCode,
                                                                  @Param("type") Integer type,
                                                                  @Param("status") Integer status,
                                                                  @Param("flag") Integer flag);


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
    @Update("update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo},update_time=GETDATE()  where id  in  ${ids} and status = #{status} and  lock_flag = #{flag} and ref_settlement_no = ''")
    int updateBillById(@Param("ids") String ids,@Param("settlementNo") String settlementNo,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag,@Param("targetStatus") Integer targetStatus);



    /**
     *  查询同维度下 负数金额 的总和
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount, sum(amount_with_tax) as amount_with_tax   from t_xf_bill_deduct where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and  lock_flag = #{flag} and ref_settlement_no = '' and tax_rate = #{taxRate} and amount_without_tax < 0")
    public TXfBillDeductEntity querySpecialNegativeBill(@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("type") Integer type,@Param("status") Integer status,  @Param("flag") Integer flag);


    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount  from t_xf_bill_deduct   where ref_settlement_no  = #{settlementNo} and status = #{status} and  lock_flag = #{flag}")
    public TXfBillDeductEntity queryBillBySettlementNo(@Param("settlementNo") String settlementNo, @Param("status") Integer status, @Param("flag") Integer flag );


    @Select("select top 1 *  from t_xf_bill_deduct  where ref_settlement_no = #{settlementNo}  ")
    public TXfBillDeductEntity queryOneBillBySettlementNo(@Param("settlementNo") String settlementNo);
    
    /**
     * 1、根据结算单查询下面所有的业务单
     * @param settlementNo
     * @return
     */
    @Select("select * from t_xf_bill_deduct where ref_settlement_no = #{settlementNo}  and  business_type = #{businessType}")
    public List<TXfBillDeductEntity> queryBillBySettlementNoAndBusinessType(@Param("settlementNo") String settlementNo, @Param("businessType") Integer businessType);

    @Update("update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo},update_time=GETDATE()  where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax < 0 and ref_settlement_no = '' and  lock_flag = #{flag} ")
    public int updateMergeNegativeBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    @Update(" update t_xf_bill_deduct set status =#{targetStatus},ref_settlement_no=#{settlementNo},update_time=GETDATE()  where purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and create_time <= #{referenceDate} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax > 0 and ref_settlement_no = '' and  lock_flag = #{flag}")
    public int updateMergePositiveBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("referenceDate") Date referenceDate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    /**
     * 1、索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no   from t_xf_bill_deduct where    business_type = #{type} and status = #{status}  group by purchaser_no,seller_no ")
    public List<TXfBillDeductEntity> querySuitableClaimBill( @Param("type")Integer type,@Param("status")Integer status);

    /**
     * 1、索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Update(" update t_xf_bill_deduct set status = #{targetStatus},ref_settlement_no=#{settlementNo},update_time=GETDATE()    where  purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and  business_type = #{type} and status = #{status} ")
    public Integer updateSuitableClaimBill(@Param("type")Integer type, @Param("status")Integer status, @Param("targetStatus") Integer targetStatus, @Param("settlementNo")String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo);

    /**
     * 1、根据购方+ 销方+ 状态查询业务单的信息
     * @param type
     * @param status
     * @param purchaserNo
     * @param sellerNo
     * @return
     */
    @Select("select * from t_xf_bill_deduct where purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{businessType} and status = #{status}")
    public List<TXfBillDeductEntity> queryClaimBillBypurchaserNoAndSellerNo(@Param("businessType")Integer type, @Param("status")Integer status, @Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo);
    
    @Select("<script>"+
            "select " +
            "<if test='key == 0 or key ==4'>d.update_time,</if>" +
            "<if test='key == 1 or key ==2 or key ==3'>max(s.update_time),</if>" +
            "d.id, d.business_no, d.business_type, d.ref_settlement_no, d.verdict_date, d.deduct_date, d.deduct_invoice, d.tax_rate, d.agreement_reason_code, d.agreement_reference, d.agreement_tax_code, d.agreement_memo, d.agreement_document_number, d.agreement_document_type, d.tax_amount, d.remark, d.status, d.create_time, d.update_time, d.purchaser_no, d.seller_no, d.seller_name, d.amount_without_tax, d.amount_with_tax, d.lock_flag, d.batch_no, d.source_id, d.purchaser_name,s.invoice_type," +
            "sum(bdi.tax_amount)item_tax_amount,sum(bdi.amount_with_tax)item_with_amount,sum(bdi.amount_without_tax)item_without_amount\n"+
            "from t_xf_bill_deduct d\n" +
            "left outer join t_xf_bill_deduct_item_ref di on di.deduct_id  = d.id\n"+
            "left outer join t_xf_bill_deduct_item bdi on di.deduct_item_id  = bdi.id\n"+
            "left outer join t_xf_settlement s on d.ref_settlement_no = s.settlement_no\n" +
            "where 1=1\n" +
            "<if test='ids!=null'>"+
            "and d.id in ${ids}\n"+
            "</if>"+
            "<if test='businessNo!=null'>"+
            "and d.business_no like concat(#{businessNo},'%')\n"+
            "</if>"+
            "<if test='taxRate != null'>"+
            "and d.tax_rate = #{taxRate}\n"+
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
            "and (d.status = 101 or d.status = 102 or d.status = 103 or d.status = 104 or d.status = 105)\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==2'>"+
            "and d.status = 201\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==3'>"+
            "and d.status = 301\n"+
            "</if>"+
            "<if test='settlementNo != null'>"+
            "and d.ref_settlement_no like concat(#{settlementNo},'%')\n"+
            "</if>"+
            "<if test='redNotificationNo != null'>"+
            "and d.ref_settlement_no in(select settlement_no from t_xf_pre_invoice p where p.red_notification_no = #{redNotificationNo})\n"+
            "</if>"+
            "<if test='key == 9'>"+
            "and s.settlement_status = 9 \n"+
            "</if>"+
            "<if test='key == 1'>"+
            "and s.settlement_status != 9 and s.settlement_status != 7 and d.ref_settlement_no != ''\n"+
            "and ((select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') = 0 or  (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') &lt; (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status != 7 and p.pre_invoice_status != 5) or (s.settlement_status = 2 and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 5) >0))\n"+
            "</if>"+
            "<if test='key == 2'>"+
            "and (s.settlement_status = 2 or s.settlement_status = 3)\n"+
            "and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') = (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status != 7 and p.pre_invoice_status != 5)\n"+
            "AND (SELECT count(1) FROM t_xf_pre_invoice p WHERE p.settlement_no = s.settlement_no AND p.pre_invoice_status != 7 AND p.pre_invoice_status != 5) > 0 " +
            "</if>"+
            "<if test='key == 3'>"+
            "and (s.settlement_status = 4 or (s.settlement_status = 3 and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 5) >0))\n"+
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
            "<if test='createTimeBegin!=null'>"+
            "<![CDATA[  and d.create_time > #{createTimeBegin} ]]> \n"+
            "</if>"+
            "<if test='createTimeEnd!=null'>"+
            "<![CDATA[  and d.create_time <= #{createTimeEnd} ]]>\n"+
            "</if>"+
            "group by d.id, d.business_no, d.business_type, d.ref_settlement_no, d.verdict_date, d.deduct_date, d.deduct_invoice, d.tax_rate, d.agreement_reason_code, d.agreement_reference, d.agreement_tax_code, d.agreement_memo, d.agreement_document_number, d.agreement_document_type, d.tax_amount, d.remark, d.status, d.create_time, d.update_time, d.purchaser_no, d.seller_no, d.seller_name, d.amount_without_tax, d.amount_with_tax, d.lock_flag, d.batch_no, d.source_id, d.purchaser_name,s.invoice_type\n"+
            "<if test='offset != null and next !=null'>"+
            "order by d.id desc offset #{offset} rows fetch next #{next} rows only\n"+
            "</if>"+
            "</script>")
    List<TXfBillDeductExtEntity> queryBillPage(@Param("offset")Integer offset, @Param("next")Integer next, @Param("ids")String ids,@Param("businessNo")String businessNo, @Param("businessType")Integer businessType, @Param("sellerNo")String sellerNo, @Param("sellerName")String sellerName, @Param("deductStartDate") String deductStartDate, @Param("deductEndDate") String deductEndDate, @Param("purchaserNo")String purchaserNo, @Param("key")String key
            ,@Param("createTimeEnd") String createTimeEnd
            ,@Param("createTimeBegin") String createTimeBegin
            ,@Param("settlementNo") String settlementNo
            ,@Param("redNotificationNo") String redNotificationNo
            ,@Param("taxRate") BigDecimal taxRate
    );

    @Select("<script>"+
            "select count(d.id) from t_xf_bill_deduct d\n" +
            "left outer join t_xf_settlement s on d.ref_settlement_no = s.settlement_no\n" +
            "where 1=1\n" +
            "<if test='ids!=null'>"+
            "and d.id in ${ids}\n"+
            "</if>"+
            "<if test='businessNo!=null'>"+
            "and d.business_no like concat(#{businessNo},'%')\n"+
            "</if>"+
            "<if test='businessType!=null'>"+
            "and d.business_type = #{businessType}\n"+
            "</if>"+
            "<if test='taxRate != null'>"+
            "and d.tax_rate = #{taxRate}\n"+
            "</if>"+
            "<if test='sellerNo!=null'>"+
            "and d.seller_no = #{sellerNo}\n"+
            "</if>"+
            "<if test='sellerName!=null'>"+
            "and d.seller_name = #{sellerName}\n"+
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
            "and (d.status = 101 or d.status = 102 or d.status = 103 or d.status = 104 or d.status = 105)\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==2'>"+
            "and d.status = 201\n"+
            "</if>"+
            "<if test='key == 0 and businessType ==3'>"+
            "and d.status = 301\n"+
            "</if>"+
            "<if test='settlementNo != null'>"+
            "and d.ref_settlement_no like concat(#{settlementNo},'%')\n"+
            "</if>"+
            "<if test='redNotificationNo != null'>"+
            "and d.ref_settlement_no in(select settlement_no from t_xf_pre_invoice p where p.red_notification_no = #{redNotificationNo})\n"+
            "</if>"+
            "<if test='key == 9'>"+
            "and s.settlement_status = 9 \n"+
            "</if>"+
            "<if test='key == 1'>"+
            "and s.settlement_status != 9 and s.settlement_status != 7 and d.ref_settlement_no != ''\n"+
            "and ((select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') = 0 or  (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') &lt; (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status != 7 and p.pre_invoice_status != 5) or (s.settlement_status = 2 and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 5) >0))\n"+
            "</if>"+
            "<if test='key == 2'>"+
            "AND (s.settlement_status = 2 or s.settlement_status = 3)\n"+
            "AND (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.red_notification_no != '') = (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status != 7 and p.pre_invoice_status != 5) \n"+
            "AND (SELECT count(1) FROM t_xf_pre_invoice p WHERE p.settlement_no = s.settlement_no AND p.pre_invoice_status != 7 AND p.pre_invoice_status != 5) > 0 " +
            "</if>"+
            "<if test='key == 3'>"+
            "and (s.settlement_status = 4 or (s.settlement_status = 3 and (select count(1) from t_xf_pre_invoice p where p.settlement_no = s.settlement_no and p.pre_invoice_status = 5) >0))\n"+
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
            "<if test='createTimeBegin!=null'>"+
            "<![CDATA[  and d.create_time > #{createTimeBegin} ]]> \n"+
            "</if>"+
            "<if test='createTimeEnd!=null'>"+
            "<![CDATA[  and d.create_time <= #{createTimeEnd} ]]> \n"+
            "</if>"+
            "</script>")
    int countBillPage(@Param("ids")String ids,@Param("businessNo")String businessNo,@Param("businessType")Integer businessType,@Param("sellerNo")String sellerNo,@Param("sellerName")String sellerName,@Param("deductStartDate") String deductStartDate,@Param("deductEndDate") String deductEndDate,@Param("purchaserNo")String purchaserNo,@Param("key")String key
    ,@Param("createTimeEnd") String createTimeEnd
    ,@Param("createTimeBegin") String createTimeBegin
    ,@Param("settlementNo") String settlementNo
    ,@Param("redNotificationNo") String redNotificationNo
    ,@Param("taxRate") BigDecimal taxRate
    );
    @Select("select * from t_xf_bill_deduct   where business_no in  (${businessNos}) and business_type =  #{businessType}   and  ref_settlement_no = '' and amount_with_tax >0 ")
    List<TXfBillDeductEntity> selectByBusinessNos(@Param("businessNos") String  businessNos,@Param("businessType")Integer businessType );
}

