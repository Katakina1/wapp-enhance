package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.dao.OverseasinvoiceDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.OverseasInvoiceImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.OverseasinvoiceService;
import com.xforceplus.wapp.modules.signin.service.impl.ScannerSignServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class OverseasinvoiceServiceImpl implements OverseasinvoiceService {
    private static final Logger LOGGER= getLogger(OverseasinvoiceServiceImpl.class);
    @Autowired
    private OverseasinvoiceDao overseasinvoiceDao;

    @Override
    public List<OverseasInvoiceEntity> list(Map<String, Object> map){
        List<OverseasInvoiceEntity> listo=overseasinvoiceDao.list(map);
        for (OverseasInvoiceEntity oe:listo) {
            //获取发票类型
            String invoiceType = ScannerSignServiceImpl.getFplx(oe.getInvoiceCode());
            oe.setInvoiceType(invoiceType);
        }
        return listo;
    }
    @Override
    public Integer listCount(Map<String, Object> map){
        return overseasinvoiceDao.listCount(map);
    }

    @Override
    @Transactional
    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName){
        //进入解析excel方法
        final OverseasInvoiceImport overseasInvoiceImport = new OverseasInvoiceImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<OverseasInvoiceEntity> overseasInvoiceList = overseasInvoiceImport.analysisExcel();
            if(overseasInvoiceList.size()>1000){
                LOGGER.info("excel数据不能超过1000条");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel数据不能超过1000条！");
                return map;
            }


            if (!overseasInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<OverseasInvoiceEntity>> entityMap =OverseasInvoiceImportData(overseasInvoiceList,logingName);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount4", entityMap.get("errorEntityList4").size());
                map.put("errorCount1", entityMap.get("errorEntityList1").size());
                map.put("errorCount2", entityMap.get("errorEntityList2").size());
                map.put("errorCount3", entityMap.get("errorEntityList3").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        catch(Exception e){
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "请导入正确的海外发票模板！");
        }
        return map;
    }

    private Map<String, List<OverseasInvoiceEntity>> OverseasInvoiceImportData(List<OverseasInvoiceEntity> overseasInvoiceList, String loginname){
        //返回值
        final Map<String, List<OverseasInvoiceEntity>> map = newHashMap();
        //导入成功的数据集
        final List<OverseasInvoiceEntity> successEntityList = newArrayList();
        //excel中重复的数据
        final List<OverseasInvoiceEntity> errorEntityList1 = newArrayList();
        //数据不全的数据集
        final List<OverseasInvoiceEntity> errorEntityList2 = newArrayList();
        //数据不正确的数据集
        final List<OverseasInvoiceEntity> errorEntityList3 = newArrayList();
        //已有数据的数据集
        final List<OverseasInvoiceEntity> errorEntityList4 = newArrayList();

        Date de = new Date();
        //判断是否有重复数据
        for (int d=0;d<overseasInvoiceList.size()-1;d++) {
            for (int k= overseasInvoiceList.size() - 1; k >d; k--) {
                if(overseasInvoiceList.get(d).getInvoiceNo().equals(overseasInvoiceList.get(k).getInvoiceNo())&&
                        overseasInvoiceList.get(d).getInvoiceCode().equals(overseasInvoiceList.get(k).getInvoiceCode())){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(k),loginname,de,"重复数据");
                    errorEntityList1.add(overseasInvoiceList.get(k));
                    overseasInvoiceList.remove(k);
                }
            }
           // successEntityList.add(overseasInvoiceList.get(d));
        }

        //判断是否有空数据,数据是否正确
        for(int i=0;i<overseasInvoiceList.size();i++){
            String invoiceNo=overseasInvoiceList.get(i).getInvoiceNo();
            String invoiceCode=overseasInvoiceList.get(i).getInvoiceCode();
            String invoiceDate=overseasInvoiceList.get(i).getInvoiceDate();
            String venderid=overseasInvoiceList.get(i).getVenderid();
            String jvcode=overseasInvoiceList.get(i).getJvcode();
            String companyCode=overseasInvoiceList.get(i).getCompanyCode();
            String store=overseasInvoiceList.get(i).getStore();
            String taxAmount=overseasInvoiceList.get(i).getTaxAmount().toString();
            String taxRate=overseasInvoiceList.get(i).getTaxRate().toString();
            String taxCode=overseasInvoiceList.get(i).getTaxCode();
            String costAmount=overseasInvoiceList.get(i).getCostAmount().toString();
            String certificateNo=overseasInvoiceList.get(i).getCertificateNo();
            String totalAmount=overseasInvoiceList.get(i).getTotalAmount().toString();
            String flowType=overseasInvoiceList.get(i).getFlowType();
            if(invoiceNo.isEmpty()||invoiceCode.isEmpty()||invoiceDate.isEmpty()||venderid.isEmpty()||jvcode.isEmpty()||
                    companyCode.isEmpty()||store.isEmpty()||taxAmount.isEmpty()||taxRate.isEmpty()||taxCode.isEmpty()||
                    costAmount.isEmpty()||certificateNo.isEmpty()||totalAmount.isEmpty()||flowType.isEmpty()){
                overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"2");
                errorEntityList2.add(overseasInvoiceList.get(i));
                overseasInvoiceList.remove(i);
                i=i-1;
                continue;
            }else {
                String invoiceNoRegex="^[0-9]{8}$";
                String invoiceCode1Regex="^[0-9]{10}$";
                String invoiceCode2Regex="^[0-9]{12}$";
                String venderidRegex="^d{1,6}$";
                String certificateNoRegex="^[A-Za-z0-9]*$";
                String storNoRegex="^[0-9]{4}$";
                if(!invoiceNo.matches(invoiceNoRegex)){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"发票号格式不正确");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(!invoiceCode.matches(invoiceCode1Regex)&&!invoiceCode.matches(invoiceCode2Regex)){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"发票代码格式不正确");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(!CommonUtil.isValidNum(invoiceDate, "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$")){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"开票日期格式不正确");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(overseasinvoiceDao.getXfCount(overseasInvoiceList.get(i))<1){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"没有该供应商信息");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                //门店号码校验
                if(!store.matches(storNoRegex)){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"门店号格式不正确");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(overseasinvoiceDao.getGfCount(overseasInvoiceList.get(i))<1){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"没有该购方或门店信息");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                BigDecimal taxRate1=new BigDecimal(taxRate).divide(new BigDecimal(100));
                BigDecimal taxAmouont1=new BigDecimal(costAmount).multiply(taxRate1);
                taxAmouont1=taxAmouont1.setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal taxAmount2=taxAmouont1.subtract(new BigDecimal(taxAmount));
                if(taxAmount2.abs().compareTo(new BigDecimal(0.05))==1){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"税额数据有误");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                BigDecimal totalAmount1=new BigDecimal(costAmount).add(new BigDecimal(taxAmount));
                BigDecimal totalAmount2=new BigDecimal(totalAmount).subtract(totalAmount1);
                if(totalAmount2.abs().compareTo(new BigDecimal(0.05))==1){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"价税合计数据有误");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(!certificateNo.matches(certificateNoRegex)){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"凭证号格式错误");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }
                if(!flowType.equals("商品")&&!flowType.equals("费用")&&!flowType.equals("资产")){
                    overseasinvoiceDao.saveErrorInvoice(overseasInvoiceList.get(i),loginname,de,"业务类型有误，请输入商品、费用或资产");
                    errorEntityList3.add(overseasInvoiceList.get(i));
                    overseasInvoiceList.remove(i);
                    i=i-1;
                    continue;
                }else{
                    overseasInvoiceList.get(i).setFlowType(fromType(overseasInvoiceList.get(i).getFlowType()));
                }
            }
        }
        final Map<String,Object> map1 = newHashMap();
        for(int q=0;q<overseasInvoiceList.size();q++){
            map1.put("invoiceCode",overseasInvoiceList.get(q).getInvoiceCode());
            map1.put("invoiceNo",overseasInvoiceList.get(q).getInvoiceNo());
            map1.put("lastModifyBy",loginname);
            map1.put("lastModifyTime",de);
            map1.put("createBy",loginname);
            map1.put("createTime",de);
            if(overseasinvoiceDao.listCount(map1)>0){
                overseasInvoiceList.get(q).setLastModifyBy(loginname);
                overseasInvoiceList.get(q).setLastModifyTime(de);
                overseasInvoiceList.get(q).setCreateBy(loginname);
                overseasInvoiceList.get(q).setCreateTime(de);
                overseasinvoiceDao.updateInvoice(overseasInvoiceList.get(q));
                errorEntityList4.add(overseasInvoiceList.get(q));
                overseasInvoiceList.remove(q);
                q=q-1;
            }else {
                successEntityList.add(overseasInvoiceList.get(q));
            }
        }
        List<List<OverseasInvoiceEntity>> splitProtocolList=splitList(successEntityList,100);
        for(List<OverseasInvoiceEntity> list : splitProtocolList ) {
            overseasinvoiceDao.saveInvoice(list);
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList4", errorEntityList4);
        map.put("errorEntityList1",errorEntityList1);
        map.put("errorEntityList2",errorEntityList2);
        map.put("errorEntityList3",errorEntityList3);

        return map;
    }
    public String fromType(String type){
        String flowType="";
        if(type.equals("商品")){
            flowType="1";
        }
        if(type.equals("资产")){
            flowType="2";
        }
        if(type.equals("费用")){
            flowType="3";
        }
        return flowType;
    }
    private static boolean valiDateTimeWithLongFormat(String timeStr) {
        String format = "([0-9]{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(timeStr);
        if (matcher.matches()) {
            pattern = Pattern.compile("(\\d{4})-(\\d+)-(\\d+).*");
            matcher = pattern.matcher(timeStr);
            if (matcher.matches()) {
                int y = Integer.valueOf(matcher.group(1));
                int m = Integer.valueOf(matcher.group(2));
                int d = Integer.valueOf(matcher.group(3));
                if (d > 28) {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m-1, 1);
                    int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    return (lastDay >= d);
                }
            }
            return true;
        }
        return false;
    }

    private static  List<List<OverseasInvoiceEntity>> splitList(List<OverseasInvoiceEntity> sourceList, int  batchCount) {
        List<List<OverseasInvoiceEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

    @Override
    @Transactional
    public Integer delete(String loginname){
        return overseasinvoiceDao.delete(loginname);
    }

    public List<OverseasInvoiceEntity> failedlist(String loginname){
        return overseasinvoiceDao.failedlist(loginname);
    }

}
