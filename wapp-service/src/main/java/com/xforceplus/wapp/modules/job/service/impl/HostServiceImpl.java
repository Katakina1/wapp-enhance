package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.job.dao.HostTaskDao;
import com.xforceplus.wapp.modules.job.entity.HostTaskEntity;
import com.xforceplus.wapp.modules.job.service.HostService;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.google.common.collect.Lists;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class HostServiceImpl implements HostService {

    @Value("${apiWrite.url}")
    private String apiWrite_url;
    @Autowired
    private HostTaskDao hostTaskDao;
    @Override
    public PagedQueryResult<HostTaskEntity> getTaskList(Map<String,Object> params) {
        final PagedQueryResult<HostTaskEntity> taskEntityPagedQueryResult=new PagedQueryResult<>();
        final Integer count=hostTaskDao.getJobCount(params);
        List<HostTaskEntity> list= Lists.newArrayList();
        if(count>0){
            list=hostTaskDao.getJobList(params);
        }
        taskEntityPagedQueryResult.setResults(list);
        taskEntityPagedQueryResult.setTotalCount(count);
        return taskEntityPagedQueryResult;
    }

    @Override
    public PagedQueryResult<MatchEntity> getMatchEntityLists(Map<String, Object> params) {
        final  PagedQueryResult<MatchEntity> matchEntityPagedQueryResult=new PagedQueryResult<>();
        final  Integer count=hostTaskDao.getMatchEntityCount(params);
        List<MatchEntity> matchEntities=Lists.newArrayList();
        if(count>0){
            matchEntities=hostTaskDao.getMatchEntityLists(params);

        }
        matchEntityPagedQueryResult.setResults(matchEntities);
        matchEntityPagedQueryResult.setTotalCount(count);
        return matchEntityPagedQueryResult;
    }

    @Override
    public String postApi(Long[] ids) {
        JSONArray idsString= JSONArray.fromObject(ids);
        String responseJson="";
        try {
            responseJson = HttpRequestUtils.httpPostApi(idsString.toString(),apiWrite_url,3600000);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson="写屏接口调用失败";
        }

        return responseJson==null?"写屏接口调用失败":responseJson;
    }

    @Override
    public List<ScreenExcelEntity> transformExcle(List<MatchEntity> list) {
        List<ScreenExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++){
            MatchEntity entity=list.get(i);
            ScreenExcelEntity matchExcelEntity=new ScreenExcelEntity();
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
            matchExcelEntity.setGfTaxNo( entity.getGfTaxNo());
            matchExcelEntity.setVenderid(  entity.getVenderid());
            matchExcelEntity.setVenderName(  entity.getVenderName());
            matchExcelEntity.setInvoiceAmount(  formatAmount(entity.getInvoiceAmount().toString()));
            matchExcelEntity.setInvoiceNum(  entity.getInvoiceNum()+"");
            matchExcelEntity.setPoAmount(  formatAmount(entity.getPoAmount().toString()));
            matchExcelEntity.setPoNum(  entity.getPoNum()+"");
            matchExcelEntity.setClaimAmount(  formatAmount(entity.getClaimAmount().toString()));
            matchExcelEntity.setClaimNum(  entity.getClaimNum()+"");
            matchExcelEntity.setMatchDate(  formatDate(entity.getMatchDate()));
            matchExcelEntity.setSettlementamount(  formatAmount(entity.getSettlementamount().toString()));
            matchExcelEntity.setErrDesc(entity.getMathingSource());
            matchExcelEntities.add(matchExcelEntity);

            List<InvoiceEntity> invoiceList= queryInvoiceList(entity.getMatchno());
            for (InvoiceEntity ie:invoiceList) {
                ScreenExcelEntity matchExcel=new ScreenExcelEntity();
                matchExcel.setDzVenderid(ie.getVenderid());
                matchExcel.setDzInvoiceNo(ie.getInvoiceNo());
                matchExcel.setDzInvoiceCode(ie.getInvoiceCode());
                matchExcel.setDzInvoiceAmount(formatAmount(ie.getInvoiceAmount().toString()));
                matchExcelEntities.add(matchExcel);
            }
        }
        return matchExcelEntities;
    }
    @Override
    public List<InvoiceEntity> queryInvoiceList(String matchno){
        return hostTaskDao.queryInvoiceList(matchno);
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String getisDel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="未处理";
        }else if("1".equals(getisdel)){
            value="已退票";
        }else if("2".equals(getisdel)){
            value="已处理";
        }else{
            value="未处理";
        }
        return value;
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
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String walmartStatus(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            value="—— ——";
        }else{
            if("0".equals(hostStatus)){
                value="未处理";
            }else if("1".equals(hostStatus)){
                value="未处理";
            }else if("10".equals(hostStatus)){
                value="已处理";
            }else if("13".equals(hostStatus)){
                value="已删除";
            }else if("14".equals(hostStatus)){
                value="待付款";
            }else if("11".equals(hostStatus)){
                value="已匹配";
            }else if("12".equals(hostStatus)){
                value="已匹配";
            }else if("15".equals(hostStatus)){
                value="已付款";
            }else if("19".equals(hostStatus)){
                value="已付款";
            }else if("9".equals(hostStatus)){
                value="待付款";
            }else if("99".equals(hostStatus)){
                value="已付款";
            }else if("999".equals(hostStatus)){
                value="已付款";
            }else{
                value="未处理";
            }
        }
        return value;
    }
}
