package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InvoiceProcessingStatusReportDao;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.MatchExcelEntity;
import com.xforceplus.wapp.modules.report.service.InvoiceProcessingStatusReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceProcessingStatusReportServiceImpl implements InvoiceProcessingStatusReportService {
    @Autowired
    private InvoiceProcessingStatusReportDao invoiceProcessingStatusReportDao;
    @Override
    public List<MatchEntity> matchlist(Map<String, Object> map){
        return invoiceProcessingStatusReportDao.matchlist(map);
    }
    @Override
    public Integer matchlistCount(Map<String, Object> map){
        return invoiceProcessingStatusReportDao.matchlistCount(map);
    }
    @Override
    public List<GfOptionEntity> searchGf(){
        List<GfOptionEntity> gfOptionEntities=invoiceProcessingStatusReportDao.searchGf();
        for (int i=gfOptionEntities.size()-1;i>=0;i--){
            if(gfOptionEntities.get(i).getValue().equals("0")){
                gfOptionEntities.remove(i);
            }
        }
        return gfOptionEntities;
    }

    @Override
    public List<MatchExcelEntity> transformExcle(List<MatchEntity> list) {
        List<MatchExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++){
            MatchEntity entity=list.get(i);
            MatchExcelEntity matchExcelEntity=new MatchExcelEntity();


            //序号
            matchExcelEntity.setRownumber(String.valueOf(i+1));
            //购方名称
            matchExcelEntity.setGfName(  entity.getGfName());
            //公司代码
            matchExcelEntity.setCompanyCode(  entity.getCompanyCode());
            //供应商名称
            matchExcelEntity.setVendername(  entity.getVendername());
            //供应商编码
            matchExcelEntity.setVenderId(  entity.getVenderId());
            //发票金额
            matchExcelEntity.setInvoiceAmount(  entity.getInvoiceAmount().toString());
            //发票数量
            matchExcelEntity.setInvoiceNum(  entity.getInvoiceNum().toString());
            //PO金额
            matchExcelEntity.setPoAmount(  entity.getPoAmount().toString());
            //PO数量
            matchExcelEntity.setPoNum(  entity.getPoNum().toString());
            //索赔金额
            matchExcelEntity.setClaimAmount(  entity.getClaimAmount().toString());
            //索赔单数量
            matchExcelEntity.setClaimNum( entity.getClaimNum().toString());
            //匹配日期
            matchExcelEntity.setMatchDate(  formatDate(entity.getMatchDate()));
            //结算金额
            matchExcelEntity.setSettlementAmount(  entity.getSettlementAmount().toString());
            //HOST状态
            matchExcelEntity.setHostStatus(  formateHostStatusType(entity.getHostStatus()));

            matchExcelEntities.add(matchExcelEntity);


        }
        return matchExcelEntities;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateHostStatusType(String hostStatus){
        String value="未处理";
        if("0".equals(hostStatus)){
            value="未处理";
        }else if("1".equals(hostStatus)){
            value="已处理";
        }else if("13".equals(hostStatus)){
            value="已删除";
        }else if("14".equals(hostStatus)){
            value="invoice reactived";
        }else if("10".equals(hostStatus)){
            value="未处理";
        }else if("12".equals(hostStatus)){
            value="已匹配";
        }else if("11".equals(hostStatus)){
            value="已匹配";
        }else if("19".equals(hostStatus)){
            value="已付款";
        }else if("9".equals(hostStatus)){
            value="待付款";
        }else if("99".equals(hostStatus)){
            value="已付款";
        }else if("999".equals(hostStatus)){
            value="已付款";
        }
        return value;
    }
}
