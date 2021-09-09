package com.xforceplus.wapp.modules.signin.dao;

import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.signin.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/** 
* @ClassName: IInvoiceSignDao 
* @Description: 扫描发票数据入库
* @author yuanlz
* @date 2016年12月1日 下午3:55:10 
*  
*/
@Mapper
public interface ScannerSignDao {

	
	// 获取库中最大流水号
	 String getMaxSerialNo();

	OrgTaxNoInfo getOrgTaxGfName(@Param("gfTaxNo")String gfTaxNo);


    int deleteInvoice(Long id);

	InvoiceSavePo selectByUuid(String uuid);

	Integer saveInvoice(InvoiceSavePo savePo);

	List<RecordInvoiceQueryByCodeAndNoVo> queryByCodeAndNo(
			RecordInvoiceQueryByCodeAndNoPo param);

    Integer saveImg(@Param("schemaLabel") String schemaLabel,@Param("savePo")InvoiceImgSavePo savePo);

	InvoiceScan recordInvoiceByUuid(String scpz);

	int deleteById(String scpz);

	/**
	 * 根据扫描纸质发票的结果插入底账表
	 */
	int insertFromScan(RecordInvoiceCreateSignPo po);

	InvoiceImgQueryVo getImg(@Param("schemaLabel") String schemaLabel,@Param("scanId")String scanId);

	int deleteInvoiceImg (@Param("schemaLabel") String schemaLabel,@Param("scanId")String scanId);

	int invoiceDelete(String uuId);

	int updateRecordInvoiceHandleState(String uuId);
	int updateRecordInvoiceState(String uuId);

	InvoiceSavePo selectByInvoice(String uuId);

	int deleteRecordInvoiceById(String uuId);

	int saveRecordInvoice(@Param("recordInvoice")RecordInvoice recordInvoice);
	int saveRecordInvoiceDetail( @Param("detailList") List<RecordInvoiceDetail> detailList);

	int savedel(String uuId);

	InvoiceSavePo findDelByUuid(String uuId);

	int delDel(String uuId);
}
