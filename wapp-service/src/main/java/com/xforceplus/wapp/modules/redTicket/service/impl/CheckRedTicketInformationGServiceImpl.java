package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.redTicket.dao.CheckRedTicketInformationGDao;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.service.CheckRedTicketInformationGService;
import org.apache.catalina.LifecycleState;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CheckRedTicketInformationGServiceImpl implements CheckRedTicketInformationGService {
    private static final Logger LOGGER= getLogger(CheckRedTicketInformationGServiceImpl.class);
    private final CheckRedTicketInformationGDao checkRedTicketInformationGDao;
    @Autowired
    public CheckRedTicketInformationGServiceImpl(CheckRedTicketInformationGDao checkRedTicketInformationGDao){
        this.checkRedTicketInformationGDao=checkRedTicketInformationGDao;
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches=checkRedTicketInformationGDao.getRedTicketMatchList(map);
        if (redTicketMatches.size()>0) {
            for (int i = 0; i < redTicketMatches.size(); i++) {
                if (redTicketMatches.get(i).getBusinessType().equals("2")) {
                    RedTicketMatch redTicketMatch = redTicketMatches.get(i);
                    BigDecimal taxRate = redTicketMatch.getTaxRate().divide(new BigDecimal(100));
                    taxRate = taxRate.add(new BigDecimal(1));
                    redTicketMatches.get(i).setRedTotalAmount(redTicketMatches.get(i).getRedTotalAmount().multiply(taxRate));
                }
            }
        }
        return redTicketMatches;
    }

    @Override
    public Integer getRedTicketMatchListCount(Map<String, Object> map) {
        return checkRedTicketInformationGDao.getRedTicketMatchListCount(map);
    }

    @Override
    @Transactional
    public String cancelRedRushInformation(Map<String, Object> map){
        Boolean flag=false;
        try {
            //取消退货状态
            if (map.get("businessType").equals("1")){
                checkRedTicketInformationGDao.cancelReturnGoodsStatus(map);
            }
            //取消协议状态
            if (map.get("businessType").equals("2")){
                checkRedTicketInformationGDao.cancelAgreementStatus(map);
            }
            //红票匹配状态作废
            checkRedTicketInformationGDao.redRushInformationObsolete(map);

            //清空发票明细红冲数据
            checkRedTicketInformationGDao.clearTicketInformationData(map);
            //发票中间表查询
            List<RedTicketMatchMiddle> redTicketMatchMiddles = checkRedTicketInformationGDao.queryRedTicketMatchMiddle(map);
            //发票可红冲金额回冲
            for (int i = 0; i < redTicketMatchMiddles.size(); i++) {
                checkRedTicketInformationGDao.invoiceRedRushAmountBackflush(redTicketMatchMiddles.get(i));
            }
            flag=true;
        }catch(Exception e){
            LOGGER.info("取消失败 {}",e);
            throw new RuntimeException();
        }
        if(flag){
            return "取消成功";
        }else{
            return "取消失败";
        }

    }

    @Override
    public List<ProtocolEntity>  protocolList(Map<String, Object> params){
        return checkRedTicketInformationGDao.protocolList(params);
    }
    @Override
    public Integer protocolListCouont(Map<String, Object> map) {
        return checkRedTicketInformationGDao.protocolListCouont(map);
    }

    @Override
    public List<RedTicketMatchMiddle> invoiceList(Map<String, Object> params){
        List<InvoiceDetail> invoiceDetails=checkRedTicketInformationGDao.invoiceDetailList(params);
        List<RedTicketMatchMiddle> invoiceEntities=checkRedTicketInformationGDao.invoiceList(params);
        //计算发票含税红冲金额
        for(int i=0;i<invoiceEntities.size();i++){
            BigDecimal taxRate=new BigDecimal(invoiceDetails.get(0).getTaxRate()).divide(new BigDecimal(100));
            taxRate=taxRate.add(new BigDecimal(1));
            invoiceEntities.get(i).setRedRushAmount(invoiceEntities.get(i).getRedRushAmount().multiply(taxRate));
        }
        return invoiceEntities;
    }
    @Override
    public Integer invoiceListCount(Map<String, Object> map) {
        return checkRedTicketInformationGDao.invoiceListCount(map);
    }

    @Override
    public List<InvoiceDetail> invoiceDetailList(Map<String, Object> params){
        List<InvoiceDetail> invoiceDetails=checkRedTicketInformationGDao.invoiceDetailList(params);
        if(invoiceDetails.size()>0) {
            BigDecimal taxRate = new BigDecimal(invoiceDetails.get(0).getTaxRate()).divide(new BigDecimal(100));
            taxRate = taxRate.add(new BigDecimal(1));
            //计算发票明细含税红冲金额
            for (int i = 0; i < invoiceDetails.size(); i++) {
                invoiceDetails.get(i).setRedRushAmount(invoiceDetails.get(i).getRedRushAmount().multiply(taxRate));
            }
        }

        return invoiceDetails;
    }
    @Override
    public Integer invoiceDetailListCount(Map<String, Object> map) {
        return checkRedTicketInformationGDao.invoiceDetailListCount(map);
    }

    @Override
    public List<RedTicketMatchDetail> redTicketMatchDetailList(Map<String, Object> params){
        List<RedTicketMatchDetail> redTicketMatchDetails=checkRedTicketInformationGDao.redTicketMatchDetailList(params);
        //计算红冲明细含税红冲金额
        for (int i=0;i<redTicketMatchDetails.size();i++){
            RedTicketMatchDetail redTicketMatchDetail=redTicketMatchDetails.get(i);
            BigDecimal taxRate=new BigDecimal(redTicketMatchDetail.getTaxRate()).divide(new BigDecimal(100));
            taxRate=taxRate.add(new BigDecimal(1));
            redTicketMatchDetails.get(i).setRedRushAmount(redTicketMatchDetail.getRedRushAmount().multiply(taxRate));
        }
        return redTicketMatchDetails;
    }
    @Override
    public Integer redTicketMatchDetailListCount(Map<String, Object> map) {
        return checkRedTicketInformationGDao.redTicketMatchDetailListCount(map);
    }
}
