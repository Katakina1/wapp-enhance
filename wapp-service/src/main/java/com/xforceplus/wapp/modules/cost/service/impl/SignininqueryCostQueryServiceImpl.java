package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.cost.dao.SignininqueryCostQueryDao;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.service.SignininqueryCostQueryService;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class SignininqueryCostQueryServiceImpl implements SignininqueryCostQueryService {

    @Autowired
    private SignininqueryCostQueryDao signininqueryCostQueryDao;

    @Autowired
    private CostAppliction costAppliction;


    @Override
    public List<SettlementEntity> queryList(Map<String, Object> map) {
        return signininqueryCostQueryDao.queryList(map);
    }

    @Override
    public List<SettlementExcelEntity> queryAllList(Map<String, Object> map) {
        List<SettlementEntity> list = signininqueryCostQueryDao.queryList(map);
        List<SettlementExcelEntity> excelList = new LinkedList();
        SettlementExcelEntity excel = null;
        int index = 1;
        for(SettlementEntity entity:list){
            excel= new SettlementExcelEntity();
            excel.setApproverEmail(entity.getApproverEmail());
            excel.setCostNo(entity.getCostNo());
            excel.setCreateDate(formatDate(entity.getCreateDate()));
            excel.setEpsNo(entity.getEpsNo());
            excel.setRejectReason(entity.getRejectReason());
            excel.setRownumber(""+index++);
            excel.setScanDate(formatDate(entity.getScanDate()));
            excel.setScanStatus(getisDel(entity.getScanStatus()));
            excel.setSettlementAmount(formatAmount(entity.getSettlementAmount().toString()));
            excel.setVenderId(entity.getVenderId());
            excel.setVenderName(entity.getVenderName());
            excel.setPayModel(getisModel(entity.getPayModel()));

            excelList.add(excel);
        }
        return excelList;
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return signininqueryCostQueryDao.queryCount(map);
    }



    @Override
    public List<RecordInvoiceEntity> queryDetail(String costNo) {
        List<RecordInvoiceEntity> invoiceList = signininqueryCostQueryDao.getInvoice(costNo);
//        for(RecordInvoiceEntity invoice : invoiceList){
//            List<RateEntity> rateList = signininqueryCostQueryDao.getRate(costNo, invoice.getInvoiceCode(), invoice.getInvoiceNo());
//            for(RateEntity rate : rateList){
//                List<CostEntity> costList = signininqueryCostQueryDao.getCost(rate.getId());
//                rate.setCostTableData(costList);
//            }
//            //invoice.setRateTableData(rateList);
//        }
        return invoiceList;
    }

    @Override
    public List<ContrastEntity> queryDetails(String costNo) {
        List<ContrastEntity> invoiceList = signininqueryCostQueryDao.getInvoices(costNo);
        return invoiceList;
    }

    @Override
    public List<SettlementFileEntity> queryFileDetail(String costNo) {
        return signininqueryCostQueryDao.getFile(costNo);
    }

    @Override
    public List<SelectionOptionEntity> getStatusOptions() {
        return signininqueryCostQueryDao.getStatusOptions();
    }


    @Override
    public Boolean deleteMsgById(String schemaLabel, String costNo, String instanceId, String epsNo, String refundReason, String refundCode, String belongsTo, String payModel) {
        signininqueryCostQueryDao.underWay(costNo);
        if("0".equals(payModel) || "3".equals(payModel)) {
           Boolean d = costAppliction.sendDelete(instanceId, costNo,refundReason);
            if(d){
                //删除扫描表数据
                Boolean a=signininqueryCostQueryDao.deleteInvice(schemaLabel,costNo,epsNo,refundReason,refundCode,belongsTo);
                Boolean b=signininqueryCostQueryDao.deleteInvices(schemaLabel,costNo);
                Boolean c=signininqueryCostQueryDao.deleteMsgById(schemaLabel,costNo,refundReason);
                Boolean e =signininqueryCostQueryDao.deleteMsgByIds(schemaLabel,costNo);
                return Boolean.TRUE;
            }
       }
       if("1".equals(payModel)){
           //删除扫描表数据
           Boolean a=signininqueryCostQueryDao.deleteInvice(schemaLabel,costNo,epsNo,refundReason,refundCode,belongsTo);
           Boolean b=signininqueryCostQueryDao.deleteInvices(schemaLabel,costNo);
           Boolean c=signininqueryCostQueryDao.deleteMsgById(schemaLabel,costNo,refundReason);
           Boolean e =signininqueryCostQueryDao.deleteMsgByIds(schemaLabel,costNo);
           return Boolean.TRUE;
       }
//        //保存备份数据
//        RecordInvoiceEntity r=signatureProcessingDao.selectInvoice(schemaLabel,uuid);
//        //查询备份表是否存在该uuid的发票数据
//        Long copyDataId=signatureProcessingDao.getCopyId(schemaLabel,uuid);
//        if(copyDataId!=null){
//            //存在uuid 数据 更新备份表
//            signatureProcessingDao.updateCopyData(schemaLabel,r,uuid,user);
//        }else{
//            //不存在uuid数据 插入备份数据
//            signatureProcessingDao.saveCopyData(schemaLabel,r,user);
//        }if


        return Boolean.FALSE;
    }

    /**
     * 处理预付款退单 回滚金额逻辑
     * @param costNo
     * @param epsNo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebackYf(String costNo, String epsNo) {
        //查询出费用单及明细
        List<MatchRebackEntity> list = signininqueryCostQueryDao.searchDetail(costNo);
        Map map = new HashMap();
        String totalAmount =  signininqueryCostQueryDao.findMatch(epsNo);
        BigDecimal total = new BigDecimal(totalAmount);
        for(MatchRebackEntity entity : list){
            signininqueryCostQueryDao.updateAmountByUuid(entity.getInvoiceCode()+entity.getInvoiceNo(),entity.getInvoiceAmount());
           String amount = signininqueryCostQueryDao.searchMatchDetailAmount(entity.getMatchDetailId());
           BigDecimal bd = new BigDecimal(amount).subtract(entity.getCostAmount());
            total = total.subtract(entity.getCostAmount());
           signininqueryCostQueryDao.updateAmountById(entity.getMatchDetailId(),bd);
        }
        signininqueryCostQueryDao.updateMatchAmount(epsNo,total);
//        Set set = map.keySet();
//        Iterator<String> iter = set.iterator();
//        if(iter.hasNext()){
//            String uuid = iter.next();
//            BigDecimal invoiceAmount = (BigDecimal) map.get(uuid);
//            signininqueryCostQueryDao.updateAmountByUuid(uuid,invoiceAmount);
//        }
    }

    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
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

}
