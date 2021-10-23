package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import io.swagger.models.auth.In;
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
               "and  create_date >= #{startDate} "+
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
            "      where create_date => #{startDate} and create_date <= #{endDate} \n"+
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
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,sum(amount_with_tax) as amount_with_tax,seller_no,purchaser_no, tax_rate   from t_xf_bill_deduct where deduct_date >= #{referenceDate}  and business_type = #{type} and status = #{status} and amount_without_tax > 0 and  lock_flag = #{flag} group by purchaser_no, seller_no,tax_rate")
    public List<TXfBillDeductEntity> querySuitablePositiveBill(@Param("referenceDate") Date referenceDate,@Param("type") Integer type,@Param("status") Integer status,@Param("flag") Integer flag);



    /**
     *  查询同维度下 负数金额 的总和
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount, sum(amount_with_tax) as amount_with_tax  from t_xf_bill_deduct where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and  lock_flag = #{flag} and ref_settlement_no = '' and tax_rate = #{taxRate} and amount_without_tax < 0")
    public TXfBillDeductEntity querySpecialNegativeBill(@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("type") Integer type,@Param("status") Integer status,  @Param("flag") Integer flag);

    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount  from t_xf_bill_deduct   where ref_settlement_no  = #{settlementNo} and status = #{status} and  lock_flag = #{flag}")
    public TXfBillDeductEntity queryBillBySettlementNo(@Param("settlementNo") String settlementNo, @Param("status") Integer status, @Param("flag") Integer flag );


    @Update(" update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo}  where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax < 0 and ref_settlement_no = '' and  lock_flag = #{flag} ")
    public int updateMergeNegativeBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    @Update(" update t_xf_bill_deduct set status =#{targetStatus},ref_settlement_no=#{settlementNo}  where purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and create_date >= #{referenceDate} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax > 0 and ref_settlement_no = '' and  lock_flag = #{flag}")
    public int updateMergePositiveBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("referenceDate") Date referenceDate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus,@Param("flag") Integer flag );

    /**
     * 索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no   from t_xf_bill_deduct where  create_date >= dateadd(d,-day(getdate())+1,getdate()) and business_type = #{type} and status = #{status}  group by purchaser_no,seller_no ")
    public List<TXfBillDeductEntity> querySuitableClaimBill( @Param("type")Integer type,@Param("status")Integer status);

    /**
     * 索赔单 当月有效
     * @param type
     * @param status
     * @return
     */
    @Select(" update t_xf_bill_deduct set status = #{targetStatus},ref_settlement_no=#{settlementNo}    where  purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and  business_type = #{type} and status = #{status} and create_date >= dateadd(d,-day(getdate())+1,getdate()) ")
    public Integer updateSuitableClaimBill(@Param("type")Integer type, @Param("status")Integer status, @Param("targetStatus") Integer targetStatus, @Param("settlementNo")String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo);

    @Select("<script>"+
            "select d.*,s.invoice_type from t_xf_bill_deduct d\n" +
            "left outer join t_xf_settlement s on d.ref_settlement_no = s.settlement_no\n" +
            "where 1=1\n" +
            "<if test='businessNo!=null'>"+
            "and business_no = #{businessNo}\n"+
             "</if>"+
            "<if test='businessType!=null'>"+
            "and business_type = #{businessType}\n"+
            "</if>"+
            "<if test='sellerNo!=null'>"+
            "and seller_no = #{sellerNo}\n"+
            "</if>"+
            "<if test='sellerName!=null'>"+
            "and seller_name = #{sellerName}\n"+
            "</if>"+
            "<if test='businessNo!=null'>"+
            "and business_no = #{businessNo}\n"+
            "</if>"+
            "<if test='deductDate!=null'>"+
            "and format(deduct_date,'yyyy-MM-dd')= #{deductDate}\n"+
            "</if>"+
            "<if test='purchaserNo!=null'>"+
            "and purchaser_no= #{deductDate}\n"+
            "</if>"+
            "<if test='key == 0'>"+
            "and s.settlement_status = 8\n"+
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
            "<if test='key == 4 and businessType ==2'>"+
            "and d.status = 206\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==3'>"+
            "and d.status = 304\n"+
            "</if>"+
            "<if test='offset != null and next !=null'>"+
            "order by id offset #{offset} rows fetch next #{next} rows only\n"+
            "</if>"+
            "</script>")
    List<TXfBillDeductEntity> queryBillPage(@Param("offset")int offset,@Param("next")int next,@Param("businessNo")String businessNo,@Param("businessType")Integer businessType,@Param("sellerNo")String sellerNo,@Param("sellerName")String sellerName,@Param("deductDate") String deductDate,@Param("purchaserNo")String purchaserNo,@Param("key")String key);

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
            "<if test='deductDate!=null'>"+
            "and format(d.deduct_date,'yyyy-MM-dd')= #{deductDate}\n"+
            "</if>"+
            "<if test='purchaserNo!=null'>"+
            "and d.purchaser_no= #{deductDate}\n"+
            "</if>"+
            "<if test='key == 0'>"+
            "and s.settlement_status = 8\n"+
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
            "<if test='key == 4 and businessType ==2'>"+
            "and d.status = 206\n"+
            "</if>"+
            "<if test='key == 4 and businessType ==3'>"+
            "and d.status = 304\n"+
            "</if>"+
            "</script>")
    int countBillPage(@Param("businessNo")String businessNo,@Param("businessType")Integer businessType,@Param("sellerNo")String sellerNo,@Param("sellerName")String sellerName,@Param("deductDate") String deductDate,@Param("purchaserNo")String purchaserNo,@Param("key")String key);
}
