package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.CostGenerateRefundNumberDao;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.CostGenerateRefundNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CostGenerateRefundNumberServiceImpl implements CostGenerateRefundNumberService {

    @Autowired
    private CostGenerateRefundNumberDao costgenerateRefundNumberDao;

    @Override
    public List<GenerateRefundNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return costgenerateRefundNumberDao.queryList(schemaLabel,map);
    }

    @Override
    public void rebatenobyId(String schemaLabel, Long id,String rebateNo) {
        costgenerateRefundNumberDao.rebatenobyId(schemaLabel, id, rebateNo);
    }

    @Override
    public GenerateRefundNumberEntity queryrebateno(Long id) {
        return costgenerateRefundNumberDao.queryrebateno(id);
    }

    @Override
    public void rebatenobyuuid(String uuid) {
        costgenerateRefundNumberDao.rebatenobyuuid(uuid);
    }

    @Override
    public GenerateRefundNumberEntity queryuuid1(Long id) {
        return costgenerateRefundNumberDao.queryuuid1(id);
    }


    @Override
    public GenerateRefundNumberEntity querymaxrebateno() {
        return costgenerateRefundNumberDao.querymaxrebateno();
    }


    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return costgenerateRefundNumberDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<GenerateRefundNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return costgenerateRefundNumberDao.queryListAll(schemaLabel,map);
    }

    @Override
    public List<GenerateRefundNumberExcelEntity> queryListAlls(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<GenerateRefundNumberEntity> list =  costgenerateRefundNumberDao.queryList(schemaLabel,map);
        List<GenerateRefundNumberExcelEntity> excelList = new LinkedList();
        int index = 1;
        GenerateRefundNumberExcelEntity excel = null;
        for(GenerateRefundNumberEntity entity :list){
            excel = new GenerateRefundNumberExcelEntity();
            excel.setRownumber(""+index++);
            excel.setBelongsTo(entity.getBelongsTo());
            excel.setCreateDate(formatDate(entity.getCreateDate()));
            excel.setEpsNo(entity.getEpsNo());
            try{
                excel.setInvoiceAmount(formatAmount(entity.getInvoiceAmount().toString()));
            }catch (Exception e){
                excel.setInvoiceAmount("");
            }
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceDate(formatDate(entity.getInvoiceDate()));
            excel.setInvoiceSerialNo(entity.getScanId());
            excel.setRefundCode(entity.getRefundCode());
            excel.setRefundReason(entity.getRefundReason());
            try{
                excel.setTaxAmount(formatAmount(entity.getTaxAmount().toString()));
            }catch (Exception e){
                excel.setTaxAmount("");
            }
            excel.setVenderId(entity.getVenderId());
            excelList.add(excel);
        }
        return excelList;
    }

    @Override
    public List<GenerateRefundNumberEntity> epsDetaList(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return costgenerateRefundNumberDao.epsDetaList(schemaLabel,map);
    }
    @Override
    public List<GenerateRefundNumberEntity> queryepsno(String epsNo){
        return costgenerateRefundNumberDao.queryepsno(epsNo);
    }

    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String errCode(String getisdel,String code){
        String value="";
        if("2".equals(getisdel)){
            value="";
        }else{
            value=code;
        }
        return value;
    }

}
