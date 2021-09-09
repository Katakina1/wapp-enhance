package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.ScanningDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ScanningServiceImpl implements ScanningService {
    @Autowired
    private ScanningDao scanningDao;
    @Override
    public List<ScanningEntity> scanningList(Map<String, Object> map){
        return scanningDao.scanningList(map);
    }
    @Override
    public Integer scanningCount(Map<String, Object> map){
        return scanningDao.scanningCount(map);
    }

    public String serachUserOrgType(Map<String, Object> map){
        return scanningDao.serachUserOrgType(map);
    }

    @Override
    public List<ScanningExcelEntity> selectScanningList(Map<String, Object> map) {
        List<ScanningEntity> list = scanningDao.scanningList(map);
        List<ScanningExcelEntity> excelList = new LinkedList<ScanningExcelEntity>();
        ScanningExcelEntity excel = null;
        for(ScanningEntity entity : list){
                excel = new ScanningExcelEntity();
                excel.setRownumber(entity.getRownumber());
                excel.setJvCode(entity.getJvCode());
                excel.setCompanyCode(entity.getCompanyCode());
                excel.setVenderid(entity.getVenderid());
                excel.setGfName(entity.getGfName());
                excel.setXfName(entity.getXfName());
                excel.setInvoiceNo(entity.getInvoiceNo());
                excel.setInvoiceDate(formatDate(entity.getInvoiceDate()));
                excel.setInvoiceAmount(formatAmount(entity.getInvoiceAmount().toString()));
                excel.setRefundNotes(entity.getRefundNotes());
                excel.setRebateno(entity.getRebateno());
                excel.setRebateExpressno(entity.getRebateExpressno());
                excelList.add(excel);
        }
        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
}
