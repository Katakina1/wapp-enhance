package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.businessData.dao.AgreementDao;
import com.xforceplus.wapp.modules.businessData.dao.ReturngoodsDao;
import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.redTicket.dao.*;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.service.GenerateRedTicketInformationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;
@Service
public class GenerateRedTicketInformationServiceImpl implements GenerateRedTicketInformationService {
    private static final Logger LOGGER= getLogger(GenerateRedTicketInformationServiceImpl.class);
    private final GenerateRedTicketInformationDao generateRedTicketInformationDao;
    private final RedTicketMatchDao redTicketMatchDao;
    private final RedTicketMatchDetailDao redTicketMatchDetailDao;
    private final InvoiceDetailDao invoiceDetailDao;
    private final ReturngoodsDao returngoodsDao;
    private final RedTicketMatchMiddleDao redTicketMatchMiddleDao;
    private final ReturnAgreementMiddleDao returnAgreementMiddleDao;
    private final AgreementDao agreementDao;
    private final AgreementRedTicketInformationDao agreementRedTicketInformationDao;
    @Autowired
    public GenerateRedTicketInformationServiceImpl(GenerateRedTicketInformationDao generateRedTicketInformationDao,RedTicketMatchDetailDao redTicketMatchDetailDao,
                                                   InvoiceDetailDao invoiceDetailDao,RedTicketMatchDao redTicketMatchDao,ReturngoodsDao returngoodsDao,
                                                   RedTicketMatchMiddleDao redTicketMatchMiddleDao,ReturnAgreementMiddleDao returnAgreementMiddleDao,
                                                   AgreementDao agreementDao,AgreementRedTicketInformationDao agreementRedTicketInformationDao){
        this.generateRedTicketInformationDao=generateRedTicketInformationDao;
        this.redTicketMatchDetailDao=redTicketMatchDetailDao;
        this.invoiceDetailDao=invoiceDetailDao;
        this.redTicketMatchDao=redTicketMatchDao;
        this.returngoodsDao=returngoodsDao;
        this.redTicketMatchMiddleDao=redTicketMatchMiddleDao;
        this.returnAgreementMiddleDao=returnAgreementMiddleDao;
        this.agreementDao=agreementDao;
        this.agreementRedTicketInformationDao=agreementRedTicketInformationDao;
    }

    @Override
    public List<InvoiceEntity> getInvoicelist(Map<String, Object> map) {
        List<InvoiceEntity> list= generateRedTicketInformationDao.getInvoicelist(map);
        return list;
    }

    @Override
    public OrganizationEntity queryGfCode(String gfName){
        return generateRedTicketInformationDao.queryGfCode(gfName);
    }
    @Override
    public Integer invoiceCount(Map<String, Object> map) {
        return generateRedTicketInformationDao.invoiceCount(map);
    }

    //生成红票数据
    @Override
    @Transactional
    public String generateRedTicketData(GenerateRedRush generateRedRush,Integer userId,String userName,String userCode){
        Boolean flag=false;
        try {
            BigDecimal sumRedRushAmount=generateRedRush.getSumRedRushAmount();
            List<InvoiceDetail> invoiceDetails=generateRedRush.getInvoiceDetails();
            List<InvoiceDetail> redRushDetails=generateRedRush.getRedRushDetails();
            List<ReturngoodsEntity> returnGoods=generateRedRush.getReturnGoods();
            List<ProtocolEntity> agreementEntities=generateRedRush.getAgreementEntities();
            String businessType=generateRedRush.getBusinessType();
            RedTicketMatch redTicketMatcha=new RedTicketMatch();
            Set<InvoiceEntity> set = new HashSet();
            List<InvoiceEntity> invoices=new ArrayList<>();
            Date de = new Date();
            //查询最大红票序列号
            //生成红票序列号
            RedTicketMatch redTicketMatch = redTicketMatchDao.querymaxredTicketNumber();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
            String str = df.format(de);
            String str2;
            if (redTicketMatch!=null){
                str2 = redTicketMatch.getRedTicketDataSerialNumber();
                String str3 = str2.substring(0,6);
                if (str.equals(str3)) {
                    Long b = Long.valueOf(str2);
                    b = b + 1;
                    str2 = String.valueOf(b);
                } else {
                    AtomicInteger atomicNum = new AtomicInteger();
                    int newNum = atomicNum.incrementAndGet();
                    String newStrNum = String.format("%06d", newNum);
                    str2 = str + newStrNum;
                }
            }else {
                AtomicInteger atomicNum = new AtomicInteger();
                int newNum = atomicNum.incrementAndGet();
                String newStrNum = String.format("%06d", newNum);
                str2 = str + newStrNum;
            }
            //查询购方税号
            String gfTaxNo=generateRedTicketInformationDao.getGfTaxNo(invoiceDetails.get(0).getUuid());
            //查询公司代码
            String companyCode=generateRedTicketInformationDao.getCompanycode(generateRedRush.getOrgcode());
            //红票匹配
            redTicketMatcha.setRedTotalAmount(sumRedRushAmount);
            redTicketMatcha.setRedTicketDataSerialNumber(str2);
            redTicketMatcha.setBusinessType(businessType);
            redTicketMatcha.setRedTicketCreationTime(de);
            redTicketMatcha.setRedTicketFounder(userName);
            redTicketMatcha.setVenderid(userCode);
            redTicketMatcha.setGfTaxNo(gfTaxNo);
            redTicketMatcha.setTaxRate(new BigDecimal(invoiceDetails.get(0).getTaxRate()));
            redTicketMatcha.setJvcode(generateRedRush.getOrgcode());
            redTicketMatcha.setCompanyCode(companyCode);
            redTicketMatchDao.insertRedTicketMatch(redTicketMatcha);

            //发票明细
            for (int i=0;i<invoiceDetails.size();i++){
                invoiceDetailDao.redRushInvoiceDetails(invoiceDetails.get(i),str2);
            }
            //筛选所选择发票
            for (int d=0;d<invoiceDetails.size()-1;d++) {
                for (int y=invoiceDetails.size()-1;y>d;y--) {
                    if (invoiceDetails.get(d).getUuid().equals(invoiceDetails.get(y).getUuid())){
                        invoiceDetails.get(d).setRedRushAmount(invoiceDetails.get(d).getRedRushAmount().add(invoiceDetails.get(y).getRedRushAmount()));
                        invoiceDetails.remove(y);
                    }
                }
            }
            //发票
            Map<String, Object> map=new HashMap<>();
            map.put("userID",userId);
            map.put("userCode",userCode);
            for (int c=0;c<invoiceDetails.size();c++){
                map.put("uuid",invoiceDetails.get(c).getUuid());
                List<InvoiceEntity> invoiceEntities=generateRedTicketInformationDao.invoicelist(map);
                if (businessType.equals("2")){
                    if (StringUtils.isEmpty(invoiceEntities.get(0).getMatchno())){
                        double invoicenum=(invoiceEntities.get(0).getInvoiceAmount().multiply(new BigDecimal(0.6))).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                        invoiceEntities.get(0).setRedMoneyAmount(invoiceEntities.get(0).getRedMoneyAmount().subtract(new BigDecimal(invoicenum)));
                    }else {
                        invoiceEntities.get(0).setSettlementAmount(invoiceEntities.get(0).getSettlementAmount().multiply(new BigDecimal(0.6)));
                        invoiceEntities.get(0).setRedMoneyAmount(invoiceEntities.get(0).getRedMoneyAmount().subtract(invoiceEntities.get(0).getSettlementAmount()));
                    }
                    if (invoiceEntities.get(0).getRedMoneyAmount().compareTo(invoiceDetails.get(c).getRedRushAmount()) == 0||invoiceEntities.get(0).getRedMoneyAmount().compareTo(invoiceDetails.get(c).getRedRushAmount()) == 1) {
                        generateRedTicketInformationDao.invoiceRedRush(invoiceEntities.get(0).getUuid(), invoiceDetails.get(c).getRedRushAmount());

                        redTicketMatchMiddleDao.insertRedTicketMatchMiddle(invoiceEntities.get(0), redTicketMatcha.getId(), invoiceDetails.get(c).getRedRushAmount());
                    } else {
                        LOGGER.info("红冲失败");
                        throw new RuntimeException("红冲明细金额大于发票实际可红冲金额,红冲失败");
                    }
                }else {
                    if (invoiceEntities.get(0).getRedMoneyAmount().compareTo(invoiceDetails.get(c).getRedRushAmount()) >= 0) {
                        generateRedTicketInformationDao.invoiceRedRush(invoiceEntities.get(0).getUuid(), invoiceDetails.get(c).getRedRushAmount());

                        redTicketMatchMiddleDao.insertRedTicketMatchMiddle(invoiceEntities.get(0), redTicketMatcha.getId(), invoiceDetails.get(c).getRedRushAmount());
                    } else {
                        LOGGER.info("红冲失败");
                        throw new RuntimeException("红冲明细金额大于发票可红冲金额,红冲失败");
                    }
                }
            }

            //红冲明细
            for (int k=0;k<redRushDetails.size();k++) {
                redTicketMatchDetailDao.insertRedRushDetails(redRushDetails.get(k), str2);
            }
            //退货
            if(businessType.equals("1")) {
                for (int j = 0; j < returnGoods.size(); j++) {
                    returngoodsDao.redRushreturnGoods(returnGoods.get(j),userCode,str2);
                    //退货中间表
                    map.put("returnAgreementAssociation",returnGoods.get(j).getId());
                    map.put("redTicketMatchingAssociation",redTicketMatcha.getId());
                    map.put("id",null);
                    returnAgreementMiddleDao.insertReturnAgreementMiddle(map);
                }
            }
            //协议
            if(businessType.equals("2")) {
                for (int h = 0; h < agreementEntities.size(); h++) {
                    agreementRedTicketInformationDao.redRushAgreement(agreementEntities.get(h),userCode,str2);
                    //退协中间表
                    map.put("returnAgreementAssociation",agreementEntities.get(h).getId());
                    map.put("redTicketMatchingAssociation",redTicketMatcha.getId());
                    map.put("id",null);
                    returnAgreementMiddleDao.insertReturnAgreementMiddle(map);
                }
            }
            flag=true;
        }catch(Exception e){
            LOGGER.info("红冲失败",e);
            throw new RuntimeException(e.getMessage());
        }
        return "红冲成功";
    }

    @Override
    public List<ReturngoodsEntity> getReturnGoodsList(Map<String, Object> map) {
        return generateRedTicketInformationDao.getReturnGoodsList(map); }

    @Override
    public Integer getReturnGoodsCount(Map<String, Object> map) { return generateRedTicketInformationDao.getReturnGoodsCount(map); }
}
