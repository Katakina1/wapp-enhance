package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-12
 */
@Mapper
public interface TXfPreInvoiceItemDao extends BaseMapper<TXfPreInvoiceItemEntity> {

    @Update("UPDATE txpii SET goods_tax_no = #{goodsTaxNo}\n" +
            "FROM t_xf_pre_invoice txpi \n" +
            "JOIN t_xf_pre_invoice_item txpii ON txpi.id = txpii.pre_invoice_id\n" +
            "WHERE txpi.pre_invoice_status = 1 AND txpii.cargo_code = #{itemNo} \n" +
            "AND txpi.seller_no = #{sellerNo}")
    void updateItem(@Param("itemNo") String itemNo, @Param("sellerNo") String sellerNo, @Param("goodsTaxNo") String goodsTaxNo);
}
