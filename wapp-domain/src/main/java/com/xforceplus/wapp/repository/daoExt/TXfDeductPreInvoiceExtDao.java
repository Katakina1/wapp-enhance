package com.xforceplus.wapp.repository.daoExt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfDeductPreInvoiceEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
* <p>
* 业务单信息关联表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2022-09-14
*/
public interface TXfDeductPreInvoiceExtDao extends BaseMapper<TXfDeductPreInvoiceEntity> {

    /**
     * 根据业务单和结算单查询申请红字的预制发票数量
     * @param deductId 业务单id
     * @param settlementNo 结算单号
     */
    @Select("select count(*) from t_xf_deduct_pre_invoice t left join t_xf_pre_invoice p on p.id = t.pre_invoice_id " +
            "where t.deduct_id = #{deductId} and p.settlement_no = #{settlementNo} and t.apply_status <> 0")
    Integer selectCount(@Param("deductId") Long deductId, @Param("settlementNo") String settlementNo);

    /**
     * 根据业务单和结算单查询申请红字的预制发票数量
     * @param deductId 业务单id
     * @param settlementNo 结算单号
     */
    @Select("select t.* from t_xf_deduct_pre_invoice t left join t_xf_pre_invoice p on p.id = t.pre_invoice_id " +
            "where t.deduct_id = #{deductId} and p.settlement_no = #{settlementNo} and t.apply_status <> 0")
    List<TXfDeductPreInvoiceEntity> selectList(@Param("deductId") Long deductId, @Param("settlementNo") String settlementNo);
}
