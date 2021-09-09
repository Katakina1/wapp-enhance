package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostQueryDao;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.service.CostQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CostQueryServiceImpl implements CostQueryService {

    @Autowired
    private CostQueryDao costQueryDao;

    @Override
    public List<SettlementEntity> queryList(Map<String, Object> map) {
        return costQueryDao.queryList(map);
    }

    @Override
    public List<SettlementEntity> queryAllList(Map<String, Object> map) {
        return costQueryDao.queryAllList(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return costQueryDao.queryCount(map);
    }

    @Override
    public List<RecordInvoiceEntity> queryDetail(String costNo) {
        List<RecordInvoiceEntity> invoiceList = costQueryDao.getInvoice(costNo);
        for(RecordInvoiceEntity invoice : invoiceList){
            List<RateEntity> rateList = costQueryDao.getRate(costNo, invoice.getInvoiceCode(), invoice.getInvoiceNo());
            for(RateEntity rate : rateList){
                List<CostEntity> costList = costQueryDao.getCost(rate.getId());
                rate.setCostTableData(costList);
            }
            invoice.setRateTableData(rateList);
        }
        return invoiceList;
    }

    @Override
    public List<SettlementFileEntity> queryFileDetail(String costNo) {
        return costQueryDao.getFile(costNo);
    }

    @Override
    public List<SelectionOptionEntity> getStatusOptions() {
        return costQueryDao.getStatusOptions();
    }

    @Override
    public int updateUser(String rzuserId,String lzuserId){
        return costQueryDao.updateUser(rzuserId,lzuserId);
    }

    @Override
    public List<SettlementExcelEntity> transformExcle(List<SettlementEntity> list) {
        List<SettlementExcelEntity> list2=new ArrayList<>();
        for(int i = 0 ; i < list.size() ; i++) {
            SettlementEntity entity=list.get(i);
            SettlementExcelEntity SettlementExcelEntity=new SettlementExcelEntity();

//序号
            SettlementExcelEntity.setRownumber(  String.valueOf(i+1));
            //费用号
            SettlementExcelEntity.setCostNo(entity.getCostNo());
            //审批人邮箱
            SettlementExcelEntity.setApproverEmail(entity.getApproverEmail());
            //供应商号
            SettlementExcelEntity.setVenderId( entity.getVenderId());
            //供应商名称
            SettlementExcelEntity.setVenderName( entity.getVenderName());
            //费用金额
            SettlementExcelEntity.setSettlementAmount( formatAmount(entity.getSettlementAmount()== null ? "" : entity.getSettlementAmount().toString()));
            //EPS_NO
            SettlementExcelEntity.setEpsNo( entity.getEpsNo());
            //发票号码
            SettlementExcelEntity.setInvoiceNo( entity.getInvoiceNo());
            //价税合计
            SettlementExcelEntity.setTotalAmount( formatAmount(entity.getTotalAmount()== null ? "" : entity.getTotalAmount().toString()));
            //申请日期
            SettlementExcelEntity.setCreateDate( formatDate(entity.getCreateDate()));
            //驳回理由
            SettlementExcelEntity.setRejectReason( entity.getRejectReason());
            //数据来源
            SettlementExcelEntity.setPayModel(getisModel(entity.getPayModel()));
            //沃尔玛状态
            SettlementExcelEntity.setWalmartStatus(getWalmartStatus(entity.getWalmartStatus()));
            //沃尔玛更新时间
            SettlementExcelEntity.setWalmartDate(formatDate(entity.getWalmartDate()));
            list2.add(SettlementExcelEntity);
        }

        return list2;
    }


    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        return amount.equals("") ? "" : amount.substring(0, amount.length()-2);
    }
    private String getisModel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="wapp";
        }else if("1".equals(getisdel)){
            value="预付款";
        }else if("3".equals(getisdel)){
            value="bpms";
        }
        return value;
    }
    private String getisDel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="未匹配";
        }else if("1".equals(getisdel)){
            value="匹配成功";
        }else if("2".equals(getisdel)){
            value="匹配失败";
        }else{
            value="未匹配";
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

    private String getWalmartStatus(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="已提交";
        }else if("1".equals(getisdel)){
            value="待收票";
        } else if("2".equals(getisdel)){
            value="审批不通过";
        }else if("3".equals(getisdel)){
            value="沃尔玛审批中";
        }else if("4".equals(getisdel)){
            value="待付款";
        }else if("5".equals(getisdel)){
            value="已付款";
        }else if("6".equals(getisdel)){
            value="已冲账";
        }else if("7".equals(getisdel)){
            value="已退票";
        }
        return value;
    }
}
