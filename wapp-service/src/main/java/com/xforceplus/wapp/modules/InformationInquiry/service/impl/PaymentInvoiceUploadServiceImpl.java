package com.xforceplus.wapp.modules.InformationInquiry.service.impl;



import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.dao.PaymentInvoiceUploadDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.InputRedTicketInformationDao;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.export.EnterPackageNumberImport;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static com.google.common.collect.Maps.newHashMap;
import org.slf4j.Logger;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.slf4j.LoggerFactory.getLogger;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class PaymentInvoiceUploadServiceImpl implements PaymentInvoiceUploadService {
    private static final Logger LOGGER= getLogger(PaymentInvoiceUploadServiceImpl.class);
    @Autowired
    private PaymentInvoiceUploadDao paymentInvoiceUploadDao;

    @Override
    public List<PaymentInvoiceUploadEntity> queryList(Map<String, Object> map) {
        return paymentInvoiceUploadDao.queryList(map);
    }
    @Override
    public ReportStatisticsEntity queryTotalResult( Map<String, Object> map) {

        return paymentInvoiceUploadDao.queryTotalResult(map);
    }

    @Override
    public List<PaymentInvoiceUploadEntity> queryListAll(Map<String, Object> map) {

        return paymentInvoiceUploadDao.queryListAll(map);
    }

    @Override
    public List<PaymentInvoiceUploadEntity> queryListAllFail(Map<String, Object> map) {

        return paymentInvoiceUploadDao.queryListAllFail(map);
    }



    /**
     * 解析excel数据，解析保存入库
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName) {
        //进入解析excel方法
        final PaymentInvoiceUploadImport paymentInvoiceUploadImport = new PaymentInvoiceUploadImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<PaymentInvoiceUploadEntity> redInvoiceList = paymentInvoiceUploadImport.analysisExcel();
            if(redInvoiceList.size()>10000){
                LOGGER.info("excel数据不能超过10000条");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel数据不能超过10000条！");
                return map;
            }


            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<PaymentInvoiceUploadEntity>> entityMap =RedInvoiceImportData(redInvoiceList,logingName);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
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
        return map;
    }

    @Override
    public List<PaymentInvoiceUploadExcelEntity> transformExcle(List<PaymentInvoiceUploadEntity> list) {
        List<PaymentInvoiceUploadExcelEntity> list2=new ArrayList<>();
        for(int i = 0 ; i < list.size() ; i++) {
            PaymentInvoiceUploadEntity entity=list.get(i);
            PaymentInvoiceUploadExcelEntity paymentInvoiceUploadExcelEntity=new PaymentInvoiceUploadExcelEntity();

//序号
            paymentInvoiceUploadExcelEntity.setIndexNo(  String.valueOf(i+1));
            //扣款公司
            paymentInvoiceUploadExcelEntity.setJvcode(  entity.getJvcode());
            //供应商号
            paymentInvoiceUploadExcelEntity.setSupplierAssociation(  entity.getSupplierAssociation());
            //类型
            paymentInvoiceUploadExcelEntity.setCaseType(  entity.getCaseType());
            //备注
            paymentInvoiceUploadExcelEntity.setRemark( entity.getRemark());
            //换货号
            paymentInvoiceUploadExcelEntity.setExchangeNo( entity.getExchangeNo());
            //索赔号
            paymentInvoiceUploadExcelEntity.setReturnGoodsCode(  entity.getReturnGoodsCode());
            //定案日期
            paymentInvoiceUploadExcelEntity.setReturnGoodsDate(  entity.getReturnGoodsDate());
            //成本金额
            paymentInvoiceUploadExcelEntity.setReturnCostAmount(  entity.getReturnCostAmount());
            //供应商结款发票号
            paymentInvoiceUploadExcelEntity.setPaymentInvoiceNo(  entity.getPaymentInvoiceNo());
            //扣款日期
            paymentInvoiceUploadExcelEntity.setDeductionDate(  entity.getDeductionDate());
            //沃尔玛扣款发票号
            paymentInvoiceUploadExcelEntity.setPurchaseInvoiceNo(  entity.getPurchaseInvoiceNo());
            //税率
            paymentInvoiceUploadExcelEntity.setTaxRate(  entity.getTaxRate().toString());
            //含税金额
            paymentInvoiceUploadExcelEntity.setTaxAmount(  entity.getTaxAmount());
            //发送日期
            paymentInvoiceUploadExcelEntity.setSendDate( entity.getSendDate());
            //邮寄时间
            paymentInvoiceUploadExcelEntity.setMailData(  entity.getMailData());
            //快递单号
            paymentInvoiceUploadExcelEntity.setExpressNo(  entity.getExpressNo());
            //快递公司
            paymentInvoiceUploadExcelEntity.setExpressName(  entity.getExpressName());
            list2.add(paymentInvoiceUploadExcelEntity);
        }

        return list2;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String taxRate(String taxRate) {
        BigDecimal rate = new BigDecimal(taxRate);
        String str;
        if(rate.compareTo(new BigDecimal(1))==-1){
            rate = rate.multiply(new BigDecimal(100));
            str=rate.toString();
        }else {
            str = taxRate;
        }
        return str;

    }

    private Map<String, List<PaymentInvoiceUploadEntity>> RedInvoiceImportData(List<PaymentInvoiceUploadEntity> redInvoiceList, String loginname){
        //返回值
        final Map<String, List<PaymentInvoiceUploadEntity>> map = newHashMap();
        //导入成功的数据集
        final List<PaymentInvoiceUploadEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<PaymentInvoiceUploadEntity> errorEntityList = newArrayList();
        //导入数据格式正确但数据库已有的数据集
        final List<PaymentInvoiceUploadEntity> errorEntityList1 = newArrayList();
        //成功导入，但在数据库中供应商号和索赔号已经有的只能修改
        final List<PaymentInvoiceUploadEntity> errorEntityList2 = newArrayList();
        //excel中重复的数据
        final List<PaymentInvoiceUploadEntity> errorEntityList3 = newArrayList();


        for(int i = redInvoiceList.size()-1;i>=0;i--) {
            PaymentInvoiceUploadEntity redInvoiceData = redInvoiceList.get(i);
            String supplierAssociation = redInvoiceData.getSupplierAssociation();
            String returnGoodsCode = redInvoiceData.getReturnGoodsCode();
            String returnCostAmount = redInvoiceData.getReturnCostAmount();
            String taxRate1 = redInvoiceData.getTaxRate();

            String taxAmount = redInvoiceData.getTaxAmount();

            String returnGoodsDate = redInvoiceData.getReturnGoodsDate();
            String deductionDate = redInvoiceData.getDeductionDate();
            String sendDate = redInvoiceData.getSendDate();
            String mailData = redInvoiceData.getMailData();
            String jv = redInvoiceData.getJvcode();


            if (StringUtils.isEmpty(supplierAssociation)
                    || StringUtils.isEmpty(returnGoodsCode)
                    || StringUtils.isEmpty(returnCostAmount)
                    || StringUtils.isEmpty(taxRate1)
                    || StringUtils.isEmpty(taxAmount)
                    || StringUtils.isEmpty(returnGoodsDate)
                    || StringUtils.isEmpty(jv)
                    ) {
                redInvoiceData.setFailReason("扣款公司、供应商号、索赔号、成本金额、税率、含税金额、定案日期为空");
                errorEntityList.add(redInvoiceData);
                redInvoiceData.setCreateByName(loginname);
                paymentInvoiceUploadDao.saveInvoiceFail(redInvoiceData);
                redInvoiceList.remove(redInvoiceData);
            }
//            else if (
//                    (!CommonUtil.isValidNum(returnGoodsDate, "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))
//                    || (!deductionDate.equals("") && (!CommonUtil.isValidNum(deductionDate, "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$")))
//                    || (!sendDate.equals("") && (!CommonUtil.isValidNum(sendDate, "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$")))
//                    || (!mailData.equals("") && (!CommonUtil.isValidNum(mailData, "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$")))
//                    ) {
//                redInvoiceData.setFailReason("定案日期、扣款日期、发送日期、邮寄时间格式不对");
//                errorEntityList.add(redInvoiceData);
//                paymentInvoiceUploadDao.saveInvoiceFail(redInvoiceData);
//                redInvoiceList.remove(redInvoiceData);
//            }
            else if (new BigDecimal(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo((new BigDecimal(returnCostAmount).multiply(new BigDecimal(taxRate1)).add(new BigDecimal(returnCostAmount))).setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
                redInvoiceData.setFailReason("税率、含税金额、成本金额输入不正确");
                errorEntityList.add(redInvoiceData);
                redInvoiceData.setCreateByName(loginname);
                paymentInvoiceUploadDao.saveInvoiceFail(redInvoiceData);
                redInvoiceList.remove(redInvoiceData);
            } else {
                successEntityList.add(redInvoiceData);
            }
        }

        for  ( int  i  =   0 ; i  <  successEntityList.size()  -   1 ; i ++ )  {
            for  ( int  j  =  successEntityList.size()  -   1 ; j  >  i; j -- )  {
                String str1=successEntityList.get(j).getSupplierAssociation();
                String str11=successEntityList.get(i).getSupplierAssociation();
                String str2=successEntityList.get(j).getReturnGoodsCode();
                String str22=successEntityList.get(i).getReturnGoodsCode();
                String str3=successEntityList.get(j).getReturnGoodsDate();
                String str33=successEntityList.get(i).getReturnGoodsDate();
                String str4=successEntityList.get(j).getReturnCostAmount();
                String str44=successEntityList.get(i).getReturnCostAmount();
                String str5=successEntityList.get(j).getPaymentInvoiceNo();
                String str55=successEntityList.get(i).getPaymentInvoiceNo();
                String str6=successEntityList.get(j).getPurchaseInvoiceNo();
                String str66=successEntityList.get(i).getPurchaseInvoiceNo();
                if  (str1.equals(str11)&&str2.equals(str22)&&str3.equals(str33)&&str4.equals(str44)&&str5.equals(str55)&&str6.equals(str66)) {
                    successEntityList.get(j).setFailReason("excle中已经有了供应商号、索赔号、定案日期、成本金额、供应商结款发票号、沃尔玛扣款发票号重复的数据");
                    errorEntityList3.add(successEntityList.get(j));
                    successEntityList.get(j).setCreateByName(loginname);
                    paymentInvoiceUploadDao.saveInvoiceFail(successEntityList.get(j));
                    successEntityList.remove(successEntityList.get(j));

                }
            }
        }

            for (int i = successEntityList.size()-1;i>=0;i--){
                PaymentInvoiceUploadEntity red = successEntityList.get(i);
                String supplierAssociation = red.getSupplierAssociation();
                String returnGoodsCode = red.getReturnGoodsCode();
                String returnGoodsDate = red.getReturnGoodsDate();
                String returnCostAmount = red.getReturnCostAmount();
                String paymentInvoiceNo=red.getPaymentInvoiceNo();
                String purchaseInvoiceNo=red.getPurchaseInvoiceNo();
                if(paymentInvoiceUploadDao.selectIsExists(supplierAssociation,returnGoodsCode,returnGoodsDate,returnCostAmount,paymentInvoiceNo,purchaseInvoiceNo)>0){
                    if(red.getMailData()==""||red.getMailData()==null){
                        errorEntityList1.add(red);
                        errorEntityList.add(red);
                        red.setFailReason("系统中已经有了供应商号、索赔号、定案日期、成本金额、供应商结款发票号、沃尔玛扣款发票号重复的数据");
                        red.setCreateByName(loginname);
                        paymentInvoiceUploadDao.saveInvoiceFail(red);
                        successEntityList.remove(red);
                    }
                }

                int count = paymentInvoiceUploadDao.queryreturnGoodsCode(red.getSupplierAssociation(),red.getReturnGoodsCode(),red.getPaymentInvoiceNo(),red.getPurchaseInvoiceNo());
                if(count > 0){
                    if(red.getMailData()!=""&red.getMailData()!=null) {
                        errorEntityList2.add(red);
                        paymentInvoiceUploadDao.inputreturnGoodsCode(red);
                    }
                    successEntityList.remove(red);

                }else {
                    red.setCreateByName(loginname);
//                    successEntityList.add(red);
//                    paymentInvoiceUploadDao.saveInvoice(red);
                }
            }
        List<List<PaymentInvoiceUploadEntity>> splitProtocolList=splitList(successEntityList,100);
        for(List<PaymentInvoiceUploadEntity> list : splitProtocolList ) {
            paymentInvoiceUploadDao.saveInvoice(list);
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        map.put("errorEntityList1",errorEntityList1);
        map.put("errorEntityList2",errorEntityList2);
        map.put("errorEntityList3",errorEntityList3);

        return map;
    }

    @Override
    public int delete(String loginName) {
        return paymentInvoiceUploadDao.delete(loginName);
    }


    @Override
    public int deletefail(Long id) {
      return   paymentInvoiceUploadDao.deletefail(id);
    }

    private static  List<List<PaymentInvoiceUploadEntity>> splitList(List<PaymentInvoiceUploadEntity> sourceList, int  batchCount) {
        List<List<PaymentInvoiceUploadEntity>> returnList =  new ArrayList<>();
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
}
