package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


/**
* <p>
* 结算单明细 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-21
*/
public interface TXfSettlementItemDao extends BaseMapper<TXfSettlementItemEntity> {

    @Update("UPDATE txsi SET goods_tax_no = #{goodsTaxNo}\n" +
            "FROM t_xf_settlement txs \n" +
            "JOIN t_xf_settlement_item txsi ON txsi.settlement_no = txs.settlement_no\n" +
            "WHERE txs.settlement_status = 1 AND txsi.item_code = #{itemNo} AND txs.seller_no = #{sellerNo}")
    void updateItem(@Param("itemNo") String itemNo, @Param("sellerNo") String sellerNo, @Param("goodsTaxNo") String goodsTaxNo);
}
