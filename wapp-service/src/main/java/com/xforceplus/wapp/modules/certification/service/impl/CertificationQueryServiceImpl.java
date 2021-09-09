package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.certification.dao.CertificationQueryDao;
import com.xforceplus.wapp.modules.certification.service.CertificationQueryService;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionResultExcelInfo;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 查询认证业务层实现
 * @author Colin.hu
 * @date 4/13/2018
 */
@Service
public class CertificationQueryServiceImpl implements CertificationQueryService {

    private final CertificationQueryDao certificationQueryDao;

    @Autowired
    public CertificationQueryServiceImpl(CertificationQueryDao certificationQueryDao) {
        this.certificationQueryDao = certificationQueryDao;
    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        ReportStatisticsEntity result = certificationQueryDao.getCertificationListCount(map);

        //需要返回的集合
        List<InvoiceCollectionInfo> infoArrayList = newArrayList();
        if (result.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = certificationQueryDao.selectCertificationList(map);
            /*for (InvoiceCollectionInfo entity: infoArrayList) {
                //获取税码
                entity.setTaxCode(formatTaxCode(entity));
                entity.setServiceType(formatServiceType(entity));
            }*/
        }
        pagedQueryResult.setTotalCount(result.getTotalCount());
        pagedQueryResult.setTotalAmount(result.getTotalAmount());
        pagedQueryResult.setTotalTax(result.getTotalTax());
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    

    @Override
    public List<InvoiceCollectionExcelInfo> queryCertificationExport(Map<String, Object> map) {
       List<InvoiceCollectionInfo> list =  certificationQueryDao.selectCertificationListExport(map);
       List<InvoiceCollectionExcelInfo> excelList= new LinkedList();
       int index = 1;
       InvoiceCollectionExcelInfo  excel = null;
        for(InvoiceCollectionInfo info:list){
            excel = new InvoiceCollectionExcelInfo();
            excel.setRownumber(""+index++);
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
            excel.setQsDate(formatDate(info.getQsDate()));
//            excel.setRownumber(info.getRownumber());
            excel.setQsStatus(formatQs(info.getQsStatus()));
            excel.setRzhBelongDate(info.getRzhBelongDate());
            excel.setRzhDate(formatDate(info.getRzhDate()));
            excel.setRzhYesorno(formatRzhYesOrNo(info.getRzhYesorno()));
            excel.setTaxAmount(info.getTaxAmount());
            excel.setVenderid(info.getVenderid());
            excel.setXfName(info.getXfName());
            excel.setXfTaxNo(info.getXfTaxNo());
//            excel.setRzhBackMsg(info.getRzhBackMsg());
            excelList.add(excel);
        }
        return excelList;
    }
    @Override
   public ReportStatisticsEntity getCertificationListCount(Map<String, Object> map){
        return certificationQueryDao.getCertificationListCount(map);
    }
    
    private String formatServiceType(InvoiceCollectionInfo entity) {
        //商品业务类型
        if(entity.getFlowType().equals("1")){
            return  "商品付款类";
        }

        //费用 业务类型
        if(entity.getFlowType().equals("2")){
            String uuid=entity.getInvoiceCode()+entity.getInvoiceNo();
            List<OptionEntity> costTaxCode = certificationQueryDao.getCostTaxCode(uuid);
            if(costTaxCode.size()>0){
                String costRate =  costTaxCode.get(0).getValue();
                String costServiceType = certificationQueryDao.getCostServiceType(costRate);
                if(costServiceType.trim().length()>0){
                    return costServiceType;
                }else {
                    return "";
                }
            }
            //通过税率找业务类型
            return "";
        }
        return  "";
    }

    private  OptionEntity queryHostTaxRate(String value){
        return certificationQueryDao.queryHostTaxRate(value);
    }
    //税码获取
    private String formatTaxCode(InvoiceCollectionInfo entity) {
        String invoiceType = entity.getInvoiceType();
        //商品类 host税率
        if(entity.getFlowType().equals("1")){
            if (StringUtils.isEmpty(entity.getTaxRate())){
                return "";
            }
            String hostTaxRate = entity.getTaxRate().substring(0,entity.getTaxRate().indexOf('.'));
            if(hostTaxRate.isEmpty()){
                return "";
            }
            String value = invoiceType+'_'+hostTaxRate;
            if(queryHostTaxRate(value)!=null){
                return  queryHostTaxRate(value).getLabel();
            }

            return "";
        }
        //费用 获取taxCode
        if(entity.getFlowType().equals("2")){
            String uuid=entity.getInvoiceCode()+entity.getInvoiceNo();
            List<OptionEntity> costTaxCode = this.certificationQueryDao.getCostTaxCode(uuid);
            if(costTaxCode.size()>0){
                    return  costTaxCode.get(0).getLabel();
            }
          return "";
        }
        return "";
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
    private String formatQs(String rzhYesorno) {
        if (StringUtils.isEmpty(rzhYesorno)){
            return "一 一";
        }
        if(rzhYesorno.equals("1")){
            return "已签收";
        }else if(rzhYesorno.equals("0")){
            return "未签收";
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
    private String formatQsType(String authStatus) {
        String authStatusName = "";
        if (StringUtils.isEmpty(authStatus)){
            return "一 一";
        }
        if("1".equals(authStatus)) {
            authStatusName = "扫描仪签收";
        } else  if("2".equals(authStatus)) {
            authStatusName = "app签收";
        } else  if("3".equals(authStatus)) {
            authStatusName = "导入签收";
        } else  if("4".equals(authStatus)) {
            authStatusName = "手工签收";
        } else if("0".equals(authStatus)) {
            authStatusName = "扫码签收";
        }else if("5".equals(authStatus)) {
            authStatusName = "pdf上传签收";
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
                                                        "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }
}
