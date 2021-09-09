package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.dao.CostScanConfirmDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.ScanConfirmDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.importTemplate.ScanConfirmImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.CostScanConfirmService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanConfirmService;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceCostQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class CostScanConfirmServiceImpl implements CostScanConfirmService {

    @Autowired
    private CostScanConfirmDao costScanConfirmDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map) {
        return costScanConfirmDao.queryList(map);
    }

    @Override
    public int queryCount(Map<String, Object> map) {
        return costScanConfirmDao.queryCount(map);
    }

    @Override
    public List<SelectionOptionEntity> getJV(String taxNo) {
        return costScanConfirmDao.getJV(taxNo);
    }

    @Override
    public List<SelectionOptionEntity> getVender() {
        return costScanConfirmDao.getVender();
    }

    @Override
    @Transactional
    public boolean submit(ConfirmInvoiceEntity entity) {
        entity.setCompanyCode(costScanConfirmDao.getCompanyCode(entity.getJvcode()));
        int res1 = costScanConfirmDao.updateRecordInvoice(entity);
        int res2 = costScanConfirmDao.updateInvoice(entity);
        return (res1>0 && res2 >0);
    }

    @Override
    public R submitBatch(MultipartFile file, Long userId) {
        //进入解析excel方法
        final ScanConfirmImportExcel importExcel= new ScanConfirmImportExcel(file);
        int totalCount = 0;
        int okCount = 0;
        int errorCount = 0;
        List<String> errorList = newArrayList();
        StringBuffer sb = new StringBuffer();
        try {
            //读取excel
            final List<ConfirmInvoiceEntity> list = importExcel.analysisExcel();
            for(ConfirmInvoiceEntity entity : list){
                if(entity.getConfirmReason().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写旧发票.";
                    errorList.add(error);
                    continue;
                }
                if(entity.getJvcode().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写JVCODE.";
                    errorList.add(error);
                    continue;
                }
                if(entity.getVenderid().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写供应商号.";
                    errorList.add(error);
                    continue;
                }
                //校验jvcode
                if(costScanConfirmDao.jvOk(entity.getJvcode(), entity.getGfTaxNo())==0){
                    String error = "序号"+entity.getId()+": JV填写错误或与发票抬头不一致.";
                    errorList.add(error);
                    continue;
                }
                //校验供应商
                if(costScanConfirmDao.venderOk(entity.getVenderid())==0){
                    String error = "序号"+entity.getId()+": 供应商不存在或被冻结.";
                    errorList.add(error);
                    continue;
                }
                entity.setConfirmUserId(userId);
                //保存
                if(submit(entity)){
                    okCount++;
                }else{
                    String error = "序号"+entity.getId()+": 确认失败,请检查发票信息是否被篡改.";
                    errorList.add(error);
                    continue;
                }
            }
            totalCount = list.size();
            errorCount = errorList.size();
            sb.append("共计"+totalCount+"条数据,其中确认成功"+okCount+"条,失败"+errorCount+"条.");
            if(errorCount>0){
                sb.append("原因如下:");
                for(String e : errorList){
                    sb.append("<br/>"+e);
                }
            }
        } catch (Exception e) {
            return R.error("导入过程出错,请检查填写内容!");
        }
        return R.ok(sb.toString());
    }

    @Override
    public List<ComprehensiveInvoiceCostQueryExcelEntity> queryExcelList(Map<String, Object> map) {

        List<ComprehensiveInvoiceQueryEntity> list = costScanConfirmDao.queryList(map);
        List<ComprehensiveInvoiceCostQueryExcelEntity> excelList = new LinkedList();
        ComprehensiveInvoiceCostQueryExcelEntity excel = null;
        int index = 1;
        for (ComprehensiveInvoiceQueryEntity entity : list) {
            excel = new ComprehensiveInvoiceCostQueryExcelEntity();
            excel.setConfirmReason(entity.getConfirmReason());
            excel.setDeductibleTax(CommonUtil.formatMoney(entity.getDeductibleTax()));
            excel.setGfName(entity.getGfName());
            excel.setGfTaxNo(entity.getGfTaxNo());
            excel.setDeductibleTaxRate(CommonUtil.formatMoney(entity.getDeductibleTaxRate()));
            excel.setInvoiceAmount(CommonUtil.formatMoney(entity.getInvoiceAmount()));
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceDate(formatDateString(entity.getInvoiceDate()));
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setJvCode(entity.getJvCode());
            excel.setRownumber("" + index++);
            excel.setTaxAmount(CommonUtil.formatMoney(entity.getTaxAmount()));
            excel.setTotalAmount(CommonUtil.formatMoney(entity.getTotalAmount()));
            excel.setVenderId(entity.getVenderId());
            excel.setXfName(entity.getXfName());
            excel.setXfTaxNo(entity.getXfTaxNo());
            excelList.add(excel);
        }
        return excelList;

    }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

}
