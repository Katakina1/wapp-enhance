package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.SignForQueryChargeDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.SignForQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcel1Entity;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryChargeService;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SignForQueryChargeServiceImpl implements SignForQueryChargeService {

    @Autowired
    private SignForQueryChargeDao signForQueryChargeDao;

    @Override
    public List<SignForQueryEntity> queryList(Map<String, Object> map) {
        return signForQueryChargeDao.queryList(map);
    }
    @Override
    public Integer invoiceMatchCount(Map<String, Object> map){
        return signForQueryChargeDao.invoiceMatchCount(map);
    }
    @Override
    public List<SignForQueryExcel1Entity> queryListAll(Map<String, Object> map) {
        List<SignForQueryEntity> list =  signForQueryChargeDao.queryList(map);
        List<SignForQueryExcel1Entity>  excelList = new LinkedList();
        SignForQueryExcel1Entity excel = null;
        int index = 1;
        for(SignForQueryEntity entity : list){
            excel = new SignForQueryExcel1Entity();
            excel.setCompanyCode(entity.getCompanyCode());
            excel.setFlowType(formatFlowType(entity.getFlowType()));
            excel.setGfName(entity.getGfName());
            excel.setInvoiceAmount(entity.getInvoiceAmount().toString().substring(0,entity.getInvoiceAmount().toString().length()-2));
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceDate(formatDate(entity.getInvoiceDate()));
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceType(entity.getInvoiceType());
            excel.setJvCode(entity.getJvCode());
            excel.setNotes(entity.getNotes());
            excel.setQsDate(formatDate(entity.getQsDate()));
            excel.setRownumber(""+index++);
            excel.setQsStatus(formateVenderType(entity.getQsStatus()));
            excel.setScanFailReason(entity.getScanFailReason());
            excel.setScanId(entity.getScanId());
            excel.setScanMatchStatus(scanMatchStatus(entity.getScanMatchStatus()));
            excel.setTaxAmount(entity.getTaxAmount().toString().substring(0,entity.getTaxAmount().toString().length()-2));
            excel.setVenderid(entity.getVenderid());
            excel.setXfName(entity.getXfName());
            excel.setEpsNo(entity.getEpsNo());
            excelList.add(excel);
        }

        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String qsStatus){
        String value="";
        if("0".equals(qsStatus)){
            value="签收失败";
        }else if("1".equals(qsStatus)){
            value="签收成功";
        }
        return value;
    }

    private String scanMatchStatus(String qsStatus) {
        String value = "";
        if ("0".equals(qsStatus)) {
            value = "未匹配";
        } else if ("1".equals(qsStatus)) {
            value = "匹配成功";
        } else if ("2".equals(qsStatus)) {
            value = "匹配失败";

        }else{
            value = "未匹配";
        }
        return value;
    }

    private String formatFlowType(String type){
        return null==type ? "" :
                "1".equals(type) ? "商品" :
                        "2".equals(type) ? "费用" :
                                "3".equals(type) ? "外部红票" :
                                        "4".equals(type) ? "内部红票" :
                                                "5".equals(type) ? " 供应商红票" :
                                                        "6".equals(type) ? " 租赁" :
                                                                "7".equals(type) ? "直接认证":
                                                                        "8".equals(type) ? "Ariba":"";
    }
}
