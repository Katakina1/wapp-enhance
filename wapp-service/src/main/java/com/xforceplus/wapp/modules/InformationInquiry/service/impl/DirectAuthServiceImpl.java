package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.dao.DirectAuthDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.importTemplate.DirectAuthImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.DirectAuthService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.DirectAuthQueryExcelEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DirectAuthServiceImpl implements DirectAuthService {

    @Autowired
    private DirectAuthDao scanConfirmDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map) {
        return scanConfirmDao.queryList(map);
    }

    @Override
    public int queryCount(Map<String, Object> map) {
        return scanConfirmDao.queryCount(map);
    }

    @Override
    @Transactional
    public R submit(List<ConfirmInvoiceEntity> list) {
        for(ConfirmInvoiceEntity entity : list) {
            boolean res = submitOne(entity);
            if(!res){
                return R.error();
            }
        }
        return R.ok();
    }

    private boolean submitOne(ConfirmInvoiceEntity entity){
        int res1 = scanConfirmDao.updateRecordInvoice(entity);
        int res2 = scanConfirmDao.updateInvoice(entity);
        return res1>0 && res2>0;
    }

    @Override
    public R submitBatch(MultipartFile file, Long userId) {
        //进入解析excel方法
        final DirectAuthImportExcel importExcel= new DirectAuthImportExcel(file);
        int totalCount = 0;
        int okCount = 0;
        StringBuffer sb = new StringBuffer();
        try {
            //读取excel
            final List<ConfirmInvoiceEntity> list = importExcel.analysisExcel();
            for(ConfirmInvoiceEntity entity : list){
                if("Y".equalsIgnoreCase(entity.getConfirmReason())) {
                    entity.setConfirmUserId(userId);
                    //保存
                    if (submitOne(entity)) {
                        okCount++;
                    }
                }
            }
            totalCount = list.size();
            sb.append("共计"+totalCount+"条数据,其中确认成功"+okCount+"条.");
        } catch (Exception e) {
            return R.error("导入过程出错,请检查填写内容!");
        }
        return R.ok(sb.toString());
    }
    @Override
    public List<DirectAuthQueryExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> list){
        List<DirectAuthQueryExcelEntity> excelList = new LinkedList<>();
        DirectAuthQueryExcelEntity excel = null;
        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
        for(ComprehensiveInvoiceQueryEntity claimEntity : list){
            excel = new DirectAuthQueryExcelEntity();  //实体转换类型，便于导出Excel
            excel.setRownumber(claimEntity.getRownumber());
            excel.setInvoiceCode(claimEntity.getInvoiceCode());
            excel.setInvoiceNo(claimEntity.getInvoiceNo());
            try{
                excel.setInvoiceDate(formatDate(s.parse(claimEntity.getInvoiceDate())));
            }catch (Exception e){
            }
            excel.setGfTaxNo(claimEntity.getGfTaxNo());
            excel.setGfName(claimEntity.getGfName());
            excel.setXfName(claimEntity.getXfName());
            excel.setXfTaxNo(claimEntity.getXfTaxNo());
            excel.setInvoiceAmount(formatAmount(claimEntity.getInvoiceAmount().toString()));
            excel.setTaxAmount(formatAmount(claimEntity.getTaxAmount().toString()));
            try{
            excel.setTotalAmount(formatAmount(claimEntity.getTotalAmount().toString()));
            }catch (Exception e){
            }
            excel.setVenderId(claimEntity.getVenderId());
            excel.setJvCode(claimEntity.getJvCode());
            excel.setCompanyCode(claimEntity.getCompanyCode());
            excel.setQsDate(formatDate(claimEntity.getQsDate()));
            excelList.add(excel);
        }
//        excel.setRownumber("合计：");
//        excel.setClaimAmount(map.get("claimAmount").toString());
        //excelList.add(excel);
        return excelList;
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
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
}

