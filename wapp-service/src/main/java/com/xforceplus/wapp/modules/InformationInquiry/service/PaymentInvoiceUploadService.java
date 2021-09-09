package com.xforceplus.wapp.modules.InformationInquiry.service;



import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface PaymentInvoiceUploadService {

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryList( Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map);

//    /**
//     * 保存扣款发票信息
//     */
//    void saveInvoice( PaymentInvoiceUploadEntity entity);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryListAll(Map<String, Object> map);

    List<PaymentInvoiceUploadEntity> queryListAllFail(Map<String, Object> map);

    int delete(String loginName);
    int deletefail(Long id);


    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName);

    List<PaymentInvoiceUploadExcelEntity> transformExcle(List<PaymentInvoiceUploadEntity> list);
}
