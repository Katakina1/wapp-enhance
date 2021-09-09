package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CostScanConfirmDao {
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("map") Map<String, Object> map);
    int queryCount(@Param("map") Map<String, Object> map);
    List<SelectionOptionEntity> getJV(String taxNo);
    List<SelectionOptionEntity> getVender();
    Integer updateRecordInvoice(ConfirmInvoiceEntity entity);
    Integer updateInvoice(ConfirmInvoiceEntity entity);
    String getCompanyCode(String jvcode);
    Integer jvOk(@Param("jvcode") String jvcode, @Param("gfTaxNo") String gfTaxNo);
    Integer venderOk(String venderid);
}
