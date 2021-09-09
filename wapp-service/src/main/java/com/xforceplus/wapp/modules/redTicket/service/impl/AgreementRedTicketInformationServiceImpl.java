package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.redTicket.dao.AgreementRedTicketInformationDao;
import com.xforceplus.wapp.modules.redTicket.dao.GenerateRedTicketInformationDao;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RoleEntity;
import com.xforceplus.wapp.modules.redTicket.service.AgreementRedTicketInformationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
@Service
public class AgreementRedTicketInformationServiceImpl implements AgreementRedTicketInformationService {
    private static final Logger LOGGER= getLogger(AgreementRedTicketInformationServiceImpl.class);

    private final GenerateRedTicketInformationDao generateRedTicketInformationDao;
    private final AgreementRedTicketInformationDao agreementRedTicketInformationDao;
    @Autowired
    public AgreementRedTicketInformationServiceImpl(GenerateRedTicketInformationDao generateRedTicketInformationDao,AgreementRedTicketInformationDao agreementRedTicketInformationDao){
        this.generateRedTicketInformationDao=generateRedTicketInformationDao;
        this.agreementRedTicketInformationDao=agreementRedTicketInformationDao;
    }

    @Override
    public Integer getInvoiceCount(Map<String, Object> map){
        List<InvoiceEntity> list=agreementRedTicketInformationDao.getInvoicelist(map);
        for (int i=list.size()-1;i>=0;i--){
            InvoiceEntity invoiceEntity=list.get(i);
            if(StringUtils.isEmpty(list.get(i).getMatchno())){
                BigDecimal invoiceAmount=invoiceEntity.getInvoiceAmount().multiply(new BigDecimal(0.6));
                double invoicenum=invoiceAmount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if ((new BigDecimal(invoicenum).compareTo(invoiceEntity.getRedMoneyAmount())) == 1 || (new BigDecimal(invoicenum).compareTo(invoiceEntity.getRedMoneyAmount())) == 0) {
                    list.remove(i);
                    continue;
                }
                list.get(i).setRedMoneyAmount(list.get(i).getRedMoneyAmount().subtract(invoiceAmount));
            }else {
                BigDecimal settlementAmount = invoiceEntity.getSettlementAmount().multiply(new BigDecimal(0.6));
                double setAmount = settlementAmount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if ((new BigDecimal(setAmount).compareTo(invoiceEntity.getRedMoneyAmount())) == 1 || (new BigDecimal(setAmount).compareTo(invoiceEntity.getRedMoneyAmount())) == 0) {
                    list.remove(i);
                    continue;
                }
                list.get(i).setRedMoneyAmount(list.get(i).getRedMoneyAmount().subtract(settlementAmount));
            }

        }
        return list.size();
    }

    @Override
    public List<InvoiceEntity> getInvoicelist(Map<String, Object> map){
        map.remove("offset");
        List<InvoiceEntity> list=generateRedTicketInformationDao.getInvoicelist(map);
        for (int i=list.size()-1;i>=0;i--){
            InvoiceEntity invoiceEntity=list.get(i);
            if(StringUtils.isEmpty(list.get(i).getMatchno())){
                BigDecimal invoiceAmount=invoiceEntity.getInvoiceAmount().multiply(new BigDecimal(0.6));
                double invoicenum=invoiceAmount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if ((new BigDecimal(invoicenum).compareTo(invoiceEntity.getRedMoneyAmount())) == 1 || (new BigDecimal(invoicenum).compareTo(invoiceEntity.getRedMoneyAmount())) == 0) {
                    list.remove(i);
                    continue;
                }
                list.get(i).setRedMoneyAmount(list.get(i).getRedMoneyAmount().subtract(invoiceAmount));
            }else {
                BigDecimal settlementAmount = invoiceEntity.getSettlementAmount().multiply(new BigDecimal(0.6));
                double setAmount = settlementAmount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if ((new BigDecimal(setAmount).compareTo(invoiceEntity.getRedMoneyAmount())) == 1 || (new BigDecimal(setAmount).compareTo(invoiceEntity.getRedMoneyAmount())) == 0) {
                    list.remove(i);
                    continue;
                }
                list.get(i).setRedMoneyAmount(list.get(i).getRedMoneyAmount().subtract(settlementAmount));
            }
        }
         return list;
    }

    public List<ProtocolEntity> protocollist(Map<String, Object> params){
        List<String> payItem=agreementRedTicketInformationDao.getPayItemlist();
        String[] strings = new String[payItem.size()];
        payItem.toArray(strings);
        params.put("payItem",payItem);
        return agreementRedTicketInformationDao.protocollist(params);
    }

    public Integer protocolCount(Map<String, Object> params){
        List<String> payItem=agreementRedTicketInformationDao.getPayItemlist();
        String[] strings = new String[payItem.size()];
        payItem.toArray(strings);
        params.put("payItem",payItem);
        return agreementRedTicketInformationDao.protocolCount(params);
    }

    public List<ProtocolDetailEntity> protocoldetaillist(Map<String, Object> params){
        return agreementRedTicketInformationDao.protocoldetaillist(params);
    }
   public String selectPurchaseInvoiceNo(String PurchaseInvoiceNo){
       return agreementRedTicketInformationDao.selectPurchaseInvoiceNo(PurchaseInvoiceNo);
    }
   public List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(String caseDate, String protocolNo){
        return agreementRedTicketInformationDao.queryInvoiceDetailList(caseDate,protocolNo);
   }

  public List<RoleEntity> selectRoleCode(long userId){
       return agreementRedTicketInformationDao.selectRoleCode(userId);
    }
}
