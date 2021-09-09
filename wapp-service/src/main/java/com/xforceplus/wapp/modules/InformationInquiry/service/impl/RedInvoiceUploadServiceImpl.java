package com.xforceplus.wapp.modules.InformationInquiry.service.impl;


import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.InformationInquiry.dao.RedInvoiceUploadDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.RedInvoiceUploadImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.RedInvoiceUploadService;
import com.xforceplus.wapp.modules.InformationInquiry.service.RedInvoiceUploadService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class RedInvoiceUploadServiceImpl implements RedInvoiceUploadService {
    private static final Logger LOGGER = getLogger(RedInvoiceUploadServiceImpl.class);
    @Autowired
    private RedInvoiceUploadDao redInvoiceUploadDao;

    @Override
    public List<RedInvoiceUploadEntity> queryList(Map<String, Object> map) {
        return redInvoiceUploadDao.queryList(map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map) {

        return redInvoiceUploadDao.queryTotalResult(map);
    }

    @Override
    public List<RedInvoiceUploadEntity> queryListAll(Map<String, Object> map) {

        return redInvoiceUploadDao.queryListAll(map);
    }

    /**
     * 解析excel数据，解析保存入库
     *
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final RedInvoiceUploadImport redInvoiceUploadImport = new RedInvoiceUploadImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<RedInvoiceUploadEntity> redInvoiceList = redInvoiceUploadImport.analysisExcel();
            if (redInvoiceList.size() > 10000) {
                LOGGER.info("excel数据不能超过10000条");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel数据不能超过10000条！");
                return map;
            }


            if (!redInvoiceList.isEmpty()) {
                Map<String, List<RedInvoiceUploadEntity>> entityMap =RedInvoiceImportData(redInvoiceList);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
                map.put("success", Boolean.TRUE);
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }
    private Map<String, List<RedInvoiceUploadEntity>> RedInvoiceImportData(List<RedInvoiceUploadEntity> redInvoiceList){
        //返回值
        final Map<String, List<RedInvoiceUploadEntity>> map = newHashMap();
        //导入成功的数据集
        final List<RedInvoiceUploadEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<RedInvoiceUploadEntity> errorEntityList = newArrayList();
        for(RedInvoiceUploadEntity entity : redInvoiceList ){
           int num= redInvoiceUploadDao.selectCount(entity);
           if(num>0){
               errorEntityList.add(entity);
           }else{
               successEntityList.add(entity);
           }
        }
        List<List<RedInvoiceUploadEntity>> splitProtocolList=splitList(successEntityList,100);
        for(List<RedInvoiceUploadEntity> list : splitProtocolList ) {
            redInvoiceUploadDao.saveInvoice(list);
        }
        for(RedInvoiceUploadEntity entity : errorEntityList ){
            redInvoiceUploadDao.updateRedEntity(entity);
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }
    private static  List<List<RedInvoiceUploadEntity>> splitList(List<RedInvoiceUploadEntity> sourceList, int  batchCount) {
        List<List<RedInvoiceUploadEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }
    @Override
    public List<RedInvoiceUploadExcelEntity> transformExcle(List<RedInvoiceUploadEntity> list) {
        List<RedInvoiceUploadExcelEntity> list2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RedInvoiceUploadEntity entity = list.get(i);
            RedInvoiceUploadExcelEntity redInvoiceUploadExcelEntity = new RedInvoiceUploadExcelEntity();
            //序号
            redInvoiceUploadExcelEntity.setIndexNo(String.valueOf(i + 1));
            redInvoiceUploadExcelEntity.setVenderid(entity.getVenderid());
            redInvoiceUploadExcelEntity.setRedAmount(formatAmount(entity.getRedAmount()));
            redInvoiceUploadExcelEntity.setInvoiceOrAgreementNo(entity.getInvoiceOrAgreementNo());
            redInvoiceUploadExcelEntity.setRedInvoiceNo(entity.getRedInvoiceNo());
            redInvoiceUploadExcelEntity.setRedInvoiceDate(formatStrDate(entity.getRedInvoiceDate()));
            redInvoiceUploadExcelEntity.setVenderName(entity.getVenderName());
            /*redInvoiceUploadExcelEntity.setTaxAmount(entity.getTaxAmount());
            redInvoiceUploadExcelEntity.setTaxRate(entity.getTaxRate());*/
            redInvoiceUploadExcelEntity.setRedType(entity.getRedType());
            list2.add(redInvoiceUploadExcelEntity);
        }

        return list2;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String formatStrDate(String source) {
        return source == null ? "" : source.substring(0,10);
    }
    private String taxRate(String taxRate) {
        BigDecimal rate = new BigDecimal(taxRate);
        String str;
        if (rate.compareTo(new BigDecimal(1)) == -1) {
            rate = rate.multiply(new BigDecimal(100));
            str = rate.toString();
        } else {
            str = taxRate;
        }
        return str;
    }
    private String formatAmount(String d) {
        try {
            if(StringUtils.isEmpty(d)){
                return "";
            }else{
                BigDecimal b=new BigDecimal(Double.parseDouble(d));
                DecimalFormat df=new DecimalFormat("######0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                return df.format(b);
            }
        }catch (Exception e){
            return "";
        }
    }
}