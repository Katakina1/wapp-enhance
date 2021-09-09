package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.dao.EntryRedTicketDao;
import com.xforceplus.wapp.modules.redTicket.dao.QueryOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.export.InvoiceImport;
import com.xforceplus.wapp.modules.redTicket.service.EntryRedTicketService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/10/27 10:06
 */
@Service
public class EntryRedTicketServiceImpl implements EntryRedTicketService {
    private static final Logger LOGGER = getLogger(EntryRedTicketServiceImpl.class);
    @Autowired
    private QueryOpenRedTicketDataDao queryOpenRedTicketDataDao;
    private EntryRedTicketDao entryRedTicketDao;

    @Autowired
    public EntryRedTicketServiceImpl(EntryRedTicketDao entryRedTicketDao) {
        this.entryRedTicketDao = entryRedTicketDao;
    }

    @Override
    public List<RedTicketMatch> selectRedTicketList(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches = entryRedTicketDao.selectRedTicketList(map);
        for (int i = 0; i < redTicketMatches.size(); i++){
            if(redTicketMatches.get(i).getBusinessType().equals("2")){
                BigDecimal taxRate =(redTicketMatches.get(i).getTaxRate()).divide(new BigDecimal(100));
                taxRate = taxRate.add(new BigDecimal(1));
                redTicketMatches.get(i).setRedTotalAmount(redTicketMatches.get(i).getRedTotalAmount().multiply(taxRate));
            }
        }
        return redTicketMatches;
    }

    @Override
    public Integer selectRedTicketListCount(Map<String, Object> map) {
        return entryRedTicketDao.selectRedTicketListCount(map);
    }

    @Override
    public List<OrgEntity> getGfNameAndTaxNo(Long userId) {
        return entryRedTicketDao.getGfNameAndTaxNo(userId);
    }

    @Override @Transactional
    public PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String, Object> map) {
        String businessType = (String) map.get("businessType");
        BigDecimal redTotalAmount = new BigDecimal(map.get("redTotalAmount").toString());

        String gfTaxNo = (String) map.get("gfTaxNo");
        String xfTaxNo = (String) map.get("xfTaxNo");
        String redTicketDataSerialNumber = (String) map.get("redTicketDataSerialNumber");
        String redNoticeNumber = (String) map.get("redNoticeNumber");
        String taxRate1 =  map.get("taxRate1").toString();

        final PagedQueryResult<InvoiceEntity> pagedQueryResult=new PagedQueryResult<>();
        String uuid=(String) map.get("invoiceCode")+(String) map.get("invoiceNo");
        map.put("uuid",uuid);
        List<InvoiceEntity> list=entryRedTicketDao.invoiceQueryList(map);

        boolean flag;
        if(list.size()>0) {
            InvoiceEntity invoiceEntity = list.get(0);
            map.put("InvoiceEntity", invoiceEntity);
            String source = invoiceEntity.getSystemSource();
            //判断是红票redTotalAmount
            if (!invoiceEntity.getInvoiceType().equals("01")) {
                pagedQueryResult.setMsg("该发票不是专票");
            } else {
                if ((invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 && (businessType.equals("1") || businessType.equals("3"))) {
                    //判断来源
                    if ("0".equals(source) ) {
                        if( invoiceEntity.getInvoiceAmount().abs().compareTo(redTotalAmount) == 0){
                            //采集
                            if(invoiceEntity.getTaxRate().equals(taxRate1)){
                            if (!invoiceEntity.getGfTaxNo().equals(gfTaxNo) && !xfTaxNo.equals(invoiceEntity.getXfTaxNo())) {
                                pagedQueryResult.setMsg("该发票的购方税号不一致！ 或者销方税号不一致");
                            } else {
                                if (invoiceEntity.getRedNoticeNumber() == null ||invoiceEntity.getRedNoticeNumber().isEmpty() ) {
                                    //红字通知单号没有
                                    //同步匹配表
                                    flag = entryRedTicketDao.insertRedInvoiceMatch(map) > 0;
                                    //给抵账表插入红字通知单
                                    entryRedTicketDao.insertRedNoticeNumber(map);

                                    if (businessType.equals("1")) {
                                        //修改协议状态
                                        flag = entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber) > 0;
                                    }
                                    pagedQueryResult.setMsg("该发票已经代出");
                                } else {
                                    pagedQueryResult.setMsg("该红票已被关联");
                                }
                            }
                            }else {
                                pagedQueryResult.setMsg("该红票的税率与带出的发票的税率不符");
                            }

                        }else {
                            pagedQueryResult.setMsg("该红票的红冲总金额与带出的发票金额不符");
                        }

                    } else if ("2".equals(source)) {
                        // 录入
                        pagedQueryResult.setMsg(null);
                    } else if( "1".equals(source)){
                        //查验
                        if(invoiceEntity.getInvoiceAmount().abs().compareTo(redTotalAmount) == 0){
                            if(invoiceEntity.getTaxRate().equals(taxRate1)){
                                if (!invoiceEntity.getGfTaxNo().equals(gfTaxNo) && !xfTaxNo.equals(invoiceEntity.getXfTaxNo())) {
                                    pagedQueryResult.setMsg("该发票的购方税号与库里不一致！或者销方税号不一致");
                                } else {
                                    if (invoiceEntity.getRedNoticeNumber() == null ||invoiceEntity.getRedNoticeNumber().isEmpty() ) {
                                        flag = entryRedTicketDao.insertRedInvoiceMatch(map) > 0;
                                        if (businessType.equals("1")) {
                                            //修改退货状态
                                            entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                                        }
                                        entryRedTicketDao.insertRedNoticeNumber(map);
                                        pagedQueryResult.setMsg("该发票已经代出");
                                    } else {
                                        pagedQueryResult.setMsg("该红票已被关联");
                                    }

                                }
                            }else {
                                pagedQueryResult.setMsg("该红票的税率与带出的发票的税率不符");
                            }
                        }else {
                            pagedQueryResult.setMsg("该红票的红冲总金额于带出的发票金额不符");
                        }

                    }
                } else if ((invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 && (businessType.equals("2"))) {
                    //判断来源
                    if (("0".equals(source))) {
                        if(invoiceEntity.getTotalAmount().abs().compareTo(redTotalAmount) == 0){
                            if(invoiceEntity.getTaxRate().equals(taxRate1)){
                                 //采集
                                if (!invoiceEntity.getGfTaxNo().equals(gfTaxNo) && !xfTaxNo.equals(invoiceEntity.getXfTaxNo())) {
                                    pagedQueryResult.setMsg("该发票的购方税号与该供应商的购方税号不一致！或者销方税号不一致 ");
                                } else {

                                    if (invoiceEntity.getRedNoticeNumber() == null ||invoiceEntity.getRedNoticeNumber().isEmpty() ) {
                                        //红字通知单号没有
                                        pagedQueryResult.setMsg("该发票已经代出");
                                        //同步匹配表
                                        flag = entryRedTicketDao.insertRedInvoiceMatch(map) > 0;
                                        entryRedTicketDao.insertRedNoticeNumber(map);
                                        //修改协议状态
                                        entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                                    } else {
                                        pagedQueryResult.setMsg("该红票已被关联");
                                    }
                                }
                            }else {
                                pagedQueryResult.setMsg("该红票的税率与带出的发票的税率不符");
                            }
                        }else {
                            pagedQueryResult.setMsg("该红票的红冲总金额与带出的发票金额不符");
                        }

                    } else if ("2".equals(source)) {
                        // 录入
                        pagedQueryResult.setMsg(null);
                    } else if("1".equals(source)){
                        if(invoiceEntity.getTotalAmount().abs().compareTo(redTotalAmount) == 0){
                            if(invoiceEntity.getTaxRate().equals(taxRate1)){
                        //查验
                        if (invoiceEntity.getGfTaxNo().equals(gfTaxNo) && !xfTaxNo.equals(invoiceEntity.getXfTaxNo())) {
                            pagedQueryResult.setMsg("该发票的购方税号与库里不一致！或者销方税号不一致");
                        } else {
                            if (invoiceEntity.getRedNoticeNumber() == null ||invoiceEntity.getRedNoticeNumber().isEmpty()) {
                                entryRedTicketDao.insertRedInvoiceMatch(map);
                                //改抵账表插入红字通知单号
                                entryRedTicketDao.insertRedNoticeNumber(map);
                                //修改协议状态
                                entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                                pagedQueryResult.setMsg("该发票已经代出");
                            } else {
                                pagedQueryResult.setMsg("该红票已被关联");
                            }
                        }
                            }else {
                                pagedQueryResult.setMsg("该红票的税率与带出的发票的税率不符");
                            }
                        }else {
                            pagedQueryResult.setMsg("该红票的红冲总金额与带出的发票金额不符");
                        }
                    }
                } else {
                    pagedQueryResult.setMsg("该发票不是红票！");
                }
            }
        }

        return pagedQueryResult;
    }



    private int saveRedNoticeNumber(String redNoticeNumber, String uuid ,String jvcode, String companyCode) {
        return entryRedTicketDao.saveRedNoticeNumber(redNoticeNumber,uuid, jvcode, companyCode);
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

    @Override @Transactional
    public PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String, Object> map) {

        final PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        String uuid=(String) map.get("invoiceCode")+(String) map.get("invoiceNo");
        Boolean flag=true;
        map.put("uuid",uuid);
        List<InvoiceEntity> list1=entryRedTicketDao.invoiceQueryList(map);

        String businessType = (String) map.get("businessType");
        BigDecimal redTotalAmount = new BigDecimal(map.get("redTotalAmount").toString()) ;
        BigDecimal taxRate =  new BigDecimal(map.get("taxRate").toString()).divide(new BigDecimal(100));
        String redTicketDataSerialNumber = (String) map.get("redTicketDataSerialNumber");
        String redNoticeNumber = (String) map.get("redNoticeNumber");
        try {
            if (list1.size() == 0 || list1 == null) {
                //不存在
                //录入
                //获取类型
                map.put("invoiceType",CommonUtil.getFplx((String)map.get("invoiceCode")) ) ;

                if(((String)map.get("flag1")).equals("a")){
                    //红字通知单号清空
                    flag = entryRedTicketDao.updateRed(map) > 0;
                }
                //录入到抵账表
                flag = entryRedTicketDao.saveInvoice(map) > 0;
                // 同步到匹配表
                flag = entryRedTicketDao.saveInvoiceMatch(map) > 0;
                if(businessType.equals("2")){
                    //修改协议状态
                    entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                }
                if(businessType.equals("1")){
                    //修改协议状态
                    entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                }
                if(flag){
                    poEntityPagedQueryResult.setMsg("SUCCESS");
                }

            } else {
                //存在数据
                InvoiceEntity invoiceEntity = list1.get(0);
                map.put("InvoiceEntity", invoiceEntity);
                String source = invoiceEntity.getSystemSource();
                //判断是红票
                if ((invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 && (businessType.equals("1") || businessType.equals("3"))
                        && invoiceEntity.getInvoiceAmount().abs().compareTo(redTotalAmount) == 0) {
                    //判断来源
                    if ("0".equals(source)) {
                        //采集
                        //获取红字通知单号
                        if (invoiceEntity.getRedNoticeNumber()==null) {
                            if (((String) map.get("flag1")).equals("a")) {
                                //红字通知单号清空
                                flag = entryRedTicketDao.updateRed(map) > 0;
                            }
                            //红字通知单号不存在
                            //1.抵账表插入红字通知单号
                            flag = entryRedTicketDao.updateRedNoticeNumber(map) > 0;
                            //2.同步到匹配表
                            flag = entryRedTicketDao.saveInvoiceMatchEntity(map) > 0;
                            if(businessType.toString().equals("1")){
                                //修改退货状态
                                entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");
                        }else if (invoiceEntity.getRedNoticeNumber()!=null &&invoiceEntity.getRedNoticeNumber().equals(redNoticeNumber)) {

                            //同步匹配表
                            entryRedTicketDao.insertRedInvoiceMatch(map);
                            if(businessType.equals("1")){
                                //修改退货状态
                                entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");
                        } else {
                            poEntityPagedQueryResult.setMsg("error");
                        }

                    } else if ("2".equals(source)) {
                        if (((String) map.get("flag1")).equals("a")) {
                            //红字通知单号清空
                            flag = entryRedTicketDao.updateRed(map) > 0;
                        }
                        // 录入
                        //覆盖
                        map.put("invoiceId", invoiceEntity.getId());
                        //1.覆盖抵账表
                        flag = entryRedTicketDao.allUpdate(map) > 0;
                        //2.同步匹配表
                        flag = entryRedTicketDao.allUpdateMatch(map) > 0;
                        if(businessType.equals("1")){
                            //修改退货状态
                            entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                        }
                        poEntityPagedQueryResult.setMsg("SUCCESS");

                    } else if ("1".equals(source)) {
                        //查验
                        //获取红字通知单号
                         if (invoiceEntity.getRedNoticeNumber()==null) {
                            if (((String) map.get("flag1")).equals("a")) {
                                //红字通知单号清空
                                flag = entryRedTicketDao.updateRed(map) > 0;
                            }

                            //红字通知单号不存在
                            //1.抵账表插入红字通知单号
                            flag = entryRedTicketDao.updateRedNoticeNumber(map) > 0;
                            //2.同步到匹配表
                            flag = entryRedTicketDao.saveInvoiceMatchEntity(map) > 0;
                            if(businessType.equals("1")){
                                //修改协议状态
                                entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");
                        } else if (invoiceEntity.getRedNoticeNumber().equals(redNoticeNumber)) {
                            //同步匹配表
                            entryRedTicketDao.insertRedInvoiceMatch(map);

                            if(businessType.equals("1")){
                                //修改退货状态
                                entryRedTicketDao.updateRuteStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");

                        } else {
                            poEntityPagedQueryResult.setMsg("error");
                        }
                    }
                } else if((invoiceEntity.getInvoiceAmount()).compareTo(BigDecimal.ZERO) < 0 && (businessType.equals("2")  )
                        && redTotalAmount.compareTo(invoiceEntity.getTotalAmount().setScale(2).abs())==0 ){
                    //判断来源
                    if ("0".equals(source)) {
                        //采集
                        //获取红字通知单号
                        if( invoiceEntity.getRedNoticeNumber()==null){
                            if(((String)map.get("flag1")).equals("a")){
                                //红字通知单号清空
                                flag = entryRedTicketDao.updateRed(map) > 0;
                            }
                            //红字通知单号不存在"redNoticeNumber" -> "2233445566778288"
                            //1.抵账表插入红字通知单号
                            flag = entryRedTicketDao.updateRedNoticeNumber(map) > 0;
                            //2.同步到匹配表
                            flag =  entryRedTicketDao.saveInvoiceMatchEntity(map)>0;

                            if(businessType.equals("2")){
                                //修改协议状态
                                entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");
                        }else if( invoiceEntity.getRedNoticeNumber().equals(redNoticeNumber)){
                            poEntityPagedQueryResult.setMsg("该发票已经代出");
                            //同步匹配表
                            entryRedTicketDao.insertRedInvoiceMatch(map);

                            if(businessType.equals("2")){
                                //修改协议状态
                                entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");

                        } else {
                            poEntityPagedQueryResult.setMsg("error");
                        }

                    } else if ("2".equals(source)) {
                        if(((String)map.get("flag1")).equals("a")){
                            //红字通知单号清空
                            flag = entryRedTicketDao.updateRed(map) > 0;
                        }
                        // 录入
                        //覆盖
                        map.put("invoiceId", invoiceEntity.getId());
                        //1.覆盖抵账表
                        flag = entryRedTicketDao.allUpdate(map) > 0;
                        //2.同步匹配表
                        flag = entryRedTicketDao.allUpdateMatch(map) > 0;
                        if(businessType.equals("2")){
                            //修改协议状态
                            entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                        }
                        poEntityPagedQueryResult.setMsg("SUCCESS");

                    }else if("1".equals(source)){
                        //查验
                        //获取红字通知单号
                        if( invoiceEntity.getRedNoticeNumber()==null){
                            if(((String)map.get("flag1")).equals("a")){
                                //红字通知单号清空
                                flag = entryRedTicketDao.updateRed(map) > 0;
                            }

                            //红字通知单号不存在
                            //1.抵账表插入红字通知单号
                            flag = entryRedTicketDao.updateRedNoticeNumber(map) > 0;
                            //2.同步到匹配表
                            flag =  entryRedTicketDao.saveInvoiceMatchEntity(map)>0;
                            if(businessType.equals("2")){
                                //修改协议状态
                                entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");
                        } else if( invoiceEntity.getRedNoticeNumber().equals(redNoticeNumber)){
                            //同步匹配表
                            entryRedTicketDao.insertRedInvoiceMatch(map);
                            if(businessType.equals("2")){
                                //修改协议状态
                                entryRedTicketDao.updateProcloStatu(redTicketDataSerialNumber);
                            }
                            poEntityPagedQueryResult.setMsg("SUCCESS");

                        } else {
                            poEntityPagedQueryResult.setMsg("error");
                        }
                    }
                }else {
                    poEntityPagedQueryResult.setMsg("红票信息有误！");
                }
            }
        }catch (Exception e){
            throw new RuntimeException();
        }

        return poEntityPagedQueryResult;
    }

    @Override
    public RedTicketMatch selectRedTicketById(Map<String, Object> map) {
        return entryRedTicketDao.selectRedTicketById(map);
    }

    @Override @Transactional
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {

        //进入解析excel方法
        final InvoiceImport invoiceImport = new InvoiceImport(file);
        final Map<String, Object> map = newHashMap();

        try {
            //读取excel
            final List<ImportEntity> redInvoiceList = invoiceImport.analysisExcel();
            if (redInvoiceList.size()!=0) {
                map.put("success", Boolean.TRUE);
                Map<String, List<ImportEntity>> entityMap =RedInvoiceImportData(params,redInvoiceList);
                map.put("reason", entityMap.get("successEntityList"));//导入成功的数据集
                map.put("errorEntityList1", entityMap.get("errorEntityList1"));//数据错误
                map.put("errorEntityList2", entityMap.get("errorEntityList2"));//Excel重复的集合
                map.put("errorEntityList3", entityMap.get("errorEntityList3"));//金额错误的集合
                map.put("errorEntityList", entityMap.get("errorEntityList"));//数据格式错误
                map.put("errorEntityList4", entityMap.get("errorEntityList4"));//匹配表没有对样的数据集合
                map.put("errorCount1", entityMap.get("errorEntityList1").size());
                map.put("errorCount2", entityMap.get("errorEntityList2").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
                map.put("errorCount3", entityMap.get("errorEntityList3").size());
                map.put("errorCount4", entityMap.get("errorEntityList4").size());
                if(entityMap.get("errorEntityList5")!=null){
                    map.put("errorCount5", entityMap.get("errorEntityList5").size());
                }else {
                    map.put("errorCount5", 0);
                }

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
        //excel格式不全的
        final List<ImportEntity> errorEntityList = new ArrayList<>();
        //税号不对应
        final List<ImportEntity> errorEntityList1 = new ArrayList<>();
        //Excel重复的集合
        final List<ImportEntity> errorEntityList2 = new ArrayList<>();
        //金额有问题的 信息
        final List<ImportEntity> errorEntityList3 = new ArrayList<>();
        //匹配表不存在的信息
        final List<ImportEntity> errorEntityList4 = new ArrayList<>();




        //1.去重
        //去重 红字通知单号 代号
        if(redInvoiceList.size()<=500){
            for  ( int  i  =   redInvoiceList.size()  -   1 ; i  >=0; i -- )  {
                String str2=redInvoiceList.get(i).getRedNoticeNumber();
                String str4=redInvoiceList.get(i).getInvoiceCode()+redInvoiceList.get(i).getInvoiceNo();
                for  ( int  j  =  i  -   1 ; j  >=  0; j -- )  {
                    String str=redInvoiceList.get(j).getRedNoticeNumber();
                    String str3=redInvoiceList.get(j).getInvoiceCode()+redInvoiceList.get(j).getInvoiceNo();
                    if  (str.equals(str2) || str3.equals(str4))  {
                        errorEntityList2.add(redInvoiceList.get(j));
                        redInvoiceList.remove(j);
                    }
                }
            }

            //遍历
            for(ImportEntity importEntity :redInvoiceList) {
                String redNoticeNumber = importEntity.getRedNoticeNumber();
                String invoiceAmount = importEntity.getAmount();
                String invoiceCode = importEntity.getInvoiceCode();
                String invoiceNo = importEntity.getInvoiceNo();
                String invoiceDate = importEntity.getInvoiceDate();
                String taxAmount = importEntity.getTaxAmount();;
                String taxRate = importEntity.getTaxRate();
                String totalAmount = importEntity.getTotalAmount();

                final Boolean flag = this.checkInvoiceMessage(redNoticeNumber,invoiceCode, invoiceNo, invoiceDate, invoiceAmount, totalAmount, taxRate, taxAmount);
                final Boolean flag1 = this.checkInvoiceData(redNoticeNumber,invoiceCode, invoiceNo, invoiceDate, invoiceAmount, totalAmount, taxRate, taxAmount);



                //数据是否完整
                if(flag){
                    //金额有错误
                    if(flag1){
                        RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);
                        if(redTicketMatch!=null){
                            importEntity.setGfTaxNo(redTicketMatch.getGfTaxNo());
                            importEntity.setXfTaxNo(params.get("xfTaxNo").toString());
                            importEntity.setJvcode(redTicketMatch.getJvcode());
                            importEntity.setCompanyCode(redTicketMatch.getCompanyCode());
                            //判断抵账有这条数据吗
                            InvoiceEntity invoiceEntity=entryRedTicketDao.getInvoiceQuery(invoiceCode+invoiceNo);
                            if(invoiceEntity !=null){
                                //判断来源
                                //invoiceEntity.
                                //判断红子单号是否为空
                                if(invoiceEntity.getSystemSource().equals("2") &&!StringUtils.isEmpty(invoiceEntity.getRedNoticeNumber()) ){
                                    //RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);


                                    if(params.get("xfTaxNo").toString().equals(invoiceEntity.getXfTaxNo()) &&
                                            redTicketMatch.getGfTaxNo().equals(invoiceEntity.getGfTaxNo())){
                                        //判断红冲总金额
                                        //红字通知单号清空

                                        params.put("redNoticeNumber",redNoticeNumber);
                                        entryRedTicketDao.updateRed(params);
                                        //清空抵账redNoticeNumber
                                        BigDecimal amount = new BigDecimal(invoiceAmount);
                                   /* BigDecimal rate = new BigDecimal(taxRate).divide(new BigDecimal(100));
                                    BigDecimal taxAmount1 = new BigDecimal(taxAmount);*/
                                        BigDecimal totalAmount1 = new BigDecimal(totalAmount);
                                        //分类型 索赔 协议 折让
                                        if(redTicketMatch.getBusinessType().equals("1")|| redTicketMatch.getBusinessType().equals("3")){
                                            if(amount.abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //1.覆盖抵账表
                                                entryRedTicketDao.allUpdateBatchInvoice(importEntity);
                                                //2.同步匹配表
                                                entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                                //修改状态
                                                if(redTicketMatch.getBusinessType().equals("1")){
                                                    //修改退货状态
                                                    entryRedTicketDao.updateRuteStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                                }

                                                successEntityList.add(importEntity);
                                            }else {
                                                errorEntityList3.add(importEntity);
                                            }
                                        }
                                        if(redTicketMatch.getBusinessType().equals("2")){
                                            if(amount.abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //1.覆盖抵账表
                                                entryRedTicketDao.allUpdateBatchInvoice(importEntity);
                                                //2.同步匹配表
                                                entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                                //修改协议状态
                                                entryRedTicketDao.updateProcloStatu(redTicketMatch.getRedTicketDataSerialNumber());

                                                successEntityList.add(importEntity);
                                            }else {
                                                //金额不匹配
                                                errorEntityList3.add(importEntity);
                                            }
                                        }

                                    }else {
                                        //税号不对应
                                        errorEntityList1.add(importEntity);

                                    }

                                }
                                if(invoiceEntity.getSystemSource().equals("2") &&StringUtils.isEmpty(invoiceEntity.getRedNoticeNumber()) ){
                                    //RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);


                                    if(params.get("xfTaxNo").toString().equals(invoiceEntity.getXfTaxNo()) &&
                                            redTicketMatch.getGfTaxNo().equals(invoiceEntity.getGfTaxNo())){
                                        //判断红冲总金额
                                        //红字通知单号清空

                                        params.put("redNoticeNumber",redNoticeNumber);
                                       // entryRedTicketDao.updateRed(params);
                                        //清空抵账redNoticeNumber
                                        BigDecimal amount = new BigDecimal(invoiceAmount);
                                   /* BigDecimal rate = new BigDecimal(taxRate).divide(new BigDecimal(100));
                                    BigDecimal taxAmount1 = new BigDecimal(taxAmount);*/
                                        BigDecimal totalAmount1 = new BigDecimal(totalAmount);
                                        //分类型 索赔 协议 折让
                                        if(redTicketMatch.getBusinessType().equals("1")|| redTicketMatch.getBusinessType().equals("3")){
                                            if(amount.abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //1.覆盖抵账表
                                                entryRedTicketDao.allUpdateBatchInvoice(importEntity);
                                                //2.同步匹配表
                                                entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                                //修改状态
                                                if(redTicketMatch.getBusinessType().equals("1")){
                                                    //修改退货状态
                                                    entryRedTicketDao.updateRuteStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                                }

                                                successEntityList.add(importEntity);
                                            }else {
                                                errorEntityList3.add(importEntity);
                                            }
                                        }
                                        if(redTicketMatch.getBusinessType().equals("2")){
                                            if(amount.abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //1.覆盖抵账表
                                                entryRedTicketDao.allUpdateBatchInvoice(importEntity);
                                                //2.同步匹配表
                                                entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                                //修改协议状态
                                                entryRedTicketDao.updateProcloStatu(redTicketMatch.getRedTicketDataSerialNumber());

                                                successEntityList.add(importEntity);
                                            }else {
                                                //金额不匹配
                                                errorEntityList3.add(importEntity);
                                            }
                                        }

                                    }else {
                                        //税号不对应
                                        errorEntityList1.add(importEntity);

                                    }

                                }
                                if(StringUtils.isEmpty(invoiceEntity.getRedNoticeNumber()) && !invoiceEntity.getSystemSource().equals("2")){
                                    // RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);
                                    if(params.get("xfTaxNo").toString().equals(invoiceEntity.getXfTaxNo())
                                            &&redTicketMatch.getGfTaxNo().equals(invoiceEntity.getGfTaxNo())) {
                                        if(redTicketMatch.getBusinessType().equals("1")|| redTicketMatch.getBusinessType().equals("3")){
                                            if(invoiceEntity.getInvoiceAmount().abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //抵账表插入红字通知单号
                                                entryRedTicketDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), importEntity.getInvoiceCode() + importEntity.getInvoiceNo(),redTicketMatch.getJvcode(),redTicketMatch.getCompanyCode());
                                                //代出 同步匹配表
                                                invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                                                entryRedTicketDao.saveInvoiceMatchBath(invoiceEntity);

                                                if(redTicketMatch.getBusinessType().equals("1")){
                                                    //修改退货状态
                                                    entryRedTicketDao.updateRuteStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                                }

                                                successEntityList.add(importEntity);
                                            }else {
                                                //金额不匹配
                                                errorEntityList3.add(importEntity);
                                            }
                                        } else if(redTicketMatch.getBusinessType().equals("2")){
                                            if(invoiceEntity.getInvoiceAmount().abs().compareTo(redTicketMatch.getRedTotalAmount())==0){
                                                //抵账表插入红字通知单号
                                                entryRedTicketDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), importEntity.getInvoiceCode() + importEntity.getInvoiceNo(),redTicketMatch.getJvcode(),redTicketMatch.getCompanyCode());
                                                //代出 同步匹配表
                                                invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                                                entryRedTicketDao.saveInvoiceMatchBath(invoiceEntity);
                                                //修改协议状态
                                                entryRedTicketDao.updateProcloStatu(redTicketMatch.getRedTicketDataSerialNumber());

                                                successEntityList.add(importEntity);
                                            }else {
                                                //金额不匹配
                                                errorEntityList3.add(importEntity);
                                            }
                                        }
                                    }else {
                                        errorEntityList1.add(importEntity);
                                    }
                                } else if(!StringUtils.isEmpty(invoiceEntity.getRedNoticeNumber()) && !invoiceEntity.getSystemSource().equals("2")){
                                    // RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);
                                    if( params.get("xfTaxNo").toString().equals(invoiceEntity.getXfTaxNo())
                                            && redTicketMatch.getGfTaxNo().equals(invoiceEntity.getGfTaxNo())) {
                                        if(redTicketMatch.getBusinessType().equals("1")|| redTicketMatch.getBusinessType().equals("3")) {
                                            if (invoiceEntity.getInvoiceAmount().abs().compareTo(redTicketMatch.getRedTotalAmount()) == 0&invoiceEntity.getTaxRate().equals(redTicketMatch.getTaxRate())) {
                                                //1.修改抵账表之前红字通知单号
                                                params.put("redNoticeNumber",redNoticeNumber);
                                                entryRedTicketDao.updateRed(params);
                                                entryRedTicketDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), invoiceEntity.getUuid(),redTicketMatch.getJvcode(),redTicketMatch.getCompanyCode());
                                                //代出 同步匹配表
                                                invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                                                entryRedTicketDao.saveInvoiceMatchBath(invoiceEntity);
                                                if(redTicketMatch.getBusinessType().equals("1")){
                                                    //修改退货状态
                                                    entryRedTicketDao.updateRuteStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                                }

                                                successEntityList.add(importEntity);
                                            }else {
                                                //金额不匹配
                                                errorEntityList3.add(importEntity);
                                            }
                                        } else if(redTicketMatch.getBusinessType().equals("2")){
                                            BigDecimal amount=redTicketMatch.getRedTotalAmount().multiply(redTicketMatch.getTaxRate().divide(new BigDecimal(100)));
                                            if(invoiceEntity.getInvoiceAmount().abs().compareTo(amount)==0){
                                                //1.修改抵账表之前红字通知单号
                                                params.put("redNoticeNumber",redNoticeNumber);
                                                entryRedTicketDao.updateRed(params);
                                                entryRedTicketDao.saveRedNoticeNumber(importEntity.getRedNoticeNumber(), invoiceEntity.getUuid(),redTicketMatch.getJvcode(),redTicketMatch.getCompanyCode());
                                                //代出 同步匹配表
                                                invoiceEntity.setRedNoticeNumber(importEntity.getRedNoticeNumber());
                                                entryRedTicketDao.saveInvoiceMatchBath(invoiceEntity);

                                                //修改协议状态
                                                entryRedTicketDao.updateProcloStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                                successEntityList.add(importEntity);
                                            }
                                        }else {
                                            //金额不匹配
                                            errorEntityList3.add(importEntity);
                                        }
                                    }else {
                                        errorEntityList1.add(importEntity);
                                    }
                                }
                            }else {
                                //校验金额
                                //录入
                                //RedTicketMatch redTicketMatch =entryRedTicketDao.getRedNoticeMatch(redNoticeNumber);
                                if (redTicketMatch.getBusinessType().equals("1") || redTicketMatch.getBusinessType().equals("3")) {
                                    if ((new BigDecimal(invoiceAmount)).abs().compareTo(redTicketMatch.getRedTotalAmount()) == 0) {
                                        //抵账表插入一条数据 来源为2
                                        entryRedTicketDao.insertRedTicketInvoice(importEntity);
                                        //同步到匹配表
                                        entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                        //修改索赔状态
                                        if(redTicketMatch.getBusinessType().equals("1")){
                                            entryRedTicketDao.updateRuteStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                        }
                                        successEntityList.add(importEntity);
                                    }else {
                                        //金额错误
                                        errorEntityList3.add(importEntity);
                                    }
                                } else if(redTicketMatch.getBusinessType().equals("2")) {
                                    if ((new BigDecimal(invoiceAmount)).abs().compareTo(redTicketMatch.getRedTotalAmount()) == 0) {
                                        //抵账表插入一条数据 来源为2
                                        entryRedTicketDao.insertRedTicketInvoice(importEntity);
                                        //同步到匹配表
                                        entryRedTicketDao.allUpdateMatchBatch(importEntity);
                                        //修改协议状态
                                        entryRedTicketDao.updateProcloStatu(redTicketMatch.getRedTicketDataSerialNumber());
                                        successEntityList.add(importEntity);
                                    }else{
                                        errorEntityList3.add(importEntity);
                                    }

                                }

                            }
                        }else {
                            errorEntityList4.add(importEntity);
                        }
                    }else {
                        errorEntityList3.add(importEntity);
                    }
                }else {
                    errorEntityList.add(importEntity);
                }
            };
        }else {
            map.put("errorEntityList5", redInvoiceList);
        }

        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        map.put("errorEntityList1", errorEntityList1);
        map.put("errorEntityList2", errorEntityList2);
        map.put("errorEntityList3", errorEntityList3);
        map.put("errorEntityList4", errorEntityList4);

        return  map;
    }

    private Boolean checkInvoiceMessage(String redNoticeNumber, String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
        Boolean flag=true;
        if(redNoticeNumber.trim().isEmpty() ||invoiceCode.trim().isEmpty() || invoiceNo.trim().isEmpty() || invoiceDate.trim().isEmpty()|| invoiceAmount.trim().isEmpty() || totalAmount.trim().isEmpty() ||taxRate.trim().isEmpty() || taxAmount.trim().isEmpty()){
            flag=false;
        }

        return flag;

    }
    private Boolean checkInvoiceData(String redNoticeNumber, String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
        Boolean flag=true;


         /*if(!redNoticeNumber.matches("^[\\d]{16}$")) {
            flag=false;
        }*/
        if(!CommonUtil.isValidNum(invoiceCode,"^[\\d]{10}$")) {
            flag=false;
        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
            flag=false;
        }/*else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$")){
            if(!"1.5".equals(taxRate)){
                flag=false;
            }
        }*/else if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
            flag=false;
        }else if(!("01".equals(CommonUtil.getFplx(invoiceCode)))){
            flag=false;
        }

        if(flag){
            BigDecimal amount=new BigDecimal(invoiceAmount);
            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
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
            if((amount.add(taxAmount1)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(totalAmount1)!=0 ||(amount.multiply(rate)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(taxAmount1)!=0){
                flag=false;
            }
        }
        return flag;

    }
    @Override
    public RedTicketMatch selectNoticeById(Map<String, Object> params) {
        return entryRedTicketDao.selectNoticeById(params);
    }

    @Override
    public String getXfTaxno(Integer orgid) {
        return entryRedTicketDao.getXfTaxno(orgid);
    }
}
