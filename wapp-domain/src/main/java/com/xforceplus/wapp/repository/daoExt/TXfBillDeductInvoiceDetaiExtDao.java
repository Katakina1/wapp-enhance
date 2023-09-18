package com.xforceplus.wapp.repository.daoExt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;


/**
* <p>
* 业务单发票明细关系表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2022-09-16
*/
@Mapper
public interface TXfBillDeductInvoiceDetaiExtDao extends BaseMapper<TXfBillDeductInvoiceDetailEntity> {

  /**
   * 待修复结算单明细ID列表
   * @return
   */
  @Select("<script> "
          +"select txbdid.id, "
          +"txsiid.settlement_item_id "
          +"from t_xf_bill_deduct_invoice_detail txbdid "
          +"left join t_xf_bill_deduct txbd on txbdid.deduct_id = txbd.id "
          +"left join t_xf_settlement_item_invoice_detail txsiid on txbd.ref_settlement_no = txsiid.settlement_no "
          +"where txbdid.settlement_item_id = 0 and txbdid.business_type = 2 and txbdid.status = 0 "
          +"and txbdid.invoice_detail_id = txsiid.invoice_detail_id "
          +"and txbdid.use_amount_without_tax = txsiid.use_amount_without_tax "
          +"and txbdid.use_quantity = txsiid.use_quantity "
          +"and txbdid.use_tax_amount = txsiid.use_tax_amount "
          +"</script>")
  List<TXfBillDeductInvoiceDetailEntity> queryRepairSettlementItemIdList();
}
