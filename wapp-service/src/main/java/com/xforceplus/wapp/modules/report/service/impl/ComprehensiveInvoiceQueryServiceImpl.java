package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.dao.AuthenticationQueryDao;
import com.xforceplus.wapp.modules.report.dao.ComprehensiveInvoiceQueryDao;
import com.xforceplus.wapp.modules.report.entity.*;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ComprehensiveInvoiceQueryServiceImpl implements ComprehensiveInvoiceQueryService {

    @Autowired
    private ComprehensiveInvoiceQueryDao comprehensiveInvoiceQueryDao;
    @Autowired
    private  AuthenticationQueryDao authenticationQueryDao;
    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = comprehensiveInvoiceQueryDao.queryList(schemaLabel, map);
        for (ComprehensiveInvoiceQueryEntity entity:comprehensiveInvoiceQueryEntities){
            if(entity.getTpStatus()!=null && entity.getTpStatus().equals("2")){
                //获取退票时间
                String tpDate =authenticationQueryDao.getTpDateByUuid(entity.getInvoiceCode()+entity.getInvoiceNo());
                if(!StringUtils.isEmpty(tpDate)){
                    entity.setTpDate(tpDate.substring(0,10));
                }

            }
        }

        return comprehensiveInvoiceQueryEntities;
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryListAll(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryListSL(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryListSL(schemaLabel,map);
    }

    @Override
    public List<OptionEntity> searchGf(String schemaLabel,Long userId) {
        return comprehensiveInvoiceQueryDao.searchGf(schemaLabel,userId);
    }

    @Override
    public List<String> searchXf(String schemaLabel,Map<String, Object> map) {
        return comprehensiveInvoiceQueryDao.searchXf(schemaLabel,map);
    }
    @Override
    public List<OptionEntity> searchflowType(){
        return comprehensiveInvoiceQueryDao.searchflowType();
    }


    @Override
    public List<ComprehensiveInvoiceQueryExcelEntity> queryExcelListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<ComprehensiveInvoiceQueryEntity> list =  comprehensiveInvoiceQueryDao.queryList(schemaLabel,map);
        List<ComprehensiveInvoiceQueryExcelEntity> excelList = new LinkedList<ComprehensiveInvoiceQueryExcelEntity>();
        ComprehensiveInvoiceQueryExcelEntity excel = null;
        int index=1;
        for(ComprehensiveInvoiceQueryEntity entity:list){
            excel = new ComprehensiveInvoiceQueryExcelEntity();
            excel.setRownumber(entity.getRownumber());
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceDate(formatDateString(entity.getInvoiceDate()));
            excel.setGfTaxNo(entity.getGfTaxNo());
            excel.setGfName(entity.getGfName());
            excel.setXfTaxNo(entity.getXfTaxNo());
            excel.setXfName(entity.getXfName());
            excel.setInvoiceAmount(CommonUtil.formatMoney(entity.getInvoiceAmount()));
            excel.setTaxAmount(CommonUtil.formatMoney(entity.getTaxAmount()));
            excel.setInvoiceStatus(formatInvoiceStatus(entity.getInvoiceStatus()));
            excel.setDxhyMatchStatus(formatdxhyMatchStatus(entity.getDxhyMatchStatus()));
            excel.setMatchDate(formatDate(entity.getMatchDate()));
            excel.setHostStatus(formatHostStatus(entity.getHostStatus(),entity.getTpStatus()));
            excel.setScanningSeriano(entity.getScanningSeriano());
            excel.setBbindingno(entity.getBbindingno());
            excel.setRzhBelongDate(entity.getRzhBelongDate());
            excel.setPackingno(entity.getPackingno());
            excel.setVenderId(entity.getVenderId());
            excel.setCertificateNo(entity.getCertificateNo());
            excel.setJvCode(entity.getJvCode());
            excel.setCompanyCode(entity.getCompanyCode());
            excel.setTotalAmount(CommonUtil.formatMoney(entity.getTotalAmount()));
            excel.setQsStatus(formatQsStatus(entity.getQsStatus()));
            excel.setQsType(formatQsType(entity.getQsType()));
            excel.setQsDate(formatDate(entity.getQsDate()));
            excel.setRzhYesorno(formatRzhStatus(entity.getRzhYesorno()));
            excel.setRzhDate(formatDate(entity.getRzhDate()));
            excel.setAuthStatus(formatAuthStatus(entity.getAuthStatus()));
            excel.setFlowType(formatFlowType(entity.getFlowType()));
            if(StringUtils.isNotEmpty(entity.getPaymentDate())){
                excel.setPaymentDate(entity.getPaymentDate().substring(0,10));
            }else{
                excel.setPaymentDate("");
            }
            if(StringUtils.isNotEmpty(entity.getBorrowDate())){
                excel.setBorrowDate(entity.getBorrowDate().substring(0,10));
            }else{
                excel.setBorrowDate("");
            }
            if(StringUtils.isNotEmpty(entity.getBorrowReturnDate())){
                excel.setBorrowReturnDate(entity.getBorrowReturnDate().substring(0,10));
            }else{
                excel.setBorrowReturnDate("");
            }
            excel.setBorrowDept(entity.getBorrowDept());
            excel.setBorrowReason(entity.getBorrowReason());
            excel.setBorrowReturnUser(entity.getBorrowReturnUser());
            excel.setBorrowUser(entity.getBorrowUser());
            excel.setPackingAddress(entity.getPackingAddress());
            excelList.add(excel);
        }
       /* excel = new ComprehensiveInvoiceQueryExcelEntity();
        excel.setRownumber("合计：");
        excel.setInvoiceAmount(map.get("totalAmount").toString());
        excel.setTaxAmount(map.get("totalTax").toString());
        if(map.get("taxAmount")!=null) {
            excel.setTotalAmount(map.get("taxAmount").toString());
        }
        excelList.add(excel);*/
        return excelList;
    }


    @Override
    public List<InvoiceQueryExcelEntity> queryInvoiceExcelListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<ComprehensiveInvoiceQueryEntity> list =  comprehensiveInvoiceQueryDao.queryList(schemaLabel,map);
        List<InvoiceQueryExcelEntity> excelList = new LinkedList<>();
        InvoiceQueryExcelEntity excel = null;
        int index=1;
        for(ComprehensiveInvoiceQueryEntity entity:list){
            excel = new InvoiceQueryExcelEntity();
            excel.setRownumber(""+index++);
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceDate(formatDateString(entity.getInvoiceDate()));
            excel.setGfTaxNo(entity.getGfTaxNo());
            excel.setGfName(entity.getGfName());
            excel.setXfTaxNo(entity.getXfTaxNo());
            excel.setXfName(entity.getXfName());
            excel.setInvoiceAmount(CommonUtil.formatMoney(entity.getInvoiceAmount()));
            excel.setTaxAmount(CommonUtil.formatMoney(entity.getTaxAmount()));
            excel.setInvoiceStatus(formatInvoiceStatus(entity.getInvoiceStatus()));
            excel.setDxhyMatchStatus(formatdxhyMatchStatus(entity.getDxhyMatchStatus()));
            excel.setMatchDate(formatDate(entity.getMatchDate()));
            excel.setHostStatus(formatHostStatus(entity.getHostStatus(),entity.getTpStatus()));
            excel.setScanningSeriano(entity.getScanningSeriano());
            excel.setBbindingno(entity.getBbindingno());
            excel.setRzhBelongDate(entity.getRzhBelongDate());
            excel.setPackingno(entity.getPackingno());
            excel.setTotalAmount(CommonUtil.formatMoney(entity.getTotalAmount()));
            excelList.add(excel);
        }
        excel = new InvoiceQueryExcelEntity();
//        excel.setRownumber("合计：");
//        excel.setInvoiceAmount(map.get("totalAmount").toString());
//        excel.setTaxAmount(map.get("totalTax").toString());
//        if(map.get("taxAmount")!=null) {
//            excel.setTotalAmount(map.get("taxAmount").toString());
//        }
        excelList.add(excel);
        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatInvoiceStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "正常" :
                        "1".equals(status) ? "失控" :
                                "2".equals(status) ? "作废" :
                                        "3".equals(status) ? "红冲" :
                                                "4".equals(status) ? "异常" : "";
    }

    private String formatQsStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未签收" :
                        "1".equals(status) ? "已签收" : "";
    }
    private String formatdxhyMatchStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未匹配" :
                        "1".equals(status) ? "预匹配" :
                                "2".equals(status) ? "部分匹配":
                                        "3".equals(status) ? "完全匹配":
                                                "4".equals(status) ? "差异匹配":
                                                        "5".equals(status) ? "匹配失败":
                                                                "6".equals(status) ? "取消匹配":"";
    }

    private String formatHostStatus(String status,String tpStatus){
        if(tpStatus!=null&tpStatus!=""){
            if(tpStatus.equals("2")){
                return "已退票";
            }
        }
        if(status == null||"".equals(status)){
            return "未处理";
        }

        return null==status ? "" :
                "0".equals(status) ? "未处理" :
                        "1".equals(status) ? "未处理" :
                                "10".equals(status) ? "未处理":
                                        "13".equals(status) ? "完全匹配":
                                                "14".equals(status) ? "待付款":
                                                        "11".equals(status) ? "已处理":
                                                                "12".equals(status) ? "已处理":
                                                                    "8".equals(status) ? "已冻结":
                                                                        "15".equals(status) ? "已付款":
                                                                                "19".equals(status) ? "已付款":
                                                                                     "99".equals(status) ? "已付款":
                                                                                            "999".equals(status) ? "已付款":
                                                                                                "9".equals(status) ? "待付款":"";
    }

    private String formatQsType(String type){
        return null==type ? "" :
                "0".equals(type) ? "扫码签收" :
                        "1".equals(type) ? "扫描仪签收" :
                                "2".equals(type) ? "app签收" :
                                        "3".equals(type) ? "导入签收" :
                                                "4".equals(type) ? "手工签收" :
                                                        "5".equals(type) ? "pdf上传签收" : "";
    }

    private String formatRzhStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未认证" :
                        "1".equals(status) ? "已认证" : "";
    }

    private String formatAuthStatus(String type){
        return null==type ? "" :
                "0".equals(type) ? "未认证" :
                        "1".equals(type) ? "已勾选未确认" :
                                "2".equals(type) ? "已确认" :
                                        "3".equals(type) ? "已发送认证" :
                                                "4".equals(type) ? "认证成功" :
                                                        "5".equals(type) ? " 认证失败" : "";
    }

    private String formatFlowType(String type){
        return null==type ? "" :
                "1".equals(type) ? "商品" :
                        "2".equals(type) ? "费用" :
                                "3".equals(type) ? "外部红票" :
                                        "4".equals(type) ? "内部红票" :
                                                "5".equals(type) ? " 供应商红票" :
                                                        "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }
}
