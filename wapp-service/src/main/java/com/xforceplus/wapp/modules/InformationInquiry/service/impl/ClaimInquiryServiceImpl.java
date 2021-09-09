package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.ClaimInquiryDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.PoInquiryDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.ClaimInquiryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ClaimInquiryServiceImpl implements ClaimInquiryService {
    private static  final Logger LOGGER=getLogger(ClaimInquiryServiceImpl.class);
    @Autowired
    private ClaimInquiryDao claimInquiryDao;
    @Override
    public List<ClaimEntity> claimlist(Map<String, Object> map){
        List<ClaimEntity> list=claimInquiryDao.claimlist(map);

        list.forEach(claimEntity -> {
            if(claimEntity.getNewAmount()!=null) {
                if (claimEntity.getNewAmount().compareTo(BigDecimal.ZERO) != 0) {
                    claimEntity.setClaimAmount(claimEntity.getNewAmount());
                }
            }
            String dept=claimEntity.getDept();
            if(StringUtils.isNotBlank(dept)) {
                dept = dept.substring(0, dept.length() - 1);
                Integer deptOfInt;
                try{
                    deptOfInt = Integer.valueOf(dept);
                    if (deptOfInt < 10) {
                        dept = "0" + dept;
                    }
                    claimEntity.setDept(dept);
                }catch (Exception e){
                    LOGGER.info("部门号转义异常 {}",e);
                    LOGGER.info("异常部门号为 {}",claimEntity.getDept());
                }
            }
        });
        return list;
    }
    @Override
    public Integer claimlistCount(Map<String, Object> map){
        return claimInquiryDao.claimlistCount(map);
    }

    @Override
    public List<ClaimExcelEntity> selectExcelClaimlist(Map<String, Object> map) {
        List<ClaimEntity> list=claimInquiryDao.claimlist(map);
        List<ClaimExcelEntity> excelList = new LinkedList<ClaimExcelEntity>();
        ClaimExcelEntity excel = null;
        for(ClaimEntity claimEntity : list){
            excel = new ClaimExcelEntity();  //实体转换类型，便于导出Excel
            excel.setRownumber(claimEntity.getRownumber());
            excel.setVenderid(claimEntity.getVenderid());
            excel.setStoreNbr(claimEntity.getStoreNbr());
            excel.setJvcode(claimEntity.getJvcode());
            excel.setClaimno(claimEntity.getClaimno());
            excel.setInvoiceno(claimEntity.getInvoiceno());
            excel.setPostdate(formatDate(claimEntity.getPostdate()));
            if(claimEntity.getNewAmount()!=null) {
                if (claimEntity.getNewAmount().compareTo(BigDecimal.ZERO) != 0) {
                    claimEntity.setClaimAmount(claimEntity.getNewAmount());
                }
            }
            excel.setClaimAmount(formatAmount(claimEntity.getClaimAmount().toString()));
            excel.setMatchstatus(formatedxhyMatchStatusType(claimEntity.getMatchstatus()));
            excel.setHostStatus(formateVenderType(claimEntity.getHostStatus()));

            String dept=claimEntity.getDept();
            excel.setDept(dept);
            if(StringUtils.isNotBlank(dept)) {
                dept = dept.substring(0, dept.length() - 1);
                Integer deptOfInt;
                try{
                    deptOfInt = Integer.valueOf(dept);
                    if (deptOfInt < 10) {
                        dept = "0" + dept;
                    }
                    excel.setDept(dept);
                }catch (Exception e){
                    excel.setDept(claimEntity.getDept());
                    LOGGER.info("部门号转义异常 {}",e);
                    LOGGER.info("异常部门号为 {}",claimEntity.getDept());
                }
            }
            excelList.add(excel);
        }
        excel = new ClaimExcelEntity();
        excel.setRownumber("合计：");
        excel.setClaimAmount(map.get("claimAmount").toString());
        excelList.add(excel);
        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatedxhyMatchStatusType(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            return "";
        }else if("0".equals(hostStatus)){
            value="未匹配";
        }else if("1".equals(hostStatus)){
            value="预匹配";
        }else if("2".equals(hostStatus)){
            value="部分匹配";
        }else if("3".equals(hostStatus)){
            value="完全匹配";
        }else if("4".equals(hostStatus)){
            value="差异匹配";
        }else if("5".equals(hostStatus)){
            value="匹配失败";
        }else if("6".equals(hostStatus)){
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
    private String formatAmount(String d) {
        try {
            if(StringUtils.isEmpty(d)){
                return "";
            }else{
                BigDecimal b=new BigDecimal(Double.parseDouble(d));
                DecimalFormat df=new DecimalFormat("######0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                return df.format(b);
            }
        }catch (Exception e){
            return "";
        }
    }
}
