package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


/**
* <p>
* 底账明细表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-25
*/
public interface TDxRecordInvoiceDetailDao extends BaseMapper<TDxRecordInvoiceDetailEntity> {

	/**
	 * 查询发票明细信息
	 * 
	 * @param uuid
	 * @return
	 */
	@Select("SELECT * FROM t_dx_record_invoice_detail where uuid = #{uuid}")
	List<TDxRecordInvoiceDetailEntity> queryByUuid(@Param("uuid") String uuid);

    /**
     * 查询发票明细第一条的货物或应税劳务名称
     * @param uuid
     * @return
     */
    String getGoodsName(@Param("uuid") String uuid);

}
