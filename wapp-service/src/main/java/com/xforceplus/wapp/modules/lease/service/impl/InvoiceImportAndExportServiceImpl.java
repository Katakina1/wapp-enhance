package com.xforceplus.wapp.modules.lease.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.QuestionnaireImport;
import com.xforceplus.wapp.modules.lease.dao.InvoiceImportAndExportDao;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportExcelEntity;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportImportEntity;
import com.xforceplus.wapp.modules.lease.export.InvoiceImportAndExportImport;
import com.xforceplus.wapp.modules.lease.service.InvoiceImportAndExportService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class InvoiceImportAndExportServiceImpl implements InvoiceImportAndExportService {
    private static final Logger LOGGER = getLogger(InvoiceImportAndExportServiceImpl.class);

    @Autowired
    private InvoiceImportAndExportDao invoiceImportAndExportDao;
    @Override
    public List<InvoiceImportAndExportEntity> invoiceImportAndExportlist(Map<String, Object> map){
        return invoiceImportAndExportDao.invoiceImportAndExportlist(map);
    }
    @Override
    public List<InvoiceImportAndExportEntity> invoiceImportAndExportlistAll(Map<String, Object> map){
        return invoiceImportAndExportDao.invoiceImportAndExportlistAll(map);
    }

    @Override
    public Integer invoiceImportAndExportlistCount(Map<String, Object> map){
        return invoiceImportAndExportDao.invoiceImportAndExportlistCount(map);
    }

    @Override
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {
        final InvoiceImportAndExportImport invoiceImport = new InvoiceImportAndExportImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            List currentList= Lists.newArrayList();
            Map<String,Object> errorMap=invoiceImport.analysisExcel();
            final List<InvoiceImportAndExportImportEntity> certificationEntityList =(List<InvoiceImportAndExportImportEntity>) errorMap.get("list");
            if (certificationEntityList.size()>0) {
                certificationEntityList.forEach(importEntity->{
                    Map<String,Object> mapps= Maps.newHashMapWithExpectedSize(10);
                    mapps.put("id",importEntity.getId());
                    mapps.put("shopNo",importEntity.getShopNo());
                    mapps.put("peRiod",importEntity.getPeRiod());
                    mapps.put("matChing",importEntity.getMatChing());
                    mapps.put("matChingDate",importEntity.getMatChingDate());
                    mapps.put("taxCode",importEntity.getTaxCode());

                    //mapps.put("venderid",params.get("venderid"));
                    //mapps.put("jvcode",params.get("jvcode"));
                    //mapps.put("gfName",params.get("gfName"));
                    //mapps.put("xfTaxno",params.get("xfTaxno"));
                    //mapps.put("checkNo",importEntity.getId());
                    if (importEntity.getId() != 0) {
                        invoiceImportAndExportDao.invoiceImportAndExportUpdate(mapps);
                    }

                });
                map.put("invoiceQueryList",currentList);
                map.put("success", Boolean.TRUE);
                map.put("reason", "批量导入完成！成功"+currentList.size()+"条，失败"+errorMap.get("errorCount")+"条");
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
    public List<InvoiceImportAndExportExcelEntity> invoiceImportAndExportlistExcelAll(Map<String, Object> map) {
        List<InvoiceImportAndExportEntity> list = invoiceImportAndExportDao.invoiceImportAndExportlistAll(map);
        List<InvoiceImportAndExportExcelEntity> excelList =  new LinkedList();
        InvoiceImportAndExportExcelEntity excel = null;
        int index = 1;
        for(InvoiceImportAndExportEntity entity:list){
            excel = new InvoiceImportAndExportExcelEntity();
            excel.setCompanyCode(entity.getCompanyCode());
            excel.setId(entity.getId()+"");
            excel.setInvoiceAmount(fromAmount(entity.getInvoiceAmount()));
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceDate(formatDate(entity.getInvoiceDate()));
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceType(entity.getInvoiceType());
            excel.setJvCode(entity.getJvCode());
            excel.setMatChing(formatedxhyMatchStatusType(entity.getMatChing()));
            excel.setMatChingDate(formatDate(entity.getMatChingDate()));
            excel.setPeRiod(entity.getPeRiod());
            excel.setReMark(entity.getReMark());
            excel.setRownumber(""+index++);
            excel.setShopNo(entity.getShopNo());
            excel.setTaxAmount(fromAmount(entity.getTaxAmount()));
            excel.setTaxRate(fromAmounts(entity.getTaxRate()));
            excel.setTotalAmount(fromAmount(entity.getTotalAmount()));
            excel.setVenderId(entity.getVenderId());
            excel.setVenderName(entity.getVenderName());
            excel.setTaxCode(entity.getTaxCode());
            excelList.add(excel);
        }
        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatedxhyMatchStatusType(String dxhyMatchStatus){
        String value="";
        if(dxhyMatchStatus==null || dxhyMatchStatus == ""){
            value= "未成功";
        }else if("0".equals(dxhyMatchStatus)){
            value="未成功";
        }else if("1".equals(dxhyMatchStatus)) {
            value = "成功";
        }
        return value;
    }

    private String fromAmount(Double d){
        BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0.00");
        return df.format(d);
    }
    private String fromAmounts(Double d){
        BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0");
        return df.format(d);
    }
}
