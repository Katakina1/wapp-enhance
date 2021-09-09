package com.xforceplus.wapp.modules.pack.service.impl;


import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.pack.dao.GenerateBindNumberDao;
import com.xforceplus.wapp.modules.pack.entity.BindNumberExcelEntity;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.QueryFlowTypeEntity;
import com.xforceplus.wapp.modules.pack.export.BindNumberImport;
import com.xforceplus.wapp.modules.pack.service.GenerateBindNumberService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.export.EnterPackageNumberImport;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Service
public class GenerateBindNumberServiceImpl implements GenerateBindNumberService {

    @Autowired
    private GenerateBindNumberDao generateBindNumberDao;

    @Override
    public List<GenerateBindNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return generateBindNumberDao.queryList(schemaLabel,map);
    }

//    @Override
//    public void bbindingnobyId(String schemaLabel, Long[] ids,String bbindingNo) {
//        generateBindNumberDao.bbindingnobyId(schemaLabel, ids, bbindingNo);
//    }
    @Override
    public void bbindingnobyId(String schemaLabel, Long id,String bbindingNo) {
        generateBindNumberDao.bbindingnobyId(schemaLabel, id, bbindingNo);
    }


    @Override
    public GenerateBindNumberEntity querybbindingno(Long id) {
        return generateBindNumberDao.querybbindingno(id);
    }

    @Override
    public GenerateBindNumberEntity querymaxbbindingno() {
        return generateBindNumberDao.querymaxbbindingno();
    }

    @Override
    public List<GenerateBindNumberEntity> getRecordInvoiceList(Map<String, Object> params) {
        return generateBindNumberDao.getRecordInvoiceList(params);
    }

    @Override
    public Integer getRecordInvoiceListCount(Map<String, Object> params) {
        return generateBindNumberDao.getRecordInvoiceListCount(params);
    }

    @Override
    public List<GenerateBindNumberEntity> getPOList(Map<String, Object> params) {
        return generateBindNumberDao.getPOList(params);
    }

    @Override
    public Integer getPOListCount(Map<String, Object> params) {
        return generateBindNumberDao.getPOListCount(params);
    }

    @Override
    public List<GenerateBindNumberEntity> getClaimList(Map<String, Object> params) {
        return generateBindNumberDao.getClaimList(params);
    }

    @Override
    public Integer getClaimListCount(Map<String, Object> params) {
        return generateBindNumberDao.getClaimListCount(params);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {

        return generateBindNumberDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<GenerateBindNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map) {

        return generateBindNumberDao.queryListAll(schemaLabel,map);
    }

    @Override
    public List<QueryFlowTypeEntity> searchFlowType() {
        List<QueryFlowTypeEntity> list=generateBindNumberDao.searchFlowType();
//        List<QueryFlowTypeEntity> lists=new ArrayList<QueryFlowTypeEntity>();
//        for (QueryFlowTypeEntity qe:list) {
//            if(!qe.getLabel().equals("内部红票")&&!qe.getLabel().equals("外部红票")){
//                lists.add(qe);
//            }
//        }
        return list;
    }
    @Override
    public List<BindNumberExcelEntity> transformExcle(List<GenerateBindNumberEntity> list){
        List<BindNumberExcelEntity> belist=new ArrayList<>();
        for (GenerateBindNumberEntity ge:list) {
            BindNumberExcelEntity be=new BindNumberExcelEntity();
            be.setId(ge.getId().toString());
            be.setScanDate(formatDateString(ge.getCreateDate()));
            be.setScanNo(ge.getInvoiceSerialNo());
            be.setVenderName(ge.getVenderName());
            be.setVenderNo(ge.getVenderId());
            be.setGfName(ge.getGfName());
            be.setGsdm(ge.getCompanyCode());
            be.setInvoiceCode(ge.getInvoiceCode());
            be.setInvoiceNo(ge.getInvoiceNo());
            be.setInvoiceDate(formatDateString(ge.getInvoiceDate()));
            try {
                be.setInvoiceAmount(formatTotalAmount(ge.getInvoiceAmount().toString()));
                be.setTaxAmount(formatTotalAmount(ge.getTaxAmount().toString()));
            }catch (Exception e){

            }
            be.setTaxRate(formatTaxRate(ge.getTaxRate()));
            be.setInvoiceType(invResult(ge.getInvoiceType()));
            be.setServiceType(formatFlowType(ge.getFlowType()));
            belist.add(be);
        }
        return belist;
    }
    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final BindNumberImport enterPackageNumberImport = new BindNumberImport(multipartFile);
        Map<String, Object> map = newHashMap();
        try {
            map = enterPackageNumberImport.analysisExcel();
            if(map.get("status").equals("false")){
                map.put("success", Boolean.FALSE);
                map.put("reason", "导入数据不能超过5000条！");
            }
            List<GenerateBindNumberEntity> redInvoiceList = (List<GenerateBindNumberEntity>)map.get("enjoySubsidedList");

            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<GenerateBindNumberEntity>> entityMap =RedInvoiceImportData(redInvoiceList);
                map.put("reason", entityMap.get("successEntityList").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            e.printStackTrace();
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<GenerateBindNumberEntity>> RedInvoiceImportData(List<GenerateBindNumberEntity> redInvoiceList){
        //返回值
        final Map<String, List<GenerateBindNumberEntity>> map = newHashMap();
        //导入成功的数据集
        final List<GenerateBindNumberEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<GenerateBindNumberEntity> errorEntityList = newArrayList();
        //导入失败的数据集1
        final List<GenerateBindNumberEntity> errorEntityList1 = newArrayList();

        redInvoiceList.forEach(redInvoiceData -> {
            Long rebateNo = redInvoiceData.getId();
            String bindNumber = redInvoiceData.getBbindingNo();
             if(bindNumber.isEmpty()){
                errorEntityList.add(redInvoiceData);
            } else {
                successEntityList.add(redInvoiceData);
            }
        });

        //如果都校验通过，保存入库
        for(GenerateBindNumberEntity red: successEntityList){
            generateBindNumberDao.bbindingnobyId(null,red.getId(),red.getBbindingNo());
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }

    private String invResult(String code){
        String value=null;
        if("01".equals(code)){
            value="专票";
        }else if("04".equals(code)){
            value="普票";
        }else if("11".equals(code)){
            value="卷票";
        }else{
            value=null;
        }
        return value;
    }
    private String formatTaxRate(BigDecimal taxRate){
        String tax ="";
        try{
            tax= taxRate.toString();
            if ("".equals(tax)|| tax==null) {
                return "--";
            }else {
              tax = tax.substring(0, tax.indexOf('.')) + "%";
            }
        }catch (Exception e){
            tax="--";
        }
        return  tax;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatTotalAmount(String totalAmount) {
        if("".equals(totalAmount)|| totalAmount == null){
            return "--";
        }else {
            return  totalAmount.substring(0, totalAmount.indexOf('.')+3);
        }
    }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatFlowType(String type) {
        return null==type ? "" :
                "1".equals(type) ? "商品" :
                        "2".equals(type) ? "费用" :
                                "3".equals(type) ? "外部红票" :
                                        "4".equals(type) ? "内部红票" :
                                                "5".equals(type) ? " 供应商红票" :
                                                        "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }
}
