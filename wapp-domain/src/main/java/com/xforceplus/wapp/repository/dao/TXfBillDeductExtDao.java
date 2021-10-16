package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
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
       @Select("select top ${limit} * from t_xf_bill_deduct " +
            "where id> #{id} and create_date => #{startDate} and create_date <= #{endDate} and business_type = #{billType} and status = #{status}  " +
            "order by id  ")
    List<TXfBillDeductEntity> queryUnMatchBill(@Param("id") Long id,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate,
                                               @Param("limit") Integer limit,
                                               @Param("billType") Integer billType,
                                               @Param("status") Integer status);
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
     * select sum(amount_without_tax),seller_no,purchaser_no, tax_rate from t_xf_bill_deduct where create_date >= ? and amount_without_tax >0 group by seller_no,purchaser_no, tax_rate
     * @param referenceDate
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no, tax_rate,status  from t_xf_bill_deduct where deduct_date >= #{referenceDate}  and business_type = #{type} and status = #{status} and amount_without_tax > 0 group by seller_no,purchaser_no, tax_rate")
    public List<TXfBillDeductEntity> querySuitablePositiveBill(@Param("referenceDate") Date referenceDate,Integer type,Integer status);



    /**select sum(amount_without_tax) as amount_without_tax ,seller_no,purchaser_no, tax_rate from t_xf_bill_deduct where purchaser_no  = #{purchaserNo} and seller_no  = #{sellerNo} and tax_rate = #{taxRate} and amount_without_tax < 0
     *  查询同维度下 负数金额 的总和
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @return
     */
    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no, tax_rate,status from t_xf_bill_deduct where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax < 0")
    public TXfBillDeductEntity querySpecialNegativeBill(@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("type") Integer type,@Param("status") Integer status);

    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no, tax_rate from t_xf_bill_deduct where ref_settlement_no  = #{settlementNo}")
    public TXfBillDeductEntity queryBillBySettlementNo(@Param("settlementNo") String settlementNo );


    @Update(" update t_xf_bill_deduct set status =#{targetStatus} ,ref_settlement_no=#{settlementNo}  where purchaser_no  = #{purchaserNo} and seller_no = #{sellerNo} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax < 0")
    public int updateMergeNegativeBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus);

    @Update(" update t_xf_bill_deduct set status =#{targetStatus},ref_settlement_no=#{settlementNo}  where purchaser_no = #{purchaserNo} and seller_no = #{sellerNo} and create_date >= #{referenceDate} and business_type = #{type} and status = #{status} and tax_rate = #{taxRate} and amount_without_tax > 0")
    public int updateMergePositiveBill(@Param("settlementNo") String settlementNo,@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo, @Param("taxRate") BigDecimal taxRate,@Param("referenceDate") Date referenceDate, @Param("type") Integer type, @Param("status") Integer status, @Param("targetStatus") Integer targetStatus);

    @Select("select sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,seller_no,purchaser_no, tax_rate from t_xf_bill_deduct where   business_type = #{type} and status = #{status}   group by purchaser_no,seller_no ")
    public List<TXfBillDeductEntity> querySuitableClaimBill( @Param("type")Integer type,@Param("status")Integer status);
}
