package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.SignForQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryService;

import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SignForQueryServiceImpl implements SignForQueryService {

    @Autowired
    private SignForQueryDao signForQueryDao;

    @Override
    public List<SignForQueryEntity> queryList(Map<String, Object> map) {
        return signForQueryDao.queryList(map);
    }
    @Override
    public Integer invoiceMatchCount(Map<String, Object> map){
        return signForQueryDao.invoiceMatchCount(map);
    }
    @Override
    public List<SignForQueryEntity> queryListAll(Map<String, Object> map) {

        return signForQueryDao.queryListAll(map);
    }

    @Override
    public List<SignForQueryExcelEntity> transformExcle(List<SignForQueryEntity> list){
        List<SignForQueryExcelEntity> list2=new ArrayList<>();
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < list.size(); i++) {
            SignForQueryEntity entity = list.get(i);
            SignForQueryExcelEntity entity1 = new SignForQueryExcelEntity();
            //序号
            entity1.setCell0(String.valueOf(i + 1));

            entity1.setCell1(formateVenderType(entity.getQsStatus()));

            entity1.setCell2( entity.getNotes());

            entity1.setCell3(formatDate(entity.getQsDate()));

            entity1.setCell4(entity.getScanId());

            entity1.setCell5(entity.getInvoiceCode());

            entity1.setCell6( entity.getInvoiceNo());

            entity1.setCell7(formatDate(entity.getInvoiceDate()));


            entity1.setCell8(formatFlowType(entity.getFlowType()));

            entity1.setCell9(entity.getVenderid());

            entity1.setCell10(entity.getGfName());

                entity1.setCell11( entity.getXfName());


            entity1.setCell12(entity.getInvoiceAmount().toString());

            entity1.setCell13(entity.getTaxAmount().toString());

            entity1.setCell14(entity.getJvCode());

            entity1.setCell15(entity.getCompanyCode());

            entity1.setCell16(scanMatchStatus(entity.getScanMatchStatus()));

            entity1.setCell17(entity.getScanFailReason());

            entity1.setCell18(entity.getEpsNo());
            entity1.setCell19(isDel(entity.getIsdel()));


            list2.add(entity1);

        }
        return list2;
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
    private String isDel(String isdel) {
        String value = "";
        if ("0".equals(isdel)) {
            value = "未退票";
        } else if ("1".equals(isdel)) {
            value = "已退票";
        }else{
            value = "未退票";
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
