package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
* 业务单据明细信息 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-12
*/
public interface TXfBillDeductItemExtDao extends BaseMapper<TXfBillDeductItemEntity> {

    /**
     * 查询索赔明细（未匹配到税编明细的不能进行匹配）
     * @param startDate
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @param id
     * @param limit
     * @return
     */
    @Select("select top ${limit} * from t_xf_bill_deduct_item  where id > #{id}  and  create_date => #{startDate} and create_date <= #{endDate}  and remaining_amount > 0  and purchaser_no  = #{purchaserNo} and seller_no  = #{sellerNo} and tax_rate = #{taxRate} and goods_tax_no <>'' order by id   ")
    public List<TXfBillDeductItemEntity > queryMatchBillItem(@Param("startDate") Date startDate,
                                                             @Param("endDate") Date endDate,
                                                             @Param("purchaserNo")String purchaserNo,
                                                             @Param("sellerNo")String sellerNo,
                                                             @Param("taxRate") BigDecimal taxRate,
                                                             @Param("id") Long id,
                                                             @Param("limit") int limit );

    /**
     * 更新折扣明细金额
     * @param id
     * @param amount
     * @return
     */
   @Update("update t_xf_bill_deduct_item  where id = #{id} and remaining_amount = remaining_amount - ${amount}  where remaining_amount >= ${amount}  ")
    public int updateBillItem(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 查询索赔单 下关联的 索赔明细信息
     * @param purchaserNo
     * @param sellerNo
     * @param type
     * @param status
     * @return
     */
    @Select("select item.zero_tax,item.tax_pre_con,item.tax_pre,item.goods_no_ver,item.goods_tax_no,item.cn_desc,item.unit,item.item_no,item.tax_rate,item.item_short_name,quantity,ref.price,ref.quantity,ref.use_amount amount_without_tax from t_xf_bill_deduct_item_ref ref, t_xf_bill_deduct_item item,t_xf_bill_deduct deduct where deduct.purchaser_no =#{purchaserNo} and deduct.seller_no = #{sellerNo} and  business_type = #{type} and status = #{status}  and ref.deduct_id = deduct.id and ref.deduct_item_id = item.id")
    public List<TXfBillDeductItemEntity> queryItemsByBill(@Param("purchaserNo") String purchaserNo, @Param("sellerNo") String sellerNo,@Param("type")Integer type,@Param("status")Integer status);


}
