package com.xforceplus.wapp.modules.redInvoiceManager.service.impl;



import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.MailUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.InputRedTicketInformationDao;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.InvoiceListDao;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InputRedTicketInformationEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarleQueryExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.export.InputRedInvoiceImport;
import com.xforceplus.wapp.modules.redInvoiceManager.export.InputRedTicketInformationExport;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.slf4j.LoggerFactory.getLogger;
import static com.google.common.collect.Maps.newHashMap;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;

@Service
public class InputRedTicketInformationServiceImpl implements InputRedTicketInformationService {
    private static final Logger LOGGER = getLogger(InputRedTicketInformationServiceImpl.class);
    @Autowired
    private InputRedTicketInformationDao inputRedTicketInformationDao;
    @Override
    public List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return inputRedTicketInformationDao.queryList(schemaLabel,map);
    }

    @Override
    public List<UploadScarletLetterEntity> queryListAll(Map<String, Object> map) {
        return inputRedTicketInformationDao.queryListAll(map);
    }
    @Override
    public List<UploadScarletLetterEntity> queryListAllExport(Map<String, Object> map) {
        return inputRedTicketInformationDao.queryListAllExport(map);
    }
    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map) {

        return inputRedTicketInformationDao.queryTotalResult(schemaLabel,map);
    }


    @Override
    public List<InvoiceListEntity> getRedInvoiceList(Map<String, Object> params) {
        return inputRedTicketInformationDao.getRedInvoiceList(params);
    }
    @Override
    public List<InvoiceListEntity> getRedInvoiceList1(Map<String, Object> params) {
        return inputRedTicketInformationDao.getRedInvoiceList1(params);
    }

    @Override
    public Integer getRedInvoiceCount(Map<String, Object> params) {
        return inputRedTicketInformationDao.getRedInvoiceCount(params);
    }
    @Override
    public Integer getRedInvoiceCount1(Map<String, Object> params) {
        return inputRedTicketInformationDao.getRedInvoiceCount1(params);
    }

//    @Override
//    public UploadScarletLetterEntity queryJvCode(String serialNumber) {
//        return inputRedTicketInformationDao.queryJvCode(serialNumber);
//    }
//
//
//    @Override
//    public UploadScarletLetterEntity queryCompanyCode(String jvCode) {
//        return inputRedTicketInformationDao.queryCompanyCode(jvCode);
//    }




    @Override
    public List<OrgEntity> getGfNameAndTaxNo(Long userId) {
        return inputRedTicketInformationDao.getGfNameAndTaxNo(userId);
    }

    @Override
    public RedTicketMatch selectNoticeById(Map<String, Object> params) {
        return inputRedTicketInformationDao.selectNoticeById(params);
    }


    @Override
    public PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String, Object> map) {


        final PagedQueryResult<InvoiceEntity> pagedQueryResult=new PagedQueryResult<>();
        String uuid=(String) map.get("invoiceCode")+(String) map.get("invoiceNo");
        Boolean flag=true;
        map.put("uuid",uuid);
        List<InvoiceEntity> list=inputRedTicketInformationDao.invoiceQueryList(map);
        if(list.size()>0){
            String source=list.get(0).getSystemSource();
            //判断是红票
            if((list.get(0).getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 ) {
                //判断来源
                if (("0".equals(source))) {
                    //采集
                    /*if (!list.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                        pagedQueryResult.setMsg("该发票的购方税号与该供应商的税号不一致！");
                    } else {*/
                    if (list.get(0).getRedNoticeNumber() == null) {
                        // 同步到内部红票表
                        flag = inputRedTicketInformationDao.updateRedNoticeNumber(map) > 0;
                        map.put("invoiceEntity", list.get(0));
                        flag = inputRedTicketInformationDao.saveInvoiceMatchEntity(map) > 0;
                        pagedQueryResult.setMsg("数据已在页面");
                    } else if (!map.get("redNoticeNumber").equals(list.get(0).getRedNoticeNumber()) && !(list.get(0).getRedNoticeNumber()).equals("")) {
                        pagedQueryResult.setMsg("该红票已经和其他红字通知单关联！");
                    } else {
                        pagedQueryResult.setMsg(null);
                    }
                //}
                } else if ("2".equals(source)) {
                    // 录入
                    pagedQueryResult.setMsg(null);
                } else {
                    //查验
                    if (list.get(0).getGfTaxNo() == null || !list.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                        pagedQueryResult.setMsg("该发票的购方税号与库里不一致！");
                    }
                    if (list.get(0).getRedNoticeNumber() == null) {
                        // 同步到内部红票表
                        flag = inputRedTicketInformationDao.updateRedNoticeNumber(map) > 0;
                        map.put("invoiceEntity",list.get(0));
                        flag = inputRedTicketInformationDao.saveInvoiceMatchEntity(map) > 0;
                        pagedQueryResult.setMsg("数据已在页面");
                    } else if (!map.get("redNoticeNumber").equals(list.get(0).getRedNoticeNumber()) && !(list.get(0).getRedNoticeNumber()).equals("")) {
                        pagedQueryResult.setMsg("该红票已经和其他红字通知单关联！");

                    } else {
                        pagedQueryResult.setMsg(null);
                    }
                }
            }else {
                pagedQueryResult.setMsg("该发票不是红票！");
            }
        }
        return pagedQueryResult;
    }
    @Override
    public String getFplx(String invoiceCode) {
        String fplx = "";
        if (invoiceCode.length() == 12) {
            String zero=invoiceCode.substring(0,1);
            String lastTwo=invoiceCode.substring(10,12);
            if("0".equals(zero) && ("04".equals(lastTwo) || "05".equals(lastTwo))){
                fplx="04";
            }

        } else if (invoiceCode.length() == 10) {
            String fplxflag = invoiceCode.substring(7, 8);
            if ("6".equals(fplxflag) || "3".equals(fplxflag)) {
                fplx = "04";
            }
        }
        return fplx;
    }

    @Override
    public PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String, Object> map) {

        final PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        String uuid=(String) map.get("invoiceCode")+(String) map.get("invoiceNo");
        Boolean flag=true;
        List<InvoiceEntity> list= Lists.newArrayList();
        map.put("uuid",uuid);
        List<InvoiceEntity> list1=inputRedTicketInformationDao.invoiceQueryList(map);
        try {
            //判断uuid是否存在
            if (list1.size() == 0 || list1 == null) {
                //不存在
                //录入

                if((new BigDecimal(((String)map.get("invoiceAmount")))).compareTo(BigDecimal.ZERO) < 0 ) {

                    map.put("invoiceType", CommonUtil.getFplx((String) map.get("invoiceCode")));
                    UploadScarletLetterEntity entity = inputRedTicketInformationDao.queryJvCode((String) map.get("serialNumber"));
                    map.put("jvCode",entity.getJvCode());
                    UploadScarletLetterEntity entity1 = inputRedTicketInformationDao.queryCompanyCode(entity.getJvCode());
                    if(entity1==null){
                        entity1 =new UploadScarletLetterEntity();
                        entity1.setCompanyCode("");
                    }
                    map.put("companyCode",entity1.getCompanyCode());

                    //录入到抵账表
                    inputRedTicketInformationDao.saveInvoice(map);
                    // 同步到内部红票表
                    flag = inputRedTicketInformationDao.saveInvoiceMatch(map) > 0;
                }else {
                    poEntityPagedQueryResult.setMsg("不是红票！");
                }
            } else {
                //存在数据
                String source = list1.get(0).getSystemSource();
                //判断是红票
                if((list1.get(0).getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 ){
                    //判断来源
                    if ("0".equals(source)) {
                        //采集

                  /*      if(!list1.get(0).getGfTaxNo().equals(map.get("gfTaxno")) ){
                            poEntityPagedQueryResult.setMsg("该发票的销方税号与当前用户的税号不一致！");
                        }*/

                      if(list1.get(0).getTaxAmount()!=null){
                         if(list1.get(0).getTaxAmount().compareTo(new BigDecimal(map.get("taxAmount").toString()))!=0){
                          poEntityPagedQueryResult.setMsg("抵账表的税额不一致！");
                         }
                      }

                       if(list1.get(0).getInvoiceAmount()!=null){
                          if(list1.get(0).getInvoiceAmount().compareTo(new BigDecimal(map.get("invoiceAmount").toString()))!=0){
                              poEntityPagedQueryResult.setMsg("抵账表的金额不一致！");
                           }
                        }
                        if(list1.get(0).getTaxRate()!=null){
                            if(new BigDecimal(list1.get(0).getTaxRate()).compareTo(new BigDecimal(map.get("taxRate").toString()))!=0){
                                poEntityPagedQueryResult.setMsg("抵账表的税率不一致！");
                            }
                        }
                       if(list1.get(0).getTotalAmount()!=null){
                           if(list1.get(0).getTotalAmount().compareTo(new BigDecimal(map.get("totalAmount").toString()))!=0){
                               poEntityPagedQueryResult.setMsg("抵账表的价税合计不一致！");
                           }
                       }

                        //获取红字通知单号
                        if(list1.get(0).getRedNoticeNumber()!=null){
                            if( list1.get(0).getRedNoticeNumber().equals(map.get("redNoticeNumber"))){
                                poEntityPagedQueryResult.setMsg("红字通知单号已经被关联！");

                            } else if( list1.get(0).getRedNoticeNumber().equals("")){
                                //红字通知单号不存在
                                //1.抵账表插入红字通知单号
                                flag = inputRedTicketInformationDao.updateRedNoticeNumber(map) > 0;
                                //2.同步到匹配表
                                map.put("invoiceEntity",list1.get(0));
                                flag =  inputRedTicketInformationDao.saveInvoiceMatchEntity(map)>0;


                            }else {
                                poEntityPagedQueryResult.setMsg("红字通知单号不一致！");
                            }

                        }
                    } else if ("2".equals(source)) {
                        // 录入
                        //覆盖
                        map.put("invoiceType", CommonUtil.getFplx((String) map.get("invoiceCode")));
                        map.put("invoiceId", list1.get(0).getId());
                        UploadScarletLetterEntity entity = inputRedTicketInformationDao.queryJvCode((String) map.get("serialNumber"));
                        map.put("jvCode",entity.getJvCode());
                        UploadScarletLetterEntity entity1 = inputRedTicketInformationDao.queryCompanyCode(entity.getJvCode());
                        if(entity1==null){
                            entity1 =new UploadScarletLetterEntity();
                            entity1.setCompanyCode("");
                        }
                        map.put("companyCode",entity1.getCompanyCode());
                        //1.覆盖抵账表
                        flag = inputRedTicketInformationDao.allUpdate(map) > 0;
                        //2.同步匹配表
                        flag = inputRedTicketInformationDao.allUpdateMatch(map) > 0;

                    }else if("1".equals(source)){
                        //查验
                        if(list1.get(0).getGfTaxNo()!=null){
                            if(!list1.get(0).getGfTaxNo().equals(map.get("xfTaxno")) ){
                                poEntityPagedQueryResult.setMsg("该发票的销方税号与库里不一致！");
                            }
                        }
                        if(list1.get(0).getTaxAmount()!=null){
                            if(list1.get(0).getTaxAmount().compareTo(new BigDecimal(map.get("taxAmount").toString()))!=0){
                                poEntityPagedQueryResult.setMsg("抵账表的税额不一致！");
                            }
                        }

                        if(list1.get(0).getInvoiceAmount()!=null){
                            if(list1.get(0).getInvoiceAmount().compareTo(new BigDecimal(map.get("invoiceAmount").toString()))!=0){
                                poEntityPagedQueryResult.setMsg("抵账表的金额不一致！");
                            }
                        }
                        if(list1.get(0).getTaxRate()!=null){
                            if(new BigDecimal(list1.get(0).getTaxRate()).compareTo(new BigDecimal(map.get("taxRate").toString()))!=0){
                                poEntityPagedQueryResult.setMsg("抵账表的税率不一致！");
                            }
                        }
                        if(list1.get(0).getTotalAmount()!=null){
                            if(list1.get(0).getTotalAmount().compareTo(new BigDecimal(map.get("totalAmount").toString()))!=0){
                                poEntityPagedQueryResult.setMsg("抵账表的价税合计不一致！");
                            }
                        }
                        //获取红字通知单号
                        if(list1.get(0).getRedNoticeNumber()!=null){
                            if( list1.get(0).getRedNoticeNumber().equals(map.get("redNoticeNumber"))){
                                poEntityPagedQueryResult.setMsg("红字通知单号已经被关联！");

                            } else if( list1.get(0).getRedNoticeNumber().equals("")){
                                //红字通知单号不存在
                                //1.抵账表插入红字通知单号
                                flag = inputRedTicketInformationDao.updateRedNoticeNumber(map) > 0;
                                //2.同步到匹配表
                                map.put("invoiceEntity",list1.get(0));
                                flag =  inputRedTicketInformationDao.saveInvoiceMatchEntity(map)>0;

                            }else {
                                poEntityPagedQueryResult.setMsg("红字通知单号不一致！");
                            }
                        }
                    }
                }else {
                    poEntityPagedQueryResult.setMsg("不是红票！");
                }

            }
        }catch (Exception e){
            throw new RuntimeException();
        }

        return poEntityPagedQueryResult;
    }

    @Override
    public void emptyRedInvoice(Long id) {
        inputRedTicketInformationDao.emptyRedInvoice(id);
    }

    @Override
    public void emptyRecord(String uuid) {
        inputRedTicketInformationDao.emptyRecord(uuid);
    }

    @Override
    public InputRedTicketInformationEntity queryUuid(Long id) {
        return inputRedTicketInformationDao.queryUuid(id);
    }



    @Override
    public RedTicketMatch selectRedTicketById(Map<String, Object> map) {
        return inputRedTicketInformationDao.selectRedTicketById(map);
    }

    @Override
    @Transactional
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {
//        final PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        final InputRedInvoiceImport invoiceImport = new InputRedInvoiceImport(file);
        final Map<String, Object> map = newHashMap();

      try {
        //读取excel
        final List<ImportEntity> redInvoiceList = invoiceImport.analysisExcel();
        if (!redInvoiceList.isEmpty()) {
            map.put("success", Boolean.TRUE);
            Map<String, List<ImportEntity>> entityMap =RedInvoiceImportData(params,redInvoiceList);
            map.put("reason", entityMap.get("successEntityList"));//导入成功的数据集
            map.put("errorEntityList1", entityMap.get("errorEntityList1"));//数据错误
            map.put("errorEntityList2", entityMap.get("errorEntityList2"));//Excel重复的集合
           // map.put("errorEntityList3", entityMap.get("errorEntityList3"));//发票税号和当前登录用户不一致
            map.put("errorEntityList", entityMap.get("errorEntityList"));//数据格式错误
            map.put("errorCount1", entityMap.get("errorEntityList1").size());
            map.put("errorCount2", entityMap.get("errorEntityList2").size());
            //map.put("errorCount3", entityMap.get("errorEntityList3").size());
            map.put("errorCount", entityMap.get("errorEntityList").size());
        } else {
            // LOGGER.info("读取到excel无数据");
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取到excel无数据！");
        }
    } catch (ExcelException e) {
        //LOGGER.error("读取excel文件异常:{}", e);
        map.put("success", Boolean.FALSE);
        map.put("reason", "读取excel文件异常！");
    }
        return map;
    }

    private Map<String,List<ImportEntity>> RedInvoiceImportData(Map<String, Object> params,List<ImportEntity> redInvoiceList) {
        //返回值
        final Map<String, List<ImportEntity>> map = newHashMap();
        //导入成功的数据集
        final List<ImportEntity> successEntityList = new ArrayList<>();
        //导入失败的数据集
        final List<ImportEntity> errorEntityList = new ArrayList<>();
        //储存和库里没有对应的红字通知单号 redTicketMatch
        final List<ImportEntity> errorEntityList1 = new ArrayList<>();
        //Excel重复的集合
        final List<ImportEntity> errorEntityList2 = new ArrayList<>();
        //发票税号和当前登录用户不一致集合
        //final List<ImportEntity> errorEntityList3 = new ArrayList<>();
        Object taxNo=params.get("xfTaxno");
        for  ( int  i  =   0 ; i  <  redInvoiceList.size()  -   1 ; i ++ )  {
            for  ( int  j  =  redInvoiceList.size()  -   1 ; j  >  i; j -- )  {
                String str = "";
                String str2 = "";
                str=redInvoiceList.get(j).getRedNoticeNumber();
                str2=redInvoiceList.get(i).getRedNoticeNumber();
                if  (str.equals(str2))  {
                    errorEntityList2.add(redInvoiceList.get(j));
                    redInvoiceList.remove(j);
                }
            }
        }

        //遍历
        for (ImportEntity importEntity:redInvoiceList){
            String redNoticeNumber = importEntity.getRedNoticeNumber();
            String invoiceAmount = importEntity.getAmount();
            String invoiceCode = importEntity.getInvoiceCode();
            String invoiceNo = importEntity.getInvoiceNo();
            String invoiceDate = importEntity.getInvoiceDate();
            String totalAmount = importEntity.getTotalAmount();
            String taxRate = importEntity.getTaxRate();
            String taxAmount = importEntity.getTaxAmount();

            final Boolean flag = this.checkInvoiceMessage(redNoticeNumber, invoiceCode, invoiceNo, invoiceDate, invoiceAmount, totalAmount, taxRate, taxAmount);

            if(flag) {
                //判断底账表是否有数据
                InvoiceEntity invoiceEntity = inputRedTicketInformationDao.invoiceQueryList1(invoiceCode + invoiceNo);
                if (invoiceEntity != null) {
                    //判断来源
                    //invoiceEntity.
                    //判断红子单号是否为空
                    if (invoiceEntity.getSystemSource().equals("2") ) {
                        int count = inputRedTicketInformationDao.getRedInvoiceCount2(redNoticeNumber);
                        if (count != 0) {

                            ImportEntity entity = inputRedTicketInformationDao.querySerialNumber(importEntity.getRedNoticeNumber());
                            UploadScarletLetterEntity entity1 = inputRedTicketInformationDao.queryJvCode(entity.getSerialNumber());
                            importEntity.setJvcode(entity1.getJvCode());
                            UploadScarletLetterEntity entity2 = inputRedTicketInformationDao.queryCompanyCode(entity1.getJvCode());
                            if(entity2==null){
                                entity2 =new UploadScarletLetterEntity();
                                entity2.setCompanyCode("");
                            }
                            importEntity.setCompanyCode(entity2.getCompanyCode());
                            //覆盖底账表
                            inputRedTicketInformationDao.allUpdateBatchInvoice(importEntity);
                            //录入红票信息表
                            inputRedTicketInformationDao.allUpdateMatchBatch(importEntity);
                            successEntityList.add(importEntity);
                            continue;
                        } else {
                            errorEntityList1.add(importEntity);
                            continue;
                        }
                    }
                    if (invoiceEntity.getRedNoticeNumber()==null && !invoiceEntity.getSystemSource().equals("2")) {
                        //if (!invoiceEntity.getGfTaxNo().equals(taxNo)) {
                          //  errorEntityList3.add(importEntity);
                            //continue;
                       // }else{
                        int count = inputRedTicketInformationDao.getRedInvoiceCount2(redNoticeNumber);
                        if (count != 0) {
                            //抵账表插入红字通知单号
                            inputRedTicketInformationDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), importEntity.getInvoiceCode() + importEntity.getInvoiceNo());
                            //代出 同步匹配表
                            invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                            inputRedTicketInformationDao.saveInvoiceMatchBath(invoiceEntity);
                            successEntityList.add(importEntity);
                            continue;
                        } else {
                            errorEntityList1.add(importEntity);
                            continue;
                        }
                   // }
                    }
                    if (invoiceEntity.getRedNoticeNumber()!=null && !invoiceEntity.getSystemSource().equals("2")) {
                        //if (!invoiceEntity.getGfTaxNo().equals(taxNo)) {
                           // errorEntityList3.add(importEntity);
                           // continue;
                        //}else {
                            int count = inputRedTicketInformationDao.getRedInvoiceCount2(redNoticeNumber);
                            if (count != 0) {
                                //抵账表插入红字通知单号
                                inputRedTicketInformationDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), importEntity.getInvoiceCode() + importEntity.getInvoiceNo());
                                //代出 同步匹配表
                                invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                                inputRedTicketInformationDao.saveInvoiceMatchBath(invoiceEntity);
                                successEntityList.add(importEntity);
                                continue;
                            } else {
                                errorEntityList1.add(importEntity);
                                continue;
                            }
                       // }
                    }
                } else {
                    int count = inputRedTicketInformationDao.getRedInvoiceCount2(redNoticeNumber);
                    if (count != 0) {
                        ImportEntity entity = inputRedTicketInformationDao.querySerialNumber(importEntity.getRedNoticeNumber());
                        UploadScarletLetterEntity entity1 = inputRedTicketInformationDao.queryJvCode(entity.getSerialNumber());
                        importEntity.setJvcode(entity1.getJvCode());
                        UploadScarletLetterEntity entity2 = inputRedTicketInformationDao.queryCompanyCode(entity1.getJvCode());
                        if(entity2==null){
                            entity2 =new UploadScarletLetterEntity();
                            entity2.setCompanyCode("");
                        }
                        importEntity.setCompanyCode(entity2.getCompanyCode());

                        //抵账表插入一条数据 来源为2
                        inputRedTicketInformationDao.insertRedTicketInvoice(importEntity);
                        //同步到匹配表
                        inputRedTicketInformationDao.allUpdateMatchBatch(importEntity);
                        successEntityList.add(importEntity);
                        continue;
                    } else {
                        errorEntityList1.add(importEntity);
                        continue;
                    }
                }
            }else {
//                LOGGER.info("excel数据不能超过500条");
//                map.put("success", Boolean.FALSE);
//                map.put("reason", "excel数据不能超过500条！");
//                return map;
                errorEntityList.add(importEntity);
                continue;
            }
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        map.put("errorEntityList1", errorEntityList1);
        map.put("errorEntityList2", errorEntityList2);
       // map.put("errorEntityList3", errorEntityList3);

        return  map;

    }

    private Boolean checkInvoiceMessage(String redNoticeNumber, String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
        Boolean flag=true;

        if(redNoticeNumber.trim().isEmpty() ||invoiceCode.trim().isEmpty() || invoiceNo.trim().isEmpty() || invoiceDate.trim().isEmpty()|| invoiceAmount.trim().isEmpty() || totalAmount.trim().isEmpty() ||taxRate.trim().isEmpty() || taxAmount.trim().isEmpty()){
            flag=false;
        }

        if(!redNoticeNumber.matches("^[0-9]{16}$")) {
            flag=false;
        }
        if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
            flag=false;
        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
            flag=false;
        }else if(!CommonUtil.isValidNum(taxRate,"^0\\.\\d*[0-9]$")){
                flag=false;
        }
      /*  else if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))){
            flag=false;
        }*/
        else if(!("01".equals(CommonUtil.getFplx(invoiceCode)))){
            flag=false;
        }else if(invoiceDate.length()>10){
            flag=false;
        }
        try {
            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date da=format1.parse(invoiceDate);
        }catch (Exception e){
            flag=false;
        }
        if(flag){
            BigDecimal amount=new BigDecimal(invoiceAmount);
//            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));
            BigDecimal rate=new BigDecimal(taxRate);
            BigDecimal taxAmount1=new BigDecimal(taxAmount);
            BigDecimal totalAmount1=new BigDecimal(totalAmount);
            if(amount.compareTo(new BigDecimal(0))>0){
                flag=false;
            }
            if(taxAmount1.compareTo(new BigDecimal(0))>0){
                flag=false;
            }
            if(totalAmount1.compareTo(new BigDecimal(0))>0){
                flag=false;
            }
            if((amount.add(taxAmount1)).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(totalAmount1.setScale(2, BigDecimal.ROUND_HALF_UP))!=0 &&amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(taxAmount1.setScale(2, BigDecimal.ROUND_HALF_UP))!=0){
                flag=false;
            }
        }
        return flag;

    }
    public  Map<String, Object> sendEmail(String date1,String date2,File fi){
        Map<String, Object> map = newHashMap();
        Date de=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        String time=sdf.format(de);
        String toEmail=inputRedTicketInformationDao.getCopyPerson("RECIPIENTS");
        String titel="JVCHC红票通知单传递清单-退货"+time;
        String message="尊敬的税务组你好："+date1+"至"+"date2"+"的JVCHC待开红票清单已经生成，请登录〈Q:\\AP-Superctacct\\AP\\JVCHC待开红票〉路径下获取并及时开具红字发票。友情提示，已开具的红票请于当月20日之前递交实物至GFR Team以便当月完成匹配及相关跟进。";
        try{
            MailUtils.sendEmailWithAttachment(toEmail,"","",titel,message,fi);
            map.put("success", "yes");
            fi.delete();
        }catch (Exception e){
            map.put("success", "no");
            map.put("reason", "发送失败！");
            e.printStackTrace();
        }

        return map;
    }
    @Override
    public List<UploadScarleQueryExcelEntity> toExcel(List<UploadScarletLetterEntity> list){
        List<UploadScarleQueryExcelEntity> list2=new ArrayList<>();
        for (UploadScarletLetterEntity ue:list) {
            UploadScarleQueryExcelEntity uq=new UploadScarleQueryExcelEntity();
            uq.setRownumber(ue.getRownumber());
            uq.setStore(ue.getStore());
            uq.setBuyerName(ue.getBuyerName());
            uq.setInvoiceType(ue.getInvoiceType());
            uq.setInvoiceAmount(ue.getInvoiceAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setTaxRate(ue.getTaxRate().setScale(2, BigDecimal.ROUND_UP).toString()+"%");
            uq.setTaxAmount(ue.getTaxAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setJvcode(ue.getJvCode());
            uq.setMakeoutDate(ue.getMakeoutDate());
            uq.setSerialNumber(ue.getSerialNumber());
            uq.setRedLetterNotice(ue.getRedLetterNotice());
            list2.add(uq);
        }
        return list2;
    }
}

