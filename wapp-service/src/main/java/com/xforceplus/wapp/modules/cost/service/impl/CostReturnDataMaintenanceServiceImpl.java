package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.InformationInquiry.dao.QuestionnaireDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.QuestionnaireImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.QuestionnaireService;
import com.xforceplus.wapp.modules.cost.dao.CostReturnDataMaintenanceDao;
import com.xforceplus.wapp.modules.cost.entity.ApplicantEntity;
import com.xforceplus.wapp.modules.cost.importTemplate.ApplicantImport;
import com.xforceplus.wapp.modules.cost.service.CostReturnDataMaintenanceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CostReturnDataMaintenanceServiceImpl implements CostReturnDataMaintenanceService {
    private static final Logger LOGGER = getLogger(CostReturnDataMaintenanceServiceImpl.class);


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private CostReturnDataMaintenanceDao costReturnDataMaintenanceDao;
    @Override
    public List<ApplicantEntity> questionnairelist(Map<String, Object> map){
        return costReturnDataMaintenanceDao.questionnairelist(map);
    }
    @Override
    public List<QuestionnaireEntity> questionnairelistAll(Map<String, Object> map){
        return costReturnDataMaintenanceDao.questionnairelistAll(map);
    }

    @Override
    public Integer questionnairelistCount(Map<String, Object> map){
        return costReturnDataMaintenanceDao.questionnairelistCount(map);
    }

    @Override
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {
        final ApplicantImport invoiceImport = new ApplicantImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            int index = 0;
            List currentList= Lists.newArrayList();
            final List<ApplicantEntity> certificationEntityList = invoiceImport.analysisExcel();
            index = certificationEntityList.size();
            if (certificationEntityList.size()>0) {
                certificationEntityList.forEach(importEntity->{
                    Map<String,Object> mapps= Maps.newHashMapWithExpectedSize(10);
                    mapps.put("epsNo",importEntity.getEpsNo());
                    mapps.put("shopNo",importEntity.getShopNo());
                    mapps.put("applicantDepartment",importEntity.getApplicantDepartment());
                    mapps.put("applicantNo",importEntity.getApplicantNo());
                    mapps.put("applicantName",importEntity.getApplicantName());
                    mapps.put("applicantCall",importEntity.getApplicantCall());
                    mapps.put("applicantSubarea",importEntity.getApplicantSubarea());
                    costReturnDataMaintenanceDao.saveInvoice(mapps);
                });
                map.put("invoiceQueryList",currentList);
                map.put("success", Boolean.TRUE);
                map.put("reason", "批量导入成功！总共导入{"+index+"}条");
            }else {
                LOGGER.info("读取到excel数据格式有误");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel数据格式有误！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }


        return map;
    }


    @Override
    public void queryuuid(int id) {
        costReturnDataMaintenanceDao.queryuuid(id);
    }

    @Override
    public void xqueryuuids(Long id) {
        costReturnDataMaintenanceDao.xqueryuuids(id);
    }



    @Override
    public List<QuestionnaireExcelEntity> transformExcle(List<QuestionnaireEntity> list){
        List<QuestionnaireExcelEntity> list2=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            QuestionnaireEntity entity=list.get(i);
            QuestionnaireExcelEntity entity1=new QuestionnaireExcelEntity();
           entity1.setRownumber0( entity.getId()+"");
           entity1.setRownumber1(  i+1+"");
           entity1.setCell3(  entity.getInputUser());
           entity1.setCell4(  entity.getjV());
           entity1.setCell5(  entity.getVendorNo());
           entity1.setCell6(  entity.getInvNo());
           entity1.setCell10(  entity.getTaxType());
           entity1.setCell12(  entity.getBatchID());
           entity1.setCell13(  entity.getpONo());
           entity1.setCell14(  entity.getTrans());
           entity1.setCell15(  entity.getRece());
            list2.add(entity1);
        }
        return list2;
    }
    @Override
    public String queryMatchno(String uuid){
        return costReturnDataMaintenanceDao.queryMatchno(uuid);
    }
    @Override
    public int updateIsDel(String isdel,String matchno){
        return costReturnDataMaintenanceDao.updateIsDel(isdel,matchno);
    }
    @Override
    public String getUuId(String invNo, String vendorNo, Date invoiceDate){
        return  costReturnDataMaintenanceDao.getUuId(invNo,vendorNo,invoiceDate);
    }
}
