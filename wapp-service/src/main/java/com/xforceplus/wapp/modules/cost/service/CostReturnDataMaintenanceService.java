package com.xforceplus.wapp.modules.cost.service;


import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.cost.entity.ApplicantEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CostReturnDataMaintenanceService {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<ApplicantEntity> questionnairelist(Map<String, Object> map);

    List<QuestionnaireEntity> questionnairelistAll(Map<String, Object> map);

    Integer questionnairelistCount(Map<String, Object> map);

    Map<String,Object> importInvoice(Map<String, Object> params, MultipartFile multipartFile);

    void queryuuid(int id);




    //void queryuuids(Long id);

    void xqueryuuids(Long id);


    List<QuestionnaireExcelEntity> transformExcle(List<QuestionnaireEntity> list);

    String queryMatchno(String uuid);

    int updateIsDel(String isdel, String matchno);

    String getUuId(String invNo, String vendorNo, Date invoiceDate);
}
