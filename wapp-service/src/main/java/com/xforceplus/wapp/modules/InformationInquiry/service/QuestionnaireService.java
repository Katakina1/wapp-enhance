package com.xforceplus.wapp.modules.InformationInquiry.service;


import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface QuestionnaireService {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<QuestionnaireEntity> questionnairelist(Map<String, Object> map);

    List<QuestionnaireEntity> questionnairelistAll(Map<String, Object> map);

    Integer questionnairelistCount(Map<String, Object> map);

    Map<String,Object> importInvoice(Map<String,Object> params, MultipartFile multipartFile);

    List<QuestionnaireEntity> queryuuid(Long id);

    void inputrefundyesno(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost);

    void cancelTheRefund(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost);

    //void queryuuids(Long id);

    void xqueryuuids(Long id);

    void cancelTheProcess(String id);
    void xqueryuuidss(Long id);

    void cancelTheProcesss(String id);

    List<QuestionnaireExcelEntity> transformExcle(List<QuestionnaireEntity> list);

    String queryMatchno(String uuid);

    int updateIsDel(String isdel,String matchno);

    String getUuId(String invNo, String vendorNo, Date invoiceDate,String invoiceCost);
    String getBatchId(Long id);
    void invoiceCl(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost);
}
