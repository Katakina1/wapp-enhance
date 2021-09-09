package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.dao.AuthenticationResultQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationResultQueryService;
import com.xforceplus.wapp.modules.certification.dao.CertificationQueryDao;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionResultExcelInfo;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;

/**
 * Created by 1 on 2018/12/14 23:01
 */
@Service
public class AuthenticationResultQueryServiceImpl implements AuthenticationResultQueryService {

    private final AuthenticationResultQueryDao authenticationResultQueryDao;
    private final CertificationQueryDao certificationQueryDao;


    @Autowired
    public AuthenticationResultQueryServiceImpl(AuthenticationResultQueryDao authenticationResultQueryDao, CertificationQueryDao certificationQueryDao) {
        this.authenticationResultQueryDao = authenticationResultQueryDao;
        this.certificationQueryDao = certificationQueryDao;
    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        ReportStatisticsEntity result = authenticationResultQueryDao.getCertificationListCount(map);

            List<InvoiceCollectionInfo>    infoArrayList = authenticationResultQueryDao.selectCertificationList(map);
            pagedQueryResult.setTotalCount(result.getTotalCount());
            pagedQueryResult.setTotalAmount(result.getTotalAmount());
            pagedQueryResult.setTotalTax(result.getTotalTax());
            pagedQueryResult.setResults(infoArrayList);
            return pagedQueryResult;


    }


    @Override
    public List<InvoiceCollectionResultExcelInfo> queryCertificationForExport(Map<String, Object> map) {
        List<InvoiceCollectionInfo>    infoArrayList = authenticationResultQueryDao.selectCertificationList(map);
        List<InvoiceCollectionResultExcelInfo> excelList = new LinkedList();
        InvoiceCollectionResultExcelInfo excel = null;
        for(InvoiceCollectionInfo info : infoArrayList){
            excel = new InvoiceCollectionResultExcelInfo();
            excel.setAuthStatus(formatAuthStatus(info.getAuthStatus()));
            excel.setCompanyCode(info.getCompanyCode());
            excel.setConfirmUser(info.getConfirmUser());
            excel.setFlowType(formatFlowType(info.getFlowType()));
            excel.setGfName(info.getGfName());
            excel.setGfTaxNo(info.getGfTaxNo());
            excel.setInvoiceAmount(info.getInvoiceAmount());
            excel.setInvoiceCode(info.getInvoiceCode());
            excel.setInvoiceDate(formatDate(info.getInvoiceDate()));
            excel.setInvoiceNo(info.getInvoiceNo());
            excel.setInvoiceStatus(formatInvoiceStatus(info.getInvoiceStatus()));
            excel.setJvCode(info.getJvCode());
//            excel.setQsDate(formatDate(info.getQsDate()));
            excel.setRownumber(info.getRownumber());
//            excel.setQsStatus(info.getQsStatus());
            excel.setRzhBelongDate(info.getRzhBelongDate());
            excel.setRzhDate(formatDate(info.getRzhDate()));
            excel.setRzhYesorno(formatRzhYesOrNo(info.getRzhYesorno()));
            excel.setTaxAmount(info.getTaxAmount());
            excel.setVenderid(info.getVenderid());
            excel.setXfName(info.getXfName());
            excel.setXfTaxNo(info.getXfTaxNo());
            excel.setRzhBackMsg(info.getRzhBackMsg());
            excelList.add(excel);
        }
        return excelList;
    }
    @Override
    public  ReportStatisticsEntity getCertificationListCount(Map<String,Object> map){
     return   authenticationResultQueryDao.getCertificationListCount(map);
    }
    private String formatRzhYesOrNo(String rzhYesorno) {
        if (StringUtils.isEmpty(rzhYesorno)){
            return "一 一";
        }
        if(rzhYesorno.equals("1")){
            return "已认证";
        }else if(rzhYesorno.equals("0")){
            return "未认证";
        }
        return "一 一";
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatRzhType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "线上认证";
        } else  if("2".equals(authStatus)) {
            authStatusName = "线下认证";
        }
        return authStatusName;
    }

    private String formatAuthStatus(String authStatus) {
        if (StringUtils.isEmpty(authStatus)){
            return "";
        }

        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "已勾选";
        } else  if("2".equals(authStatus)) {
            authStatusName = "已确认";
        } else  if("3".equals(authStatus)) {
            authStatusName = "已发送认证";
        } else  if("4".equals(authStatus)) {
            authStatusName = "认证成功";
        } else if("5".equals(authStatus)) {
            authStatusName = "认证失败";
        }
        return authStatusName;
    }
    private String formatInvoiceStatus(String authStatus) {
        String authStatusName = "";
        if (StringUtils.isEmpty(authStatus)){
            return "";
        }
        if("1".equals(authStatus)) {
            authStatusName = "失控";
        } else  if("2".equals(authStatus)) {
            authStatusName = "作废";
        } else  if("3".equals(authStatus)) {
            authStatusName = "红冲";
        } else  if("4".equals(authStatus)) {
            authStatusName = "异常";
        } else if("0".equals(authStatus)) {
            authStatusName = "正常";
        }
        return authStatusName;
    }

    private String formatFlowType(String type) {
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
