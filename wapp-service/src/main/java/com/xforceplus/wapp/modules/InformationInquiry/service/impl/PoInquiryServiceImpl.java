package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.PoInquiryDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class PoInquiryServiceImpl implements PoInquiryService {
    @Autowired
    private PoInquiryDao poInquiryDao;
    @Override
    public List<poEntity> polist(Map<String, Object> map){
        List<poEntity> poEntityList=poInquiryDao.polist(map);
        for(int i=0;i<poEntityList.size();i++){
            if(!StringUtils.isEmpty(poEntityList.get(i).getHostStatus())) {
                if (!"1".equals(poEntityList.get(i).getHostStatus())) {
                    if(poEntityList.get(i).getNewAmount()!=null) {
                        if (poEntityList.get(i).getNewAmount().compareTo(BigDecimal.ZERO) > 0) {
                            poEntityList.get(i).setReceiptAmount(poEntityList.get(i).getNewAmount());
                        } else {
                            poEntityList.get(i).setReceiptAmount(poEntityList.get(i).getAmountpaid());
                        }
                    }else{
                        poEntityList.get(i).setReceiptAmount(poEntityList.get(i).getAmountpaid());

                    }
                }
            }
        }
        return poEntityList;
    }
    @Override
    public Integer polistCount(Map<String, Object> map){
        return poInquiryDao.polistCount(map);
    }

    @Override
    public List<poExcelEntity> selectExcelpolist(Map<String, Object> map){
        List<poEntity> poEntityList=poInquiryDao.polist(map);
        List<poExcelEntity> list = new LinkedList<poExcelEntity>();
        poExcelEntity excel= null;
        double sumReturnAmount= 0.00;
        for(poEntity entity:poEntityList){
            excel = new poExcelEntity();
            excel.setRownumber(entity.getRownumber());
            excel.setJvcode(entity.getJvcode());
            excel.setVenderId(entity.getVenderId());
            excel.setPoCode(entity.getPoCode());
            excel.setPoType(entity.getPoType());
            excel.setReceiptDate(formatDateString(entity.getReceiptDate()));
            excel.setReceiptId(entity.getReceiptId());
            excel.setTractionNbr(entity.getTractionNbr());
            excel.setReceiptAmount(formatAmount(entity.getReceiptAmount().toString()));
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setDxhyMatchStatus(formatedxhyMatchStatusType(entity.getDxhyMatchStatus()));
            excel.setHostStatus(formateVenderType(entity.getHostStatus()));
            if(!StringUtils.isEmpty(entity.getHostStatus())) {
                if (!"1".equals(entity.getHostStatus())) {
                    if(entity.getNewAmount()!=null) {
                        if (entity.getNewAmount().compareTo(BigDecimal.ZERO) > 0) {
                            excel.setReceiptAmount(formatAmount(entity.getNewAmount().toString()));
                        }else{
                            excel.setReceiptAmount(formatAmount(entity.getAmountpaid().toString()));
                        }
                    }else{
                        excel.setReceiptAmount(formatAmount(entity.getAmountpaid().toString()));
                    }
                }
            }
            sumReturnAmount= sumReturnAmount  +  entity.getReceiptAmount().doubleValue();
            list.add(excel);
        }
        excel = new poExcelEntity();
        excel.setRownumber("合计:");
        excel.setReceiptAmount(sumReturnAmount+"");
        list.add(excel);
        return list;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatedxhyMatchStatusType(String dxhyMatchStatus){
        String value="";
        if(StringUtils.isEmpty(dxhyMatchStatus)){
            return "";
        }else if("0".equals(dxhyMatchStatus)){
            value="未匹配";
        }else if("1".equals(dxhyMatchStatus)){
            value="预匹配";
        }else if("2".equals(dxhyMatchStatus)){
            value="部分匹配";
        }else if("3".equals(dxhyMatchStatus)){
            value="完全匹配";
        }else if("4".equals(dxhyMatchStatus)){
            value="差异匹配";
        }else if("5".equals(dxhyMatchStatus)){
            value="匹配失败";
        }else if("6".equals(dxhyMatchStatus)){
            value="取消匹配";
        }
        return value;
    }

    private String formateVenderType(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            return "未处理";
        }else if("0".equals(hostStatus)){
            value="未处理";
        }else if("1".equals(hostStatus)){
            value="未处理";
        }else if("5".equals(hostStatus)){
            value="已处理";
        }else if("10".equals(hostStatus)){
            value="未处理";
        }else if("13".equals(hostStatus)){
            value="已删除";
        }else if("14".equals(hostStatus)){
            value="待付款";
        }else if("11".equals(hostStatus)){
            value="已匹配";
        }else if("12".equals(hostStatus)){
            value="已匹配";
        }else if("15".equals(hostStatus)){
            value="已匹配";
        } else if("19".equals(hostStatus)){
            value="已付款";
        }else if("9".equals(hostStatus)){
            value="待付款";
        }else if("99".equals(hostStatus)){
            value="已付款";
        }else if("999".equals(hostStatus)){
            value="已付款";
        }else if("8".equals(hostStatus)){
            value="HOLD";
        }

        return value;
    }
    private double sumReturnAmount;
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }
}
