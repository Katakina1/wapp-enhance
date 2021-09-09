package com.xforceplus.wapp.modules.redInvoiceManager.service;



import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InputRedTicketInformationEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarleQueryExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface InputRedTicketInformationService {

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListAll(Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListAllExport(Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     *
     * 查询红票明细信息
     * @param params
     * @return
     */
    List<InvoiceListEntity> getRedInvoiceList(@Param("map") Map<String, Object> params);
    List<InvoiceListEntity> getRedInvoiceList1(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询红票明细条数
     * @param params
     * @return
     */
    Integer getRedInvoiceCount(@Param("map") Map<String, Object> params);
    Integer getRedInvoiceCount1(@Param("map") Map<String, Object> params);

    List<OrgEntity> getGfNameAndTaxNo(Long userId);

    RedTicketMatch selectNoticeById(Map<String,Object> params);

    PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String,Object> params);

    /**
     * 判断发票类型是否为普票
     * @param invoiceCode
     * @return
     */
    String getFplx(String invoiceCode);

    PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String,Object> params);

    void emptyRedInvoice(Long id);

    void emptyRecord(String uuid);

    InputRedTicketInformationEntity queryUuid(Long id);

    RedTicketMatch selectRedTicketById(Map<String,Object> map);

    Map<String,Object> importInvoice(Map<String,Object> params, MultipartFile multipartFile);

//    UploadScarletLetterEntity queryJvCode(String serialNumber);
//
//    UploadScarletLetterEntity queryCompanyCode( String jvCode);
    Map<String, Object> sendEmail(String date1, String date2, File fi);
    List<UploadScarleQueryExcelEntity> toExcel(List<UploadScarletLetterEntity> list);
}
