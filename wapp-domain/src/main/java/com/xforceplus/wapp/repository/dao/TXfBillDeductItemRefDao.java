package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
* <p>
* 业务单据明细匹配关系表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-12-21
*/
public interface TXfBillDeductItemRefDao extends BaseMapper<TXfBillDeductItemRefEntity> {

    @Select("<script> SELECT bdir.* from t_xf_bill_deduct bd inner join t_xf_bill_deduct_item_ref bdir " +
            "on bd.id = bdir.deduct_id " +
            "where bd.status = #{settlementStatus} and bd.ref_settlement_no = #{settlementNo} " +
            "<if test='status!=null'>"+
            "and bdir.status = #{status}  "+
            "</if>"+
            " </script> ")
    List<TXfBillDeductItemRefEntity> selectListJoin(@Param("settlementNo") String settlementNo, @Param("settlementStatus") Integer settlementStatus,
                                                    @Param("status") Integer status);

}
