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
     * 查询折扣明细（）
     * @param date
     * @param purchaserNo
     * @param sellerNo
     * @param taxRate
     * @param start
     * @param limit
     * @return
     */
    @Select("select * from t_xf_bill_deduct_item  where create_date=>#{date} and remaining_amount > 0  and purchaser_no  = #{purchaserNo} and seller_no  = #{sellerNo} and tax_rate = #{taxRate}  order by id limit ${start} ,${limit} ")
    public List<TXfBillDeductItemEntity > queryMatchBillItem(@Param("date")Date date, @Param("purchaserNo")String purchaserNo,@Param("sellerNo")String sellerNo,@Param("taxRate") BigDecimal taxRate, @Param("start") int start, @Param("limit") int limit );

    /**
     * 更新折扣明细金额
     * @param id
     * @param amount
     * @return
     */
   @Update("update t_xf_bill_deduct_item  where id = #{id} and remaining_amount = remaining_amount - ${amount}  where remaining_amount >= ${amount}  ")
    public int updateBillItem(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
