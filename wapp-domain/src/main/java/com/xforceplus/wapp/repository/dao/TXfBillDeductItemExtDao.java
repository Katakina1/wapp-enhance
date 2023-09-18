package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.dto.TXfBillDeductItemExtDto;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* <p>
* 业务单据明细信息 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-12
*/
@Mapper
public interface TXfBillDeductItemExtDao extends BaseMapper<TXfBillDeductItemEntity> {

	/**
	 * 查询索赔明细（未匹配到税编明细的不能进行匹配）
	 * 
	 * @param sellerNo
	 * @param taxRate
	 * @param id
	 * @param limit
	 * @return
	 */
    @Select("select top ${limit} * from t_xf_bill_deduct_item  where id > #{id} and  claim_no = #{claimNo}  and remaining_amount > 0 and seller_no = #{sellerNo} and tax_rate = #{taxRate} order by id ")
    public List<TXfBillDeductItemEntity > queryMatchBillItem(@Param("sellerNo")String sellerNo,
                                                             @Param("taxRate") BigDecimal taxRate,
                                                             @Param("id") Long id,
                                                             @Param("limit") int limit,
                                                             @Param("claimNo") String claimNo );
    
    /**
	 * 查询根据索赔查询索赔明细（只查询一条，为了判断索赔号是否存在）
	 * 
	 * @param claimNo
	 * @return
	 */
    @Select("select top 1 * from t_xf_bill_deduct_item where claim_no = #{claimNo} and seller_no != #{sellerNo} ")
	public List<TXfBillDeductItemEntity> queryBillItemByClaimNo(@Param("claimNo") String claimNo, @Param("sellerNo") String sellerNo);

	/**
	 * 更新折扣明细金额
	 * 
	 * @param id
	 * @param amount
	 * @return
	 */
    @Update("update t_xf_bill_deduct_item set remaining_amount = remaining_amount - ${amount}  where id = #{id} and remaining_amount >= ${amount}  ")
    public int updateBillItem(@Param("id") Long id, @Param("amount") BigDecimal amount);

	/**
	 * 更新明细剩余可用金额及税编信息
	 * @param item 明细
	 * @param amount 使用金额
	 * @return 影响行数
	 */
	int updateBillDeductItem(@Param("item") TXfBillDeductItemEntity item, @Param("amount") BigDecimal amount);

	/**
	 * 查询索赔单 下关联的 索赔明细信息
	 * 
	 * @param purchaserNo
	 * @param sellerNo
	 * @param type
	 * @param status
	 * @return
	 */
    @Select("select ref.id item_ref_id,item.id,item.zero_tax,item.tax_pre_con,item.tax_pre,item.goods_no_ver,item.goods_tax_no,item.cn_desc,item.unit,item.item_spec,item.item_no,item.tax_rate,item.item_short_name,ref.price,ref.quantity,ref.use_amount amount_without_tax,item.remaining_amount remaining_amount,ref.amount_with_tax ,ref.tax_amount "
    		+ "from t_xf_bill_deduct_item_ref ref, t_xf_bill_deduct_item item,t_xf_bill_deduct deduct where ref.status = 0 and deduct.purchaser_no =#{purchaserNo} and deduct.seller_no = #{sellerNo} and  deduct.business_type = #{type} and deduct.status = #{status} and ref.deduct_id = deduct.id and ref.deduct_item_id = item.id")
    public List<TXfBillDeductItemExtDto> queryItemsByBill(@Param("purchaserNo") String purchaserNo,
														  @Param("sellerNo") String sellerNo,
														  @Param("type")Integer type,
														  @Param("status")Integer status);


	/**
	 * 查询索赔单 下关联的 索赔明细信息
	 * 
	 * @param deductId
	 * @param type
	 * @param status
	 * @return
	 */
    @Select("select item.id id, item.zero_tax,item.tax_pre_con,item.tax_pre,item.goods_no_ver,item.goods_tax_no,item.cn_desc,item.unit,item.item_no,item.tax_rate,item.item_short_name,item.quantity,ref.price,ref.quantity,ref.use_amount amount_without_tax,item.remaining_amount remaining_amount from t_xf_bill_deduct_item_ref ref, t_xf_bill_deduct_item item,t_xf_bill_deduct deduct where deduct.id = #{deductId} and   ref.deduct_id = deduct.id and ref.deduct_item_id = item.id")
    public List<TXfBillDeductItemEntity> queryItemsByBillId(@Param("deductId") Long deductId ,@Param("type")Integer type,@Param("status")Integer status);


}
