package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;


/**
* <p>
* 业务单据明细匹配关系表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-14
 */
@Mapper
public interface TXfBillDeductItemRefExtDao extends BaseMapper<TXfBillDeductItemRefEntity> {
    @Select("  select  sum(amount_with_tax) from t_xf_bill_deduct_item_ref where deduct_id =  #{id}")
    BigDecimal queryRefMatchAmountByBillId(@Param("id") Long id);

}
