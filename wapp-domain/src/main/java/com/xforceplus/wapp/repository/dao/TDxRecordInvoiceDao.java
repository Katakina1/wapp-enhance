package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* <p>
* 底账表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-12-02
*/
public interface TDxRecordInvoiceDao extends BaseMapper<TDxRecordInvoiceEntity> {

	/**
	 * 查询还可以用来匹配蓝票的
	 * @param preIdList 禁止匹配发票ID
	 * @param sellerTaxNo 销方税号
	 * @param purchaserTaxNo 购方税号
	 * @param invoiceDate 开票日期
	 * @param currentTaxPeriod 购方当前所属期
	 * @param taxRate 税率
	 * @param notQueryOil 是否查询成品油
	 * @param invoiceDateOrder 排序规则
	 * @return
	 */
	@Select("<script> select top 10 * from t_dx_record_invoice where 1= 1"
			+ " and gf_tax_no = #{purchaserTaxNo} and xf_tax_no = #{sellerTaxNo} and invoice_status = '0' and flow_type = '1' and invoice_type in('01','08') "
			+ "<![CDATA[   and invoice_date >= #{invoiceDate} ]]> "
			+ "<if test='preIdList!=null'> and id not in (${preIdList}) </if> "
			+ "<![CDATA[   and rzh_yesorno = '1' and tax_rate = #{taxRate} ]]> "
			+ "<if test='currentTaxPeriod!=null'> <![CDATA[   and rzh_belong_date < #{currentTaxPeriod} ]]>  </if>    "
			+ "<if test='notQueryOil!=null'> <![CDATA[   and is_oil <> #{notQueryOil} ]]>  </if> "
			+ " order by invoice_date ${invoiceDateOrder}"
			+ " </script>")
	List<TDxRecordInvoiceEntity> queryNoMatchBlueInvoice(@Param("preIdList") String preIdList,
			@Param("sellerTaxNo") String sellerTaxNo, @Param("purchaserTaxNo") String purchaserTaxNo,
			@Param("invoiceDate") Date invoiceDate, @Param("currentTaxPeriod") String currentTaxPeriod,
			@Param("taxRate") BigDecimal taxRate, @Param("notQueryOil") String notQueryOil,
			@Param("invoiceDateOrder") String invoiceDateOrder);

    /**
     * 联扫描表查询扫描人
     * @param uuid
     * @return
     */
    List<TDxRecordInvoiceEntity> queryRecordInvByUuid(@Param("uuid") String uuid);

}
