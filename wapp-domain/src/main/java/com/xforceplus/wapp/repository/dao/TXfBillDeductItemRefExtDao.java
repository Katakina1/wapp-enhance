package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.dto.DeductSettlementItemRefDto;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


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
    @Select("  select  sum(use_amount) from t_xf_bill_deduct_item_ref where deduct_id =  #{id} and status = 0")
    BigDecimal queryRefMatchAmountByBillId(@Param("id") Long id);


    List<DeductSettlementItemRefDto> queryDeductIdBySettlementItemId(@Param("items") Collection<Long> settlementItemIds);
}
