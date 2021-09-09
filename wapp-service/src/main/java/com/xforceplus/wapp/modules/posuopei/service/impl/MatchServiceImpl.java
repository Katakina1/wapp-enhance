package com.xforceplus.wapp.modules.posuopei.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.DB2Conn;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.interfaceBPMS.Table;

import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.job.service.PurchaseOrderService;
import com.xforceplus.wapp.modules.job.utils.JMSProducer;
import com.xforceplus.wapp.modules.posuopei.dao.DetailsDao;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.export.*;
import com.xforceplus.wapp.modules.posuopei.export.InvoiceImport;
import com.xforceplus.wapp.modules.posuopei.export.MatchImport;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.json.JSONArray;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import javax.servlet.http.HttpServletResponse;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

@Service("matchService")
public class MatchServiceImpl implements MatchService {


   private static final Logger LOGGER= getLogger(MatchServiceImpl.class);

    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;
    
    @Value("${activemq.queue}")
	private String queue;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteQuestionPaperFileRootPath}")
    private String remoteQuestionPaperFileRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteQuestionPaperFileTempRootPath}")
    private String remoteQuestionPaperFileTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    @Autowired
     MatchDao matchDao;
    @Autowired
     DetailsDao detailsDao;
    @Autowired
    PurchaseOrderService purchaseOrderService;
    @Autowired
    private JMSProducer producer;
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;

    @Override
    public PagedQueryResult<PoEntity> poQueryList(Map<String, Object> map) {
       final PagedQueryResult<PoEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
       final Integer count=matchDao.poQueryCount(map);
        List<PoEntity> list= Lists.newArrayList();
       if(count>0){
           list=matchDao.poQueryList(map);
       }
       poEntityPagedQueryResult.setResults(list);
       poEntityPagedQueryResult.setTotalCount(count);
        return poEntityPagedQueryResult;
    }

    @Override
    public PagedQueryResult<ClaimEntity> claimQueryList(Map<String, Object> map) {
        final PagedQueryResult<ClaimEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        final Integer count=matchDao.claimQueryCount(map);
        List<ClaimEntity> list= Lists.newArrayList();
        List<ClaimEntity> lists= Lists.newArrayList();

        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, -40);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        currentTime=calendar.getTime();
        String date1 = formatter.format(currentTime);
//        Long mills = calendar.getTime().getTime();

            list=matchDao.claimQueryList(map);

            map.put("claimDateEnd",date1);
            map.put("claimDateStart",null);

            lists=matchDao.claimQueryList(map);
            lists.forEach(claimEntity -> {
                claimEntity.setIfYq("1");
            });
            for(int i=0;i<list.size();i++){
                ClaimEntity claimEntity=list.get(i);
                Boolean flag=false;
                String date2 = formatter.format(claimEntity.getPostdate());
                if(date1.compareTo(date2)>=0){
                    claimEntity.setIfYq("1");
                    for(int j=0;j<lists.size();j++){
                        ClaimEntity claimEntity1=lists.get(j);
                        if (claimEntity1.getId().equals(claimEntity.getId())) {
                            flag=true;
                            break;
                        }
                    }
                }else{
                    claimEntity.setIfYq("0");
                }
                if (!flag){
                    lists.add(claimEntity);
                }
            }

        poEntityPagedQueryResult.setResults(lists);
        poEntityPagedQueryResult.setTotalCount(count);
        return poEntityPagedQueryResult;
    }

    /**
     * 发票录入/发票导入校验
     * @param map
     * @return
     */
    @Override
    @Transactional
    public PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String, Object> map) {
        final PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        String code=map.get("invoiceCode").toString();
        String no=map.get("invoiceNo").toString();
        String uuid=code+""+no;
        Boolean flag=true;
        List<InvoiceEntity> list= Lists.newArrayList();
        map.put("uuid",uuid);
        List<InvoiceEntity> list1=matchDao.ifExist(map);
        String jvcode=(String)map.get("jvcode");
        String companyCode=matchDao.getCompanyCode(jvcode);
        map.put("companyCode",companyCode);
        OrgEntity orgEntity=matchDao.getXfMessage((String)map.get("venderid"));
        try {
            //判断uuid是否存在
            if (list1.size() == 0 || list1 == null) {
                //不存在
                //录入
//            map.put("id",null);

                map.put("invoiceType",CommonUtil.getFplx((String)map.get("invoiceCode")) ) ;
                map.put("xfTaxNo",orgEntity.getTaxno());
                map.put("xfName",orgEntity.getOrgname());
                if("04".equals(CommonUtil.getFplx((String)map.get("invoiceCode"))) ){
                    flag = matchDao.saveInvoicePP(map) > 0;
                }else{
                    flag = matchDao.saveInvoice(map) > 0;
                }

            } else {
                //存在数据
                String source = list1.get(0).getSystemSource();
                String matchstatus = list1.get(0).getDxhyMatchStatus();
                String tpStatus = list1.get(0).getTpStatus();
                String flowType = list1.get(0).getFlowType();
                String hostStatus = list1.get(0).getHostStatus();
                BigDecimal invoiceAmount = list1.get(0).getInvoiceAmount();

                if(invoiceAmount.compareTo(BigDecimal.ZERO)<0){
                    poEntityPagedQueryResult.setMsg("该发票金额小于0，不能匹配！");
                    poEntityPagedQueryResult.setResults(list);
                    poEntityPagedQueryResult.setTotalCount(0);
                    return poEntityPagedQueryResult;
                }
                if ("0".equals(hostStatus) || "10".equals(hostStatus) || "1".equals(hostStatus) || "13".equals(hostStatus) ||StringUtils.isEmpty(hostStatus)) {


//                String gfName=list1.get(0).getGfName();
//               String jvcode=list1.get(0).getJvcode();
                if (StringUtils.isEmpty(flowType) || "1".equals(flowType)) {

                    if (list1.get(0).getXfTaxNo().equals(orgEntity.getTaxno())) {
                        if (("0".equals(matchstatus) || "6".equals(matchstatus)) && (!"1".equals(tpStatus))) {
                            //判断来源
                            if ("0".equals(source)) {
                                //采集
//                        if(gfName==null||!gfName.equals(map.get("gfName"))) {
//                            poEntityPagedQueryResult.setMsg("该发票的购方名称不是当前购方！");
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//                            return poEntityPagedQueryResult;
//                      }else
                                if (list1.get(0).getGfTaxNo() == null || !list1.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                    poEntityPagedQueryResult.setMsg("该发票的购方税号与所选购方名称不一致！");
                                    poEntityPagedQueryResult.setResults(list);
                                    poEntityPagedQueryResult.setTotalCount(0);
                                    return poEntityPagedQueryResult;
                                }
//
//                      else if(list1.get(0).getVenderid()==null||!list1.get(0).getVenderid().equals(map.get("venderid"))){
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//
//                            poEntityPagedQueryResult.setMsg("该发票的供应商编号不是当前供应商！");
//                            return poEntityPagedQueryResult;
//                        }else if(jvcode==null||jvcode!=map.get("jvcode")){
//                            poEntityPagedQueryResult.setMsg("该发票的单位代码不匹配！");
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//                            return poEntityPagedQueryResult;
//                        }
                                //判断是否有明细
                                if ("0".equals(list1.get(0).getDetailYesorno()) && list1.get(0).getTaxRate() == null) {
                                    //无税率插入税率
                                    flag = matchDao.update(list1.get(0).getId(), new BigDecimal((String) map.get("taxRate"))) > 0;

                                } else if ("1".equals(list1.get(0).getDetailYesorno()) && list1.get(0).getTaxRate() == null) {
                                    //有明细无税率
                                    poEntityPagedQueryResult.setMsg("该发票不是单一税率发票！");
                                    poEntityPagedQueryResult.setResults(list);
                                    poEntityPagedQueryResult.setTotalCount(0);
                                    return poEntityPagedQueryResult;
                                }
                            } else if ("2".equals(source)) {
                                // 录入
                                //覆盖
                                map.put("id", list1.get(0).getId());
                                if ("04".equals(CommonUtil.getFplx((String) map.get("invoiceCode")))) {
                                    flag = matchDao.allUpdatePP(map) > 0;
                                } else {
                                    flag = matchDao.allUpdate(map) > 0;
                                }


                            } else {
//                        if(gfName==null||!gfName.equals(map.get("gfName"))) {
//                            poEntityPagedQueryResult.setMsg("该发票的购方名称不是当前购方！");
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//                            return poEntityPagedQueryResult;
//                        }
                                if (list1.get(0).getGfTaxNo() == null || !list1.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                    poEntityPagedQueryResult.setMsg("该发票的购方税号与所选购方名称不一致！");
                                    poEntityPagedQueryResult.setResults(list);
                                    poEntityPagedQueryResult.setTotalCount(0);
                                    return poEntityPagedQueryResult;
                                }
                                //更新抵扣金额
                                Object invoiceAmounts = map.get("invoiceAmount");
                                matchDao.updateDkAmount(new BigDecimal(String.valueOf(invoiceAmounts)),(String)map.get("uuid"));
//                        else if(list1.get(0).getVenderid()==null||!list1.get(0).getVenderid().equals(map.get("venderid"))){
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//                            poEntityPagedQueryResult.setMsg("该发票的供应商编号不是当前供应商！");
//                            return poEntityPagedQueryResult;
//                        }else if(jvcode==null||jvcode!=map.get("jvcode")){
//                            poEntityPagedQueryResult.setMsg("该发票的单位代码不匹配！");
//                            poEntityPagedQueryResult.setResults(list);
//                            poEntityPagedQueryResult.setTotalCount(0);
//                            return poEntityPagedQueryResult;
//                        }
                            }
                        } else {
                            poEntityPagedQueryResult.setMsg("该发票已匹配或者待退票！");
                            poEntityPagedQueryResult.setResults(list);
                            poEntityPagedQueryResult.setTotalCount(0);
                            return poEntityPagedQueryResult;

                        }
                    } else {
                        poEntityPagedQueryResult.setMsg("该发票销方税号不符！");
                        poEntityPagedQueryResult.setResults(list);
                        poEntityPagedQueryResult.setTotalCount(0);
                        return poEntityPagedQueryResult;
                    }
                } else {
                    poEntityPagedQueryResult.setMsg("该发票不是商品发票！");
                    poEntityPagedQueryResult.setResults(list);
                    poEntityPagedQueryResult.setTotalCount(0);
                    return poEntityPagedQueryResult;
                }
            }else{
                    poEntityPagedQueryResult.setMsg("该发票已在沃尔玛匹配！");
                    poEntityPagedQueryResult.setResults(list);
                    poEntityPagedQueryResult.setTotalCount(0);
                    return poEntityPagedQueryResult;

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("录入发票{}",e);
            throw new RuntimeException("录入发票失败");
        }

        if(flag){
            //返回列表

            if("04".equals(CommonUtil.getFplx((String)map.get("invoiceCode"))) ){
                list=matchDao.invoiceQueryListPP(map);
            }else{
                list=matchDao.invoiceQueryList(map);
            }
//                list=matchDao.invoiceQueryList(map);
                map.put("id",list.get(0).getId());
                matchDao.updateVenderid(map);

        }
         Boolean lig=true;
        if(list.size()>0){
            if(null==list.get(0).getInvoiceAmount()){
                list.get(0).setInvoiceAmount(new BigDecimal(0));
            }
            BigDecimal amount=list.get(0).getInvoiceAmount();
            if(null==list.get(0).getTaxRate()){
                list.get(0).setTaxRate(new BigDecimal(0));
            }
            BigDecimal rate=list.get(0).getTaxRate().divide(new BigDecimal(100));
            if(null==list.get(0).getTaxAmount()){
                list.get(0).setTaxAmount(new BigDecimal(0));
            }
            BigDecimal taxAmount=list.get(0).getTaxAmount();
            BigDecimal rest=taxAmount.subtract(amount.multiply(rate)).setScale(2,BigDecimal.ROUND_HALF_UP);
            if(rest.compareTo(BigDecimal.ZERO)>0){
                lig=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

            }else{
                lig=(rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);
            }
            if((amount.add(taxAmount)).compareTo(list.get(0).getTotalAmount())!=0){
                poEntityPagedQueryResult.setMsg("此发票异常，底账价税合计不等于发票金额和税额之和！");
                poEntityPagedQueryResult.setResults(Lists.newArrayList());
                poEntityPagedQueryResult.setTotalCount(0);
                return poEntityPagedQueryResult;
            }
        }
        if("04".equals(CommonUtil.getFplx(list.get(0).getInvoiceCode())) ){

        }else{
            if(!lig){
                poEntityPagedQueryResult.setMsg("此发票异常，税额比对相差超过正负0.05元！");
                poEntityPagedQueryResult.setResults(Lists.newArrayList());
                poEntityPagedQueryResult.setTotalCount(0);
                return poEntityPagedQueryResult;
            }

    }


        poEntityPagedQueryResult.setResults(list);
        poEntityPagedQueryResult.setTotalCount(0);
        return poEntityPagedQueryResult;
    }

    @Override
    public PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String, Object> map) {
        final PagedQueryResult<InvoiceEntity> pagedQueryResult=new PagedQueryResult<>();
        String uuid=(String) map.get("invoiceCode")+(String) map.get("invoiceNo");
        map.put("uuid",uuid);
        String jvcode=(String)map.get("jvcode");
        String companyCode=matchDao.getCompanyCode(jvcode);
        map.put("companyCode",companyCode);
        List<InvoiceEntity> list=Lists.newArrayList();
        if("04".equals(CommonUtil.getFplx((String)map.get("invoiceCode"))) ){
            list=matchDao.invoiceQueryListPP(map);
            if(list.size()==1){
                if(null==list.get(0).getDkinvoiceAmount()){
                        matchDao.updateDkAmount(list.get(0).getInvoiceAmount(),list.get(0).getUuid());
                }else{
                    if(list.get(0).getDkinvoiceAmount().compareTo(BigDecimal.ZERO)!=1){
                        matchDao.updateDkAmount(list.get(0).getInvoiceAmount(),list.get(0).getUuid());
                    }
                }
            }
        }else{
            list=matchDao.invoiceQueryList(map);
        }

        OrgEntity orgEntity=matchDao.getXfMessage((String)map.get("venderid"));

        if(list.size()>0) {
            String source = list.get(0).getSystemSource();
            String tpStatus = list.get(0).getTpStatus();
            String matchstatus = list.get(0).getDxhyMatchStatus();
            String flowType = list.get(0).getFlowType();
//                String gfName=list1.get(0).getGfName();
//               String jvcode=list1.get(0).getJvcode();
            BigDecimal invoiceAmount = list.get(0).getInvoiceAmount();
            if(invoiceAmount.compareTo(BigDecimal.ZERO)<0){
                pagedQueryResult.setMsg("该发票金额小于0，不能匹配！");
                return pagedQueryResult;

            }
            if (StringUtils.isEmpty(flowType) || "1".equals(flowType)) {


                if (orgEntity.getTaxno().equals(list.get(0).getXfTaxNo())) {
                    if ((("0".equals(matchstatus) || "6".equals(matchstatus))) && (!"1".equals(tpStatus))) {
                        //判断来源
                        if (("0".equals(source))) {
                            //采集
                            //判断是否有税率
                            if (list.get(0).getTaxRate() == null) {
                                //无税率
                                //是否有明细
                                if ("1".equals(list.get(0).getDetailYesorno())) {
                                    pagedQueryResult.setMsg("多税率发票无法匹配！");
                                }
                                //判断购方名称
                            } else if (list.get(0).getGfTaxNo() == null || !list.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                pagedQueryResult.setMsg("该发票的购方税号与所选购方名称不一致！");
                            } else {
                                //有税率
                                map.put("id", list.get(0).getId());
                                matchDao.updateVenderid(map);
                                pagedQueryResult.setResults(list);
                            }
                        } else if ("2".equals(source)) {
                            // 录入

                            pagedQueryResult.setMsg(null);

                        } else {
//                    if(list.get(0).getGfName()==null||!list.get(0).getGfName().equals(map.get("gfName"))){
//                        pagedQueryResult.setMsg("该发票的购方名称不是当前购方！");
//
//                    }else
                            if (list.get(0).getGfTaxNo() == null || !list.get(0).getGfTaxNo().equals(map.get("gfTaxno"))) {
                                pagedQueryResult.setMsg("该发票的购方税号与所选购方名称不一致！");
                            }
//
//                    else if(list.get(0).getVenderid()==null||!list.get(0).getVenderid().equals(map.get("venderid"))){
//                        pagedQueryResult.setMsg("该发票的供应商编号不是当前供应商！");
//                    }else if(list.get(0).getJvcode()==null||list.get(0).getJvcode()!=map.get("jvcode")){
//                        pagedQueryResult.setMsg("该发票的单位代码不匹配！");
//
//                    }
                            else {
                                map.put("id", list.get(0).getId());
                                matchDao.updateVenderid(map);
                                pagedQueryResult.setResults(list);
                            }
                        }
                    } else {
                        pagedQueryResult.setMsg("该发票已匹配或者待退票！");
                    }
                } else {
                    pagedQueryResult.setMsg("该发票销方税号不匹配！");

                }
            } else {
                pagedQueryResult.setMsg("该发票不是商品发票！");
            }

        }
        pagedQueryResult.setTotalCount(0);
        return pagedQueryResult;
    }

    @Transactional
    @Override
    public Integer saveInvoice(Map<String, Object> map) {
        return matchDao.saveInvoice(map);
    }

    @Override
    public List<InvoiceEntity> ifExist(Map<String, Object> map) {
        return matchDao.ifExist(map);
    }

    @Override
    public Boolean match(MatchEntity matchEntity) {

        return false;
    }

    /**
     * 获取购方名称和税号
     * @param
     * @param userId
     * @return
     */
    @Override
    public List<OrgEntity> getGfNameAndTaxNo(Long userId) {
        return matchDao.getGfNameAndTaxNo(userId);
    }

    @Override
    public List<OrgEntity> getPartion(String theKey) {
        return matchDao.getPartion(theKey);
    }

    @Override
    public List<Table> getCity() {
        return matchDao.getCity();
    }

    @Override
    public List<OrgEntity> getDicdeta(String theKey) {
        return matchDao.getDicdeta(theKey);
    }


    @Override
    public OrgEntity getDefaultMessage(Long userId) {
        return matchDao.getDefaultMessage(userId);
    }

    @Override
    public Boolean checkInvoiceMessage(String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
       Boolean flag=true;
        if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
            flag=false;
        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
            flag=false;
        }else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$")){
            if(!"1.5".equals(taxRate)){
                flag=false;
            }
        }else if((!CommonUtil.isValidNum(invoiceAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(totalAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(taxAmount,"^[0-9]+(.[0-9]{2})?$"))){
            flag=false;
        }else if((!CommonUtil.isValidNum(invoiceDate,"/^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/"))){
            flag=false;
        }
        if(flag){
            BigDecimal amount=new BigDecimal(invoiceAmount);
            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
            BigDecimal taxAmount1=new BigDecimal(taxAmount);
            BigDecimal rest=taxAmount1.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
            if(rest.compareTo(BigDecimal.ZERO)>0){
                flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

            }else{
                flag=rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0;
            }
        }
        return flag;

    }

    @Override
    @Transactional
    public Map<String, Object> importInvoice(Map<String, Object> maps, MultipartFile file) {
        final InvoiceImport invoiceImport = new InvoiceImport(file);
        final Map<String, Object> map = newHashMap();
        int k=0;
        try {
            //读取excel
            List currentList=Lists.newArrayList();
             List<ImportEntity> certificationEntityList= Lists.newArrayList();

                certificationEntityList = invoiceImport.analysisExcel();
                Set<String> set=new HashSet<String>();


            if (certificationEntityList.size()>0) {
                for(ImportEntity importEntity :certificationEntityList){
                    set.add(importEntity.getInvoiceCode()+importEntity.getInvoiceNo());
                }
                if(set.size()!=certificationEntityList.size()){
                    LOGGER.info("读取到excel数据格式有误");
                    map.put("success", Boolean.FALSE);
                    map.put("reason", "存在重复数据，请检查");
                    return map;
                }
                for(int i=0;i<certificationEntityList.size();i++){

                    ImportEntity importEntity=certificationEntityList.get(i);
                    Map<String,Object> mapps=Maps.newHashMapWithExpectedSize(10);
                    mapps.put("invoiceNo",importEntity.getInvoiceNo());
                    mapps.put("invoiceCode",importEntity.getInvoiceCode());
                    mapps.put("invoiceDate",importEntity.getInvoiceDate());
                    mapps.put("invoiceAmount",importEntity.getAmount());
                    mapps.put("totalAmount",importEntity.getTotalAmount());
                    mapps.put("taxAmount",importEntity.getTaxAmount());
                    mapps.put("taxRate",importEntity.getTaxRate());
                    mapps.put("venderid",maps.get("venderid"));
                    mapps.put("jvcode",maps.get("jvcode"));
                    mapps.put("gfName",maps.get("gfName"));
                    mapps.put("gfTaxno",maps.get("gfTaxno"));
                    mapps.put("checkNo",importEntity.getId());
                    PagedQueryResult<InvoiceEntity> invoiceEntityPagedQueryResult=this.invoiceQueryList(mapps);
                    List<InvoiceEntity> li=invoiceEntityPagedQueryResult.getResults();
                    if(li.size()>0){
                        currentList.add(li.get(0));

                    }else{
                        LOGGER.info("读取到excel数据格式有误");
                        map.put("success", Boolean.FALSE);
                        int n=i+3;
                        map.put("reason", "第"+n+"行，"+invoiceEntityPagedQueryResult.getMsg());
                        return map;
                    }

                }

                map.put("invoiceQueryList",currentList);

                map.put("success", Boolean.TRUE);

            }else {
                LOGGER.info("读取到excel数据格式有误");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel数据格式有误！");
            }
        } catch (Exception e) {
            LOGGER.info("读取到excel数据格式有误");
            e.printStackTrace();
            map.put("success", Boolean.FALSE);
            map.put("reason", e.getMessage());
        }


        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> importMatch(Map<String, Object> maps, MultipartFile file,String f) {
        final MatchImport matchImport = new MatchImport(file);
         Map<String, Object> map = newHashMap();
        Boolean flag=false;
        String matchno="";
            //读取excel
            List<MatchEntity> currentList=Lists.newArrayList();
        try {
            final List<MatchEntity> certificationEntityList = matchImport.analysisExcel(f);
                currentList=certificationEntityList;

            try {
                String result=checkMatch(currentList,maps);
                if("1".equals(result)){
                    for(int lag=0;lag<currentList.size();lag++){
                        flag= this.saveImportMatch(currentList.get(lag),maps,f);
                        if(!flag){
                            matchno=currentList.get(lag).getPrintcode();
                            break;
                        }
                    }
                }else {
                    map.put("success",Boolean.FALSE);
                    map.put("result",result);
                    return map;
                }
            } catch (Exception e) {
                LOGGER.info("导入匹配关系 {}",e);
                map.put("success",Boolean.FALSE);
                if(StringUtils.isEmpty(e.getMessage())){
                    map.put("result","导入匹配失败！");
                }else{
                    map.put("result","导入匹配失败！"+e.getMessage());
                }


            }
        } catch (Exception e) {
           LOGGER.info("导入匹配关系 {}",e);
            map.put("success",Boolean.FALSE);
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("result","导入匹配失败！");
            }else{
                map.put("result",matchno+e.getMessage());
            }
            return map;
        }

        if(flag){
            map.put("success",Boolean.TRUE);
            map.put("result","导入匹配成功！");
        }else {
            map.put("success",Boolean.FALSE);
            if(StringUtils.isEmpty(matchno)){
                map.put("result","未识别到有效数据");
            }else{
                map.put("result","第"+matchno+"组导入匹配失败！");
            }

        }

        map.put("list",currentList);
        return map;
    }
    //索赔明细
    @Override
    public Map<String, Object> importClaim(MultipartFile file){
        final AddClaimImport matchImport = new AddClaimImport(file);
        Map<String, Object> map = newHashMap();
        List<AddClaimImport> currentList=Lists.newArrayList();
        try{
            final List<AddClaimEntity> certificationEntityList = matchImport.analysisExcel();
             map.put("list",certificationEntityList);
        }catch (Exception e){
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("list",currentList);
                map.put("result","导入数据失败！");
            }else{
                map.put("list",currentList);
                map.put("result","导入数据失败！"+e.getMessage());
            }
        }
        return map;
    }
    //其他明细
    @Override
    public Map<String, Object> importOther(MultipartFile file){
        final AddOtherImport matchImport = new AddOtherImport(file);
        Map<String, Object> map = newHashMap();
        List<AddClaimImport> currentList=Lists.newArrayList();
        try{
            final List<AddClaimEntity> certificationEntityList = matchImport.analysisExcel();
            map.put("list",certificationEntityList);
        }catch (Exception e){
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("list",currentList);
                map.put("result","导入数据失败！");
            }else{
                map.put("list",currentList);
                map.put("result","导入数据失败！"+e.getMessage());
            }
        }
        return map;
    }
    //订单单价差异明细
    @Override
    public Map<String, Object> importPo(MultipartFile file){
        final AddPoImport matchImport = new AddPoImport(file);
        Map<String, Object> map = newHashMap();
        List<AddClaimImport> currentList=Lists.newArrayList();
        try{
            final List<AddClaimEntity> certificationEntityList = matchImport.analysisExcel();
            map.put("list",certificationEntityList);
        }catch (Exception e){
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("list",currentList);
                map.put("result","导入数据失败！");
            }else{
                map.put("list",currentList);
                map.put("result","导入数据失败！"+e.getMessage());
            }
        }
        return map;
    }
    //订单折扣差异明细
    @Override
    public Map<String, Object> importPoDiscount(MultipartFile file){
        final AddPoDiscountImport matchImport = new AddPoDiscountImport(file);
        Map<String, Object> map = newHashMap();
        List<AddClaimImport> currentList=Lists.newArrayList();
        try{
            final List<AddClaimEntity> certificationEntityList = matchImport.analysisExcel();
            map.put("list",certificationEntityList);
        }catch (Exception e){
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("list",currentList);
                map.put("result","导入数据失败！");
            }else{
                map.put("list",currentList);
                map.put("result","导入数据失败！"+e.getMessage());
            }
        }
        return map;
    }
    //收退货数量差异明细
    @Override
    public Map<String, Object> importCount(MultipartFile file,String type){
        final AddCountImport matchImport = new AddCountImport(file);
        Map<String, Object> map = newHashMap();
        List<AddClaimImport> currentList=Lists.newArrayList();
        try{
            final List<AddClaimEntity> certificationEntityList = matchImport.analysisExcel(type);
            map.put("list",certificationEntityList);
        }catch (Exception e){
            if(StringUtils.isEmpty(e.getMessage())){
                map.put("list",currentList);
                map.put("result","导入数据失败！");
            }else{
                map.put("list",currentList);
                map.put("result","导入数据失败！"+e.getMessage());
            }
        }
        return map;
    }
    @Override
    @Transactional
    public String saveMatch(MatchEntity matchEntity,String venId) {
        String matchStatus=matchEntity.getMatchingType();
        BigDecimal cover=new BigDecimal(0);
         Boolean flag=false;
         Integer matchId=-1;
        if("3".equals(matchStatus)||"4".equals(matchStatus)) {
            BigDecimal claimAmount=matchEntity.getClaimAmount();
            BigDecimal invoiceAmount=matchEntity.getInvoiceAmount();

                cover=matchEntity.getClaimAmount().add(matchEntity.getPoAmount().subtract(matchEntity.getInvoiceAmount()));


            try {
                matchEntity.setCover(cover);
                 matchDao.insertMatch(matchEntity);
                matchId=matchEntity.getId();

                if (!(matchId < 0)) {
                    Map<String, Object> map0 = Maps.newHashMapWithExpectedSize(10);
                    map0.put("matchno",matchId);
                    map0.put("id",matchId);
                    matchDao.updateMatchMatchno(map0);
                    List<PoEntity> poList = matchEntity.getPoEntityList();
                    List<ClaimEntity> claimList = matchEntity.getClaimEntityList();
                    List<InvoiceEntity> invoiceList = matchEntity.getInvoiceEntityList();
                    Integer finalMatchId = matchId;
                    BigDecimal midd=invoiceAmount.subtract(claimAmount);
                    for(int i=0;i<poList.size();i++ ) {
                        PoEntity po=poList.get(i);
                       List<PoEntity> receiptList=matchDao.getReceiptList(po.getPocode(),venId,matchEntity.getJvcode());
                       if(receiptList.size()<1){
                           throw new RuntimeException();
                       }
                        Boolean isCode=isPocode(receiptList);
                        Collections.sort(receiptList,new Comparator<PoEntity>(){

                            @Override
                            public int compare(PoEntity o1, PoEntity o2) {
                                if(!isCode){
                                    if(o1.getReceiptdate().getTime()>o2.getReceiptdate().getTime()){
                                        return 1;
                                    }else if(o1.getReceiptdate().getTime()<o2.getReceiptdate().getTime()){
                                        return -1;
                                    }else{
                                        return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                    }
                                }else{
                                    if(("4").equals(o1.getPoType())){
                                        if(("4").equals(o1.getPoType()) &&("4").equals(o2.getPoType())){
                                            return   o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    }else if(("2").equals(o1.getPoType())){
                                        if(("2").equals(o1.getPoType()) &&("2").equals(o2.getPoType())){
                                            return   o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    }else {
                                        return 1;
                                    }
                                }
                            }
                        });

                        for(int re=0;re<receiptList.size();re++){
                            PoEntity receiver=receiptList.get(re);
                            midd=midd.subtract(receiver.getAmountunpaid());
                 //           if(midd.compareTo(BigDecimal.ZERO)>0||midd.compareTo(BigDecimal.ZERO)==0||midd.abs().compareTo(new BigDecimal(20))==-1||midd.abs().compareTo(new BigDecimal(20))==0) {
                                Boolean lage = true;
                                Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                                map.put("matchid", finalMatchId);
                                map.put("codeType", "1");
                                map.put("matchno", finalMatchId);
                                map.put("code", receiver.getId());
                                map.put("changeAmount", receiver.getAmountunpaid());
                                map.put("amount", receiver.getAmountunpaid());
                                lage = matchDao.insertSonOfMatch(map) > 0;
                                if (lage) {
                                    Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                                    map1.put("id", receiver.getId());
                                    map1.put("matchno", finalMatchId);
                                    map1.put("dxhyMatchStatus", matchStatus);
                                    map1.put("amountunpaid", new BigDecimal(0));
                                    map1.put("amountpaid", receiver.getAmountpaid().add(receiver.getAmountunpaid()));
                                    matchDao.updatePoMatch(map1);
                                }
                                if(midd.compareTo(BigDecimal.ZERO)==0){
                                    LOGGER.info("该收货不参与");
                                    break;
                                }
         //                   }
                        };
                    };

                    claimList.forEach(claim -> {
                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "2");
                        map.put("code", claim.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", claim.getClaimAmount());
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", claim.getId());
                            map1.put("matchStatus", matchStatus);
                            map1.put("matchno", finalMatchId);
                            matchDao.updateCliamMatch(map1);
                        }


                    });


                    invoiceList.forEach(invoice -> {
                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "0");
                        map.put("code", invoice.getId());
                        map.put("changeAmount", new BigDecimal(0));

                        if("4".equals(matchStatus)){
                            map.put("amount", matchEntity.getPoAmount().add(matchEntity.getClaimAmount()));
                        }else{
                            map.put("amount", invoice.getInvoiceAmount());

                        }
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", invoice.getId());
                            map1.put("matchStatus", matchStatus);
                            map1.put("matchno", finalMatchId);
                            map1.put("venderid",venId);
                            if("4".equals(matchStatus)){
                                map1.put("invoiceAmount", matchEntity.getPoAmount().add(matchEntity.getClaimAmount()));
                            }else{
                                map1.put("invoiceAmount", invoice.getInvoiceAmount());

                            }
                            matchDao.updateSubmit(invoice.getInvoiceNo(),venId);
                          final Integer cot =matchDao.updateInvioceMatch(map1);
                            LOGGER.info("匹配参数",map1);
                            if(cot<1){
                                throw new RuntimeException();
                            }
                        }


                    });

                    flag = true;
                }
            }catch(Exception e){
                LOGGER.error("保存匹配 {}",e);
                throw new RuntimeException();
            }

        }else if("2".equals(matchStatus)){
                //部分匹配
            try {

                BigDecimal claimAmount=matchEntity.getClaimAmount();
                BigDecimal invoiceAmount=matchEntity.getInvoiceAmount();
                BigDecimal poAmount=matchEntity.getPoAmount();
                List<PoEntity> poList = matchEntity.getPoEntityList();
                //po单按照pocode从小到大排序
                Collections.sort(poList,new Comparator<PoEntity>(){

                    @Override
                    public int compare(PoEntity o1, PoEntity o2) {
                        Long i=Long.parseLong(o1.getPocode())-Long.parseLong(o2.getPocode());
                        if(i>0){
                            return 1;
                        }else if(1==0){
                            return 0;
                        }else{
                            return -1;
                        }

                    }
                });


                List<ClaimEntity> claimList = matchEntity.getClaimEntityList();

                List<InvoiceEntity> invoiceList = matchEntity.getInvoiceEntityList();
                BigDecimal mid=invoiceAmount.subtract(claimAmount);
                for(int i=0;i<poList.size();i++ ) {
                    PoEntity po=poList.get(i);
                    mid=mid.subtract(po.getAmountunpaid());
                    if(mid.compareTo(BigDecimal.ZERO)>0){
                        continue;
                    }else{
                        if(i<poList.size()-1){
                            return "您所选的PO单号为："+po.getPocode()+"的PO单不会参与此次匹配";
                        }
                        break;
                    }
                }
                matchEntity.setCover(new BigDecimal(0));
                matchDao.insertMatch(matchEntity);
                matchId=matchEntity.getId();
                Integer finalMatchId = matchId;

                if (!(matchId < 0)) {
                    Map<String, Object> map0 = Maps.newHashMapWithExpectedSize(10);
                    map0.put("matchno",matchId);
                    map0.put("id",matchId);
                    matchDao.updateMatchMatchno(map0);

                    BigDecimal midd=invoiceAmount.subtract(claimAmount);
                    Boolean bool=true;

                    for(int i=0;i<poList.size();i++ ) {
                        if(!bool){
                            break;
                        }
                        PoEntity po=poList.get(i);
                        List<PoEntity> receiptList=matchDao.getReceiptList(po.getPocode(),venId,matchEntity.getJvcode());
                        Boolean isCode=isPocode(receiptList);
                        Collections.sort(receiptList,new Comparator<PoEntity>(){

                            @Override
                            public int compare(PoEntity o1, PoEntity o2) {
                                if(!isCode){
                                    if(o1.getReceiptdate().getTime()>o2.getReceiptdate().getTime()){
                                        return 1;
                                    }else if(o1.getReceiptdate().getTime()<o2.getReceiptdate().getTime()){
                                        return -1;
                                    }else{
                                        return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                    }
                                }else {
                                    if (("4").equals(o1.getPoType())) {
                                        if (("4").equals(o1.getPoType()) && ("4").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else if (("2").equals(o1.getPoType())) {
                                        if (("2").equals(o1.getPoType()) && ("2").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            }
                        });

                        for(int re=0;re<receiptList.size();re++){
                            PoEntity receiver=receiptList.get(re);

                            Boolean lage = true;
                            BigDecimal change=new BigDecimal(0);
                            midd=midd.subtract(receiver.getAmountunpaid());
                            if(midd.compareTo(BigDecimal.ZERO)>0||midd.compareTo(BigDecimal.ZERO)==0){
                                Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                                map.put("matchid", finalMatchId);
                                map.put("codeType", "1");
                                map.put("matchno", finalMatchId);
                                map.put("code", receiver.getId());
                                map.put("changeAmount", receiver.getAmountunpaid());
                                map.put("amount", receiver.getAmountunpaid());
                                lage = matchDao.insertSonOfMatch(map) > 0;
                                if (lage) {
                                    Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                                    map1.put("id", receiver.getId());
                                    map1.put("matchno", finalMatchId);
                                    map1.put("dxhyMatchStatus", "3");
                                    map1.put("amountunpaid", new BigDecimal(0));
                                    map1.put("amountpaid", receiver.getAmountpaid().add(receiver.getAmountunpaid()));
                                    matchDao.updatePoMatch(map1);
                                    change=receiver.getAmountunpaid();
                                }
                                if(midd.compareTo(BigDecimal.ZERO)==0){
                                    break;
                                }


                            }else{

                                Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                                map.put("matchid", finalMatchId);
                                map.put("codeType", "1");
                                map.put("matchno", finalMatchId);
                                map.put("code", receiver.getId());
                                map.put("changeAmount", midd.add(receiver.getAmountunpaid()));
                                map.put("amount", receiver.getAmountunpaid());
                                lage = matchDao.insertSonOfMatch(map) > 0;
                                if (lage) {
                                    Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                                    map1.put("id", receiver.getId());
                                    map1.put("matchno", finalMatchId);
                                    map1.put("dxhyMatchStatus", "2");
                                    map1.put("amountunpaid",new BigDecimal(0).subtract(midd) );
                                    map1.put("amountpaid", receiver.getAmountpaid().add(midd).add(receiver.getAmountunpaid()));
                                    matchDao.updatePoMatch(map1);
                                    change=midd.add(receiver.getAmountunpaid());
                                }
                                bool=false;


                            }
                            if(!bool){
                                break;
                            }

                        }
                    };

                    claimList.forEach(claim -> {
                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "2");
                        map.put("code", claim.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", claim.getClaimAmount());
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", claim.getId());
                            map1.put("matchStatus", "2");
                            map1.put("matchno", finalMatchId);
                            matchDao.updateCliamMatch(map1);
                        }


                    });


                    invoiceList.forEach(invoice -> {
                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "0");
                        map.put("code", invoice.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", invoice.getInvoiceAmount());
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", invoice.getId());
                            map1.put("matchStatus", "2");
                            map1.put("matchno", finalMatchId);
                            map1.put("invoiceAmount", invoice.getInvoiceAmount());
                            map1.put("venderid",venId);
                            final Integer cot1 =matchDao.updateInvioceMatch(map1);
                            matchDao.updateSubmit(invoice.getInvoiceNo(),venId);
                            LOGGER.info("匹配参数",map1);
                            if(cot1<1){
                                throw new RuntimeException();
                            }
                        }


                    });

                    flag = true;
                }
            }catch(Exception e){
                LOGGER.error("保存匹配 {}",e);
                throw new RuntimeException();
            }
        }if("5".equals(matchStatus)){
            try {
                matchEntity.setCover(new BigDecimal(0));
                matchDao.insertMatch(matchEntity);
                matchId=matchEntity.getId();

                if (!(matchId < 0)) {
                    Map<String, Object> map0 = Maps.newHashMapWithExpectedSize(10);
//                    map0.put("matchno",matchId);
//                    map0.put("id",matchId);
//                    matchDao.updateMatchMatchno(map0);
                    List<PoEntity> poList = matchEntity.getPoEntityList();
                    List<ClaimEntity> claimList = matchEntity.getClaimEntityList();
                    List<InvoiceEntity> invoiceList = matchEntity.getInvoiceEntityList();
                    Integer finalMatchId = matchId;
                    poList.forEach(po -> {
                        List<PoEntity> receiptList=matchDao.getReceiptList(po.getPocode(),venId,matchEntity.getJvcode());
                        Boolean isCode=isPocode(receiptList);
                        Collections.sort(receiptList,new Comparator<PoEntity>(){

                            @Override
                            public int compare(PoEntity o1, PoEntity o2) {
                                if(!isCode){
                                    if(o1.getReceiptdate().getTime()>o2.getReceiptdate().getTime()){
                                        return 1;
                                    }else if(o1.getReceiptdate().getTime()<o2.getReceiptdate().getTime()){
                                        return -1;
                                    }else{
                                        return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                    }
                                }else {
                                    if (("4").equals(o1.getPoType())) {
                                        if (("4").equals(o1.getPoType()) && ("4").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else if (("2").equals(o1.getPoType())) {
                                        if (("2").equals(o1.getPoType()) && ("2").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            }
                        });

                        receiptList.forEach(rece->{
                            Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                            map.put("matchid", finalMatchId);
                            map.put("codeType", "1");
//                        map.put("matchno", finalMatchId);
                            map.put("code", rece.getId());
                            map.put("changeAmount", new BigDecimal(0));
                            map.put("amount", rece.getAmountunpaid());
                            matchDao.insertSonOfMatch(map) ;

                        });
//                       matchDao.updatePoFather(po.getPocode());

                    });

                    claimList.forEach(claim -> {

                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
//                        map.put("matchno", finalMatchId);
                        map.put("codeType", "2");
                        map.put("code", claim.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", claim.getClaimAmount());
                        matchDao.insertSonOfMatch(map) ;



                    });


                    invoiceList.forEach(invoice -> {

                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
//                        map.put("matchno", finalMatchId);
                        map.put("codeType", "0");
                        map.put("code", invoice.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", invoice.getInvoiceAmount());
                        matchDao.insertSonOfMatch(map) ;
                        matchDao.updateSubmit(invoice.getInvoiceNo(),venId);
                    });

                    flag = false;
                }
            }catch(Exception e){
                LOGGER.error("匹配失败 {}",e);
                throw new RuntimeException();
            }
        }
        if(flag){
            if("4".equals(matchStatus)){
                return String.valueOf(matchId);
            }
            return "匹配成功";
        }else{
            return "匹配失败";
        }

    }
    public Boolean isPocode(List<PoEntity> list){
        Boolean isPostCode=true;
        for(PoEntity po:list){
            //判断收货号是否为10个0、空、0
            if(!"0000000000".equals(po.getReceiptid())||!"".equals(po.getReceiptid())||!"0".equals(po.getReceiptid())){
                     isPostCode=false;
                }else{
                isPostCode=true;
            }
        }
        return isPostCode;
    }
    @Transactional
    public Boolean saveImportMatch(MatchEntity matchEntity,Map<String,Object> mappss,String f) {

        Boolean flag=false;
        Integer matchId=-1;


            try {


                List<PoEntity> poList = matchEntity.getPoEntityList();



                List<ClaimEntity> claimList = matchEntity.getClaimEntityList();

                List<InvoiceEntity> invoiceList = matchEntity.getInvoiceEntityList();
                for(int iv=0;iv<invoiceList.size();iv++){
                    InvoiceEntity inv=invoiceList.get(iv);

                    if(matchDao.checkInvoiceMatchStatus(inv.getUuid())==0){
                        return false;
                    }
                }

                matchEntity.setMatchingType("3");
                matchEntity.setVenderid((String)mappss.get("venderid"));
                matchEntity.setCover(matchEntity.getClaimAmount().add(matchEntity.getPoAmount().subtract(matchEntity.getInvoiceAmount())));
                matchDao.insertMatch(matchEntity);
                matchId=matchEntity.getId();
                Integer finalMatchId = matchId;

                if (!(matchId < 0)) {
                    Map<String, Object> map0 = Maps.newHashMapWithExpectedSize(10);
                    map0.put("matchno", matchId);
                    map0.put("id", matchId);
                    matchDao.updateMatchMatchno(map0);

                    BigDecimal midd = matchEntity.getInvoiceAmount().subtract(matchEntity.getClaimAmount());
                    Boolean bool=true;
                    for (int i = 0; i < poList.size(); i++) {
                        if(!bool){

                           break;

                        }
                        String venId="";
                        PoEntity po = poList.get(i);
                        if("8".equals(f)){
                            venId=(String)mappss.get("venderid");
                        }else{
                            venId=poList.get(i).getVenderid();
                        }
                        List<PoEntity> getReceipt = matchDao.getReceiptList(po.getPocode(),venId,po.getJvcode());
                        Boolean isCode=isPocode(getReceipt);
                        Collections.sort(getReceipt,new Comparator<PoEntity>(){

                            @Override
                            public int compare(PoEntity o1, PoEntity o2) {
                                if(!isCode){
                                    if(o1.getReceiptdate().getTime()>o2.getReceiptdate().getTime()){
                                        return 1;
                                    }else if(o1.getReceiptdate().getTime()<o2.getReceiptdate().getTime()){
                                        return -1;
                                    }else{
                                        return   o1.getAmountunpaid().compareTo(o2.getAmountunpaid());

                                    }
                                }else {
                                    if (("4").equals(o1.getPoType())) {
                                        if (("4").equals(o1.getPoType()) && ("4").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else if (("2").equals(o1.getPoType())) {
                                        if (("2").equals(o1.getPoType()) && ("2").equals(o2.getPoType())) {
                                            return o1.getAmountunpaid().compareTo(o2.getAmountunpaid());
                                        }
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            }
                        });
                        for (int s = 0; s < getReceipt.size(); s++) {
                            PoEntity receipt = getReceipt.get(s);
                            Boolean lage = true;
                            BigDecimal change=new BigDecimal(0);
                            midd = midd.subtract(receipt.getAmountunpaid());
                            if (midd.compareTo(BigDecimal.ZERO) > 0||midd.compareTo(BigDecimal.ZERO) == 0) {
                                Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                                map.put("matchid", finalMatchId);
                                map.put("codeType", "1");
                                map.put("matchno", finalMatchId);
                                map.put("code", receipt.getId());
                                map.put("changeAmount", receipt.getAmountunpaid());
                                map.put("amount", receipt.getAmountunpaid());
                                lage = matchDao.insertSonOfMatch(map) > 0;
                                if (lage) {
                                    Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                                    map1.put("id", receipt.getId());
                                    map1.put("matchno", finalMatchId);
                                    map1.put("dxhyMatchStatus", "3");
                                    map1.put("amountunpaid", new BigDecimal(0));
                                    map1.put("amountpaid", receipt.getAmountpaid().add(receipt.getAmountunpaid()));
                                    matchDao.updatePoMatch(map1);
                                    change=change.add(receipt.getAmountunpaid());
                                }

                                if(midd.compareTo(BigDecimal.ZERO)==0){
                                    break;
                                }
                            } else {

                                Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                                map.put("matchid", finalMatchId);
                                map.put("codeType", "1");
                                map.put("matchno", finalMatchId);
                                map.put("code", receipt.getId());
                                map.put("changeAmount", midd.add(receipt.getAmountunpaid()));
                                map.put("amount", receipt.getAmountunpaid());
                                lage = matchDao.insertSonOfMatch(map) > 0;
                                if (lage) {
                                    Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                                    map1.put("id", receipt.getId());
                                    map1.put("matchno", finalMatchId);
                                    map1.put("dxhyMatchStatus", "2");
                                    map1.put("amountunpaid", new BigDecimal(0).subtract(midd));
                                    map1.put("amountpaid", receipt.getAmountpaid().add(midd).add(receipt.getAmountunpaid()));
                                    matchDao.updateCover(finalMatchId,new BigDecimal(0));
                                    matchDao.updatePoMatch(map1);
                                    change=change.add(midd).add(receipt.getAmountunpaid());
                                }
                                bool=false;
                            }
                            if(!bool){
                                break;
                            }
                        }

                    }


                    for (int noc = 0; noc < claimList.size(); noc++) {
                        ClaimEntity claim=claimList.get(noc);
                        Map<String, Object> claimMap = Maps.newHashMapWithExpectedSize(5);
                        claimMap.put("venderid", mappss.get("venderid"));
                        claimMap.put("jvcode", claim.getJvcode());
                        claimMap.put("claimno", claim.getClaimno());
                        ClaimEntity claimEntity = matchDao.claimQueryWillBeMactch(claimMap).get(0);
                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "2");
                        map.put("code", claimEntity.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", claim.getClaimAmount());
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", claimEntity.getId());
                            map1.put("matchStatus", "3");
                            map1.put("matchno", finalMatchId);
                            matchDao.updateCliamMatch(map1);
                             }
                         }
                    }


                    for(int noi=0;noi<invoiceList.size();noi++){
                        String venId="";
                        if("8".equals(f)){
                            venId=(String)mappss.get("venderid");
                        }else{
                            venId=invoiceList.get(noi).getVenderid();
                        }
                        InvoiceEntity invoice=invoiceList.get(noi);

                        Boolean lage = true;
                        Map<String, Object> map = Maps.newHashMapWithExpectedSize(10);
                        map.put("matchid", finalMatchId);
                        map.put("matchno", finalMatchId);
                        map.put("codeType", "0");
                        map.put("code", invoice.getId());
                        map.put("changeAmount", new BigDecimal(0));
                        map.put("amount", invoice.getInvoiceAmount());
                        lage = matchDao.insertSonOfMatch(map) > 0;
                        if (lage) {
                            Map<String, Object> map1 = Maps.newHashMapWithExpectedSize(10);
                            map1.put("id", invoice.getId());
                            map1.put("matchStatus", "3");
                            map1.put("matchno", finalMatchId);
                            map1.put("invoiceAmount", invoice.getInvoiceAmount());
                            map1.put("venderid",venId);
                            final Integer cot2 =matchDao.updateInvioceMatch(map1);
                            LOGGER.info("匹配参数",map1);
                            if(cot2<1){
                                throw new RuntimeException();
                            }
                            matchDao.updateSubmit(invoice.getInvoiceNo(),venId);
                        }
                    }

                    flag = true;

            }catch(Exception e){
                LOGGER.error("保存匹配 {}",e);
                throw new RuntimeException();
            }

       return flag;

    }

    @Override
    public String getFplx(String fpdm) {
        String fplx = "";
        if (fpdm.length() == 12) {
            String zero=fpdm.substring(0,1);
            String lastTwo=fpdm.substring(10,12);
            if("0".equals(zero) && ("04".equals(lastTwo) || "05".equals(lastTwo))){
                fplx="04";
            }

        } else if (fpdm.length() == 10) {
            String fplxflag = fpdm.substring(7, 8);
            if ("6".equals(fplxflag) || "3".equals(fplxflag)) {
                fplx = "04";
            }
        }
        return fplx;
    }

    @Override
    @Transactional
    public Boolean saveQuestionPaper(QuestionPaperEntity questionPaperEntity) {
        Boolean flag=false;
        try{
            Date de = new Date();
            QuestionPaperEntity querymaxstream  = matchDao.querymaxstream(questionPaperEntity);
            if (querymaxstream != null) {
                String str2 = querymaxstream.getProblemStream();
                if (!str2.equals(null) && !str2.equals("")) {
                    str2 = str2.substring(6, 14);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String str = df.format(de);
                    if (str.equals(str2)) {
                        String str3 = querymaxstream.getProblemStream();
                        Long b = Long.valueOf(str3);
                        b = b + 1;
                        str3 = String.valueOf(b);

                        questionPaperEntity.setProblemStream(str3);
                        //生成流水号
                        flag= matchDao.saveQuestionPaper(questionPaperEntity)>0;
                    }else {
                        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
                        String str4 = questionPaperEntity.getUsercode() + df2.format(de) + "0000";

                        questionPaperEntity.setProblemStream(str4);
                        flag= matchDao.saveQuestionPaper(questionPaperEntity)>0;
                    }
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String str = questionPaperEntity.getUsercode() + df.format(de) + "0000";

                    questionPaperEntity.setProblemStream(str);
                    flag= matchDao.saveQuestionPaper(questionPaperEntity)>0;
                }
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String str = questionPaperEntity.getUsercode() + df.format(de) + "0000";

                questionPaperEntity.setProblemStream(str);
                flag= matchDao.saveQuestionPaper(questionPaperEntity)>0;
            }





            if(flag){
                if(questionPaperEntity.getClaimList()!=null) {
                    if(questionPaperEntity.getClaimList().size()>0){
                        questionPaperEntity.getClaimList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());
                            matchDao.saveQuestionClaim(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getCountList()!=null) {
                    if(questionPaperEntity.getCountList().size()>0){
                        questionPaperEntity.getCountList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionCount(claimtQuestionEntity);
                        });
                    }
                }

                if(questionPaperEntity.getOtherList()!=null) {
                    if(questionPaperEntity.getOtherList().size()>0){
                        questionPaperEntity.getOtherList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveOther(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getPoList()!=null) {
                    if(questionPaperEntity.getPoList().size()>0){

                        questionPaperEntity.getPoList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionPo(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getPoDiscountList()!=null) {
                    if(questionPaperEntity.getPoDiscountList().size()>0){

                        questionPaperEntity.getPoDiscountList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionPoDiscount(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getFileList()!=null && questionPaperEntity.getFileList().size()>0){
                    questionPaperEntity.getFileList().forEach(settlementFileEntity -> {
                        settlementFileEntity.setFileType(String.valueOf(questionPaperEntity.getId()));
                        matchDao.saveFile(settlementFileEntity);
                    });
                }
            }

        }catch (Exception e){
            LOGGER.info("保存采购问题单 {}",e);
            throw new RuntimeException();

        }


        return  flag;
    }

    /**
     * 修改问题单
     * @param questionPaperEntity
     * @return
     */
    @Override
    @Transactional
    public Boolean updateQuestionPaper(QuestionPaperEntity questionPaperEntity) {
        Boolean flag=false;
        try{
            flag= matchDao.updateQuestionPaper(questionPaperEntity)>0;
            matchDao.deleteQuestionDetail(questionPaperEntity.getId());
            matchDao.deleteQuestionAttchment(questionPaperEntity.getId());
            if(flag){
                if(questionPaperEntity.getClaimList()!=null) {
                    if(questionPaperEntity.getClaimList().size()>0){
                        questionPaperEntity.getClaimList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());
                            matchDao.saveQuestionClaim(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getCountList()!=null) {
                    if(questionPaperEntity.getCountList().size()>0){
                        questionPaperEntity.getCountList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionCount(claimtQuestionEntity);
                        });
                    }
                }

                if(questionPaperEntity.getOtherList()!=null) {
                    if(questionPaperEntity.getOtherList().size()>0){
                        questionPaperEntity.getOtherList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveOther(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getPoList()!=null) {
                    if(questionPaperEntity.getPoList().size()>0){

                        questionPaperEntity.getPoList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionPo(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getPoDiscountList()!=null) {
                    if(questionPaperEntity.getPoDiscountList().size()>0){

                        questionPaperEntity.getPoDiscountList().forEach(claimtQuestionEntity -> {
                            claimtQuestionEntity.setId(questionPaperEntity.getId());

                            matchDao.saveQuestionPoDiscount(claimtQuestionEntity);
                        });
                    }
                }
                if(questionPaperEntity.getFileList()!=null && questionPaperEntity.getFileList().size()>0){
                    questionPaperEntity.getFileList().forEach(settlementFileEntity -> {
                        settlementFileEntity.setFileType(String.valueOf(questionPaperEntity.getId()));
                        matchDao.saveFile(settlementFileEntity);
                    });
                }
            }

        }catch (Exception e){
            LOGGER.info("保存采购问题单 {}",e);
            throw new RuntimeException();

        }


        return  flag;
    }

    @Override
    public PagedQueryResult<QuestionPaperEntity> questionPaperQuery(Map<String, Object> map) {
        final PagedQueryResult<QuestionPaperEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        final Integer count=matchDao.questionPaperQueryCount(map);
        List<QuestionPaperEntity> list= Lists.newArrayList();
        if(count>0){
            list=matchDao.questionPaperQuery(map);
        }
        poEntityPagedQueryResult.setResults(list);
        poEntityPagedQueryResult.setTotalCount(count);
        return poEntityPagedQueryResult;
    }

    @Override
    public PagedQueryResult<Object> questionPaperDetailQuery(Map<String, Object> map) {
        final PagedQueryResult<Object> poEntityPagedQueryResult=new PagedQueryResult<>();
        List<Object> list= Lists.newArrayList();
        String questionType=(String)map.get("questionType");
        if("2001".equals(questionType)){
            list=matchDao.claimPaperQuery(map);
        }else if("2002".equals(questionType)){
            list=matchDao.poPaperQuery(map);
        }else if("2004".equals(questionType)){
            list=matchDao.countPaperQuery(map);
        }else if("2003".equals(questionType)){
            list=matchDao.poDiscountPaperQuery(map);
        }else{
            list=matchDao.otherPaperQuery(map);
        }


        poEntityPagedQueryResult.setResults(list);

        return poEntityPagedQueryResult;
    }

    public String checkMatch(List<MatchEntity> cerList,Map<String,Object> map) throws Exception{

        List<String> listPoCodeList=Lists.newArrayList();
        Set<String> setPoCodeList=new HashSet<String>();
        if(cerList.size()>0){
            for(int vip=0;vip<cerList.size();vip++){
                MatchEntity vipMatchEntity=cerList.get(vip);
                for(PoEntity poEntity: vipMatchEntity.getPoEntityList()){

                    listPoCodeList.add(poEntity.getPocode());
                    setPoCodeList.add(poEntity.getPocode());
                    if(listPoCodeList.size()!=setPoCodeList.size()){
                        return "第"+vipMatchEntity.getPrintcode()+"组订单号码在其他行中重复出现！订单号码为"+poEntity.getPocode();

                    }
                }
            }
            listPoCodeList.clear();
            setPoCodeList.clear();
            for(int vip=0;vip<cerList.size();vip++){
                MatchEntity vipMatchEntity=cerList.get(vip);
                if(vipMatchEntity.getClaimEntityList().size()>0){


                for(ClaimEntity claimEntity: vipMatchEntity.getClaimEntityList()){

                    listPoCodeList.add(claimEntity.getClaimno());
                    setPoCodeList.add(claimEntity.getClaimno());
                    if(listPoCodeList.size()!=setPoCodeList.size()){
                        return "第"+vipMatchEntity.getPrintcode()+"组索赔号码在其他行中重复出现！索赔号码为"+claimEntity.getClaimno();

                    }
                }
                }
            }
            listPoCodeList.clear();
            setPoCodeList.clear();
            for(int vip=0;vip<cerList.size();vip++){
                MatchEntity vipMatchEntity=cerList.get(vip);
                for(InvoiceEntity invoiceEntity: vipMatchEntity.getInvoiceEntityList()){

                    listPoCodeList.add(invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());
                    setPoCodeList.add(invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());
                    if(listPoCodeList.size()!=setPoCodeList.size()){
                        return "第"+vipMatchEntity.getPrintcode()+"组发票在其他行中重复出现！发票代码为"+invoiceEntity.getInvoiceCode()+"发票号码为"+invoiceEntity.getInvoiceNo();

                    }
                }
            }
            listPoCodeList.clear();
            setPoCodeList.clear();


        for(int j=0;j<cerList.size();j++) {
            MatchEntity matchEntity=cerList.get(j);
            if(!StringUtils.isEmpty(matchEntity.getVenderid())){
                map.put("venderid",matchEntity.getVenderid());
            }
            String jvcode=matchEntity.getPoEntityList().get(0).getJvcode();
            List<InvoiceEntity> invoiceEntityList=matchEntity.getInvoiceEntityList();
            List<ClaimEntity> claimEntityList=matchEntity.getClaimEntityList();
            List<PoEntity> poEntityList=matchEntity.getPoEntityList();

            if(invoiceEntityList.size()>0&& poEntityList.size()>0){
                if(invoiceEntityList.size()>1&& poEntityList.size()>1){
                    return "第"+matchEntity.getPrintcode()+"组订单数量和发票数量不符合匹配要求";
                }else{
//                    if(invoiceEntityList.size()>1&&poEntityList.size()==1){
//                        if(matchDao.getReceiptList(poEntityList.get(0).getPocode()).size()>1){
//                            return false;
//                        }
//                    }

                    BigDecimal claimAmount=new BigDecimal(0);
                    BigDecimal poAmount=new BigDecimal(0);
                    BigDecimal invoiceAmount=new BigDecimal(0);
                    if(claimEntityList.size()>0){
                        for(int cl=0;cl<claimEntityList.size();cl++){
                            ClaimEntity claimEntity=claimEntityList.get(cl);
                            claimAmount=claimAmount.add(claimEntity.getClaimAmount());
                        }
                    }

                    if(poEntityList.size()>0){
                        for(int cl=0;cl<poEntityList.size();cl++){
                            PoEntity poEntity=poEntityList.get(cl);
                            poAmount=poAmount.add(poEntity.getAmountunpaid());
                        }
                    }

                    if(invoiceEntityList.size()>0){
                        for(int cl=0;cl<invoiceEntityList.size();cl++){
                            InvoiceEntity invoiceEntity=invoiceEntityList.get(cl);
                            invoiceAmount=invoiceAmount.add(invoiceEntity.getInvoiceAmount());
                        }
                    }

                    if((poAmount.add(claimAmount).subtract(invoiceAmount)).abs().compareTo(new BigDecimal(20))>0){
                        return "第"+matchEntity.getPrintcode()+"组金额无法正常匹配！";
                    }else {
                        OrgEntity orgEntity=matchDao.getGfTaxNo(jvcode);
                        matchEntity.setGfTaxNo(orgEntity.getTaxno());
                        matchEntity.setGfName(orgEntity.getOrgname());
                        matchEntity.setPoNum(matchEntity.getPoEntityList().size());
                        matchEntity.setClaimNum(matchEntity.getClaimEntityList().size());
                        matchEntity.setInvoiceNum(matchEntity.getInvoiceEntityList().size());
                        matchEntity.setClaimAmount(claimAmount);
                        matchEntity.setPoAmount(poAmount);
                        matchEntity.setInvoiceAmount(invoiceAmount);
                        matchEntity.setSettlementamount(invoiceAmount);


                    }
                }
            }

            List<InvoiceEntity> liIn=Lists.newArrayList();
            //校验发票
            if(invoiceEntityList.size()>0){
               for(int i=0;i<invoiceEntityList.size();i++) {
                   InvoiceEntity invoiceEntity=invoiceEntityList.get(i);
                    Map<String,Object> mapps=Maps.newHashMapWithExpectedSize(10);
                    mapps.put("invoiceNo",invoiceEntity.getInvoiceNo());
                    mapps.put("invoiceCode",invoiceEntity.getInvoiceCode());
                    mapps.put("invoiceDate",invoiceEntity.getInvoiceDate());
                    mapps.put("invoiceAmount",invoiceEntity.getInvoiceAmount());
                    mapps.put("totalAmount",invoiceEntity.getTotalAmount());
                    mapps.put("taxAmount",invoiceEntity.getTaxAmount());
                    mapps.put("taxRate",String.valueOf(invoiceEntity.getTaxRate()));
                    mapps.put("uuid",invoiceEntity.getInvoiceCode()+""+invoiceEntity.getInvoiceNo());
                    mapps.put("jvcode",invoiceEntity.getJvcode());
                    mapps.put("checkNo",invoiceEntity.getCheckNo());
                   //反查
                    OrgEntity orgEntity=matchDao.getGfTaxNo(invoiceEntity.getJvcode());
                   mapps.put("gfTaxno",orgEntity.getTaxno());
                   mapps.put("gfName",orgEntity.getOrgname());
                    //外部
                    mapps.put("venderid",map.get("venderid"));

                   PagedQueryResult<InvoiceEntity> pageList=this.invoiceQueryList(mapps);
                   List<InvoiceEntity> li=pageList.getResults();
                    if(li.size()!=1) {
                        return "第"+matchEntity.getPrintcode()+"组发票状态异常,无法进行匹配!异常原因:"+pageList.getMsg();
                    }else {
                        if(li.get(0).getTaxRate().compareTo(invoiceEntity.getTaxRate())!=0||li.get(0).getInvoiceAmount().compareTo(invoiceEntity.getInvoiceAmount())!=0||li.get(0).getTaxAmount().compareTo(invoiceEntity.getTaxAmount())!=0){
                            return "第"+matchEntity.getPrintcode()+"组发票金额/税率/价税合计等信息有误！发票号码为"+li.get(0).getInvoiceNo();
                        }
                    }
                    if(li.get(0).getInvoiceAmount().compareTo(BigDecimal.ZERO)<0){
                        return "第"+matchEntity.getPrintcode()+"组发票金额小于0,无法进行匹配!异常原因:"+pageList.getMsg();

                    }
                    liIn.add(li.get(0));

                };
                //校验订单
                if(poEntityList.size()>0){
                    for(int p=0;p<poEntityList.size();p++){
                        PoEntity poEntity=poEntityList.get(p);
                        Map<String,Object> poCheckMap=Maps.newHashMapWithExpectedSize(4);
                        poCheckMap.put("jvcode",poEntity.getJvcode());
                        poCheckMap.put("amountunpaid",poEntity.getAmountunpaid());
                        poCheckMap.put("venderid",map.get("venderid"));
                        poCheckMap.put("receiptid",poEntity.getReceiptid());
                        poCheckMap.put("poCode",poEntity.getPocode());
                        BigDecimal checkBigdecimal=matchDao.checkPo(poCheckMap);
                        if(checkBigdecimal==null ||checkBigdecimal.compareTo(poEntity.getAmountunpaid())<0) {
                            LOGGER.info("该订单重复或者无法匹配");
                            return "第"+matchEntity.getPrintcode()+"组订单金额有误或者无法匹配！订单号码为"+poEntity.getPocode();
                        }
                    }
                }else {
                    return "第"+matchEntity.getPrintcode()+"组订单无法识别";
                }


                //校验索赔
                if(claimEntityList.size()>0){
                    for(int c=0;c<claimEntityList.size();c++){
                        ClaimEntity claimEntity=claimEntityList.get(c);
                        Map<String,Object> claimCheckMap=Maps.newHashMapWithExpectedSize(4);
                        claimCheckMap.put("jvcode",claimEntity.getJvcode());
                        claimCheckMap.put("claimAmount",claimEntity.getClaimAmount());
                        claimCheckMap.put("venderid",map.get("venderid"));
                        claimCheckMap.put("claimno",claimEntity.getClaimno());
                        if(matchDao.checkClaim(claimCheckMap)!=1) {
                            LOGGER.info("该索赔单重复或者无法匹配");
                            return "第"+matchEntity.getPrintcode()+"组索赔重复或者无法匹配！索赔号码为"+claimEntity.getClaimno();

                        }
                        List<ClaimEntity> ceList = matchDao.claimQueryWillBeMactch(claimCheckMap);
                        if(ceList.size()<1){
                            LOGGER.info("该索赔已结或不存在");
                            return "第"+matchEntity.getPrintcode()+"组索赔已结或不存在！索赔号码为"+claimEntity.getClaimno();
                        }
                    }
                }
            }else{
                return "第"+matchEntity.getPrintcode()+"组索赔无法识别";
            }
            matchEntity.setInvoiceEntityList(liIn);
         };
        }else {
            return "未识别到有效数据";
        }

        return "1";
    }




    @Override
    public  void ReGetconnHostPo1(String date,String dateEnd,String vender){
        Boolean flag=true;
        Connection conn = null;

        PreparedStatement st1 = null;


        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        matchDao.insertTaskLog("ReGetconnHostPo1","start",date);
        try {
            // 获取连接

            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC  where OC.process_status_ts >'"+date+" 00:00:00' ) a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id  AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=1    and B.POST_DATE>'2016-12-25'  and D.process_status_ts >'"+date+" 00:00:00' and D.process_status_ts <'"+dateEnd+" 00:00:00' "+vender+" ";
            // 创建语句执行者

            st1=conn.prepareStatement(sql1);




            //设置参数
            System.out.println("连接成功");
            // 执行sql

            rs1=st1.executeQuery();


            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setListPoType1(rs1);
            this.getHostData(list1,"1","u",0);
            matchDao.insertTaskLog("ReGetconnHostPo1","end",date);

        } catch (Exception e) {
            matchDao.insertTaskLog("ReGetconnHostPo1","exception",date);
            LOGGER.info("{}",e);
            flag=false;
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }


      if(!flag){
          ReGetconnHostPo1( date, dateEnd, vender);
      }


    }

    
    @Override
    public  void ReGetconnHostPo2(String date,String dateEnd,String vender){

        Boolean flag=true;
        Connection conn = null;

        PreparedStatement st1 = null;


        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        matchDao.insertTaskLog("ReGetconnHostPo2","start",date);
        try {
            // 获取连接

            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC  where OC.process_status_ts >'"+date+" 00:00:00' ) a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=2 AND B.PO_NBR !=0000000000  and B.POST_DATE>'2016-12-25'  and D.process_status_ts >'"+date+" 00:00:00' and D.process_status_ts <'"+dateEnd+" 00:00:00' "+vender+"";
            // 创建语句执行者

            st1=conn.prepareStatement(sql1);

            //设置参数
            System.out.println("连接成功");
            // 执行sql

            rs1=st1.executeQuery();


            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setList(rs1);
            this.getHostData(list1,"2","u",0);
            matchDao.insertTaskLog("ReGetconnHostPo2","end",date);

        } catch (Exception e) {
            matchDao.insertTaskLog("ReGetconnHostPo2","exception",date);
            LOGGER.info("{}",e);
            flag=false;
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }

        if(!flag){
            ReGetconnHostPo2( date, dateEnd, vender);
        }


    }
    @Override
    public  void ReGetconnHostPo4(String date,String dateEnd,String vender){

        Boolean flag=true;
        Connection conn = null;

        PreparedStatement st1 = null;


        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        matchDao.insertTaskLog("ReGetconnHostPo4","start",date);
        try {
            // 获取连接

            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC  where OC.process_status_ts >'"+date+" 00:00:00' ) a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=4 AND B.PO_NBR !=0000000000  and B.POST_DATE>'2016-12-25'  and D.process_status_ts >'"+date+" 00:00:00' and D.process_status_ts <'"+dateEnd+" 00:00:00' "+vender+"";
            // 创建语句执行者

            st1=conn.prepareStatement(sql1);

            //设置参数
            System.out.println("连接成功");
            // 执行sql

            rs1=st1.executeQuery();


            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setList(rs1);
            this.getHostData(list1,"2","u",0);
            matchDao.insertTaskLog("ReGetconnHostPo4","end",date);

        } catch (Exception e) {
            matchDao.insertTaskLog("ReGetconnHostPo4","exception",date);
            LOGGER.info("{}",e);
            flag=false;
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }

        if(!flag){
            ReGetconnHostPo4( date, dateEnd, vender);
        }


    }







    @Override
    public  void ReconnHostClaimType2(String date,String dateEnd,String vender){
        Boolean flag=true;
        Connection conn = null;

        PreparedStatement st1 = null;


        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        matchDao.insertTaskLog("ReconnHostClaimType2","start",date);
        try {
            // 获取连接

            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC  where OC.process_status_ts >'"+date+" 00:00:00' ) a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=2 AND B.PO_NBR =0000000000  and B.POST_DATE>'2016-12-25'  and D.process_status_ts >'"+date+" 00:00:00' and D.process_status_ts <'"+dateEnd+" 00:00:00' "+vender+"";
            // 创建语句执行者

            st1=conn.prepareStatement(sql1);

            //设置参数
            System.out.println("连接成功");
            // 执行sql

            rs1=st1.executeQuery();


            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setList(rs1);
            this.getHostData(list1,"5","u",0);
            matchDao.insertTaskLog("ReconnHostClaimType2","end",date);

        } catch (Exception e) {
            matchDao.insertTaskLog("ReconnHostClaimType2","exception",date);
            LOGGER.info("{}",e);
            flag=false;
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }

        if(!flag){
            ReconnHostClaimType2( date, dateEnd, vender);
        }


    }


    @Override
    public  void ReconnHostClaimType3(String date,String dateEnd,String vender){
        Boolean flag=true;
        Connection conn = null;

        PreparedStatement st1 = null;


        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        matchDao.insertTaskLog("ReconnHostClaimType3","start",date);
        try {
            // 获取连接

            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC  where OC.process_status_ts >'"+date+" 00:00:00' ) a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=3 and B.POST_DATE>'2016-12-25'  and D.process_status_ts >'"+date+" 00:00:00' and D.process_status_ts <'"+dateEnd+" 00:00:00' "+vender+"";
            // 创建语句执行者

            st1=conn.prepareStatement(sql1);

            //设置参数
            System.out.println("连接成功");
            // 执行sql

            rs1=st1.executeQuery();


            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setList(rs1);
            this.getHostData(list1,"3","u",0);
            matchDao.insertTaskLog("ReconnHostClaimType3","end",date);

        } catch (Exception e) {
            matchDao.insertTaskLog("ReconnHostClaimType3","exception",date);
            LOGGER.info("{}",e);
            flag=false;
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }

        if(!flag){
            ReconnHostClaimType3( date, dateEnd, vender);
        }

    }
    @Override
    public  void ReconnHostAgain(String date,String dateEnd,String vender){
        Boolean flag=true;
        Connection conn = null;
        PreparedStatement st = null;

        ResultSet rs = null;


        try {
            // 获取连接
            conn = DB2Conn.getConnection();

            // 编写sql
            String sql = "select B.invoice_nbr,B.invoice_date,D.PROCESS_STAT_CODE,B.vendor_nbr,D.process_status_ts from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+date+" 00:00:00') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+date+" 00:00:00' and D.process_status_ts<'"+dateEnd+" 00:00:00' "+vender+"";



            // 创建语句执行者
            st= conn.prepareStatement(sql);

            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs=st.executeQuery();
            matchDao.insertTaskLog("ReconnHostAgain","start",date);

            System.out.println("连接成功");
            System.out.println(new Date().toString());
            while (rs.next()){
                String invoiceNo=rs.getString(1).trim();
                invoiceNo=invoiceNo.substring(invoiceNo.length()-8,invoiceNo.length());
                String invoiceDate=rs.getString(2)+" 00:00:00";
                String hostStatus=rs.getString(3);
                String vendor=rs.getString(4);
                java.sql.Date hostDate=rs.getDate(5);
                vendor=vendor.substring(0,vendor.length()-3);
                int length=vendor.length();
                if(6-length>0){
                    for(int i=0;i<6-length;i++){
                        vendor="0"+vendor;
                    }
                }
                if(hostStatus.equals("13")){
                   String matchno= matchDao.getInvoiceMatchno(invoiceNo,hostStatus,invoiceDate,vendor);

                   if(!StringUtils.isEmpty(matchno)){
                       Boolean in=matchDao.getMatchHostStatus(matchno)>0;
                       if(in){
                           this.matchDelete(matchno);
                       }

                   }
                }
                matchDao.upDateInvoiceHostStatus(invoiceNo,hostStatus,invoiceDate,vendor,hostDate);
            }
            matchDao.insertTaskLog("ReconnHostAgain","end",date);

        } catch (Exception e) {
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("ReconnHostAgain","exception",date);
            flag=false;

        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }

        if(!flag){

        }
    }


    @Override
    public  JSONArray writeScreen(MatchEntity matchEntity){

         //用于记录
         List<HostWriterScreenEntity> hostWriterScreenEntityList=Lists.newArrayList();
         BigDecimal tempAmount=new BigDecimal(0);
        List<InvoiceEntity> invoiceList = matchDao.invoiceList(matchEntity.getMatchno());
        invoiceList.forEach(invoiceEntity -> {

                if("04".equals(CommonUtil.getFplx(invoiceEntity.getInvoiceCode())) ){
                    invoiceEntity.setInvoiceAmount(invoiceEntity.getDkinvoiceAmount());
                    if(invoiceEntity.getDeductibleTax()==null||invoiceEntity.getDeductibleTax().compareTo(BigDecimal.ZERO)<=0){

                    }else{
                        invoiceEntity.setTaxAmount(invoiceEntity.getDeductibleTax());
                    }

                    if(invoiceEntity.getDeductibleTaxRate()==null||invoiceEntity.getDeductibleTaxRate().compareTo(BigDecimal.ZERO)<=0){

                    }else{
                        invoiceEntity.setTaxRate(invoiceEntity.getDeductibleTaxRate());
                    }


                }

        });
         matchEntity.setInvoiceEntityList(invoiceList);


         matchEntity.setPoEntityList(matchDao.hostPoList(matchEntity.getMatchno()));
         matchEntity.setClaimEntityList(matchDao.claimList(matchEntity.getMatchno()));
         List<PoEntity> poEntityList=Lists.newArrayList();
         List<PoEntity> poEntities=matchEntity.getPoEntityList();
         poEntityList.addAll(poEntities);
                        BigDecimal claimTotal=new BigDecimal(0);
        for(int i=0;i<matchEntity.getClaimEntityList().size();i++) {
            ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(i);
            claimTotal = claimTotal.add(claimEntity.getClaimAmount());
        }
        Date dueDate=matchDao.getDueDate(matchEntity.getMatchno());
         if(dueDate==null){
             dueDate=new Date();
         }
        Calendar thisdate = Calendar.getInstance();
        thisdate.setTime(new Date());

            //step2 遍历发票
         for(int i=0;i<matchEntity.getInvoiceEntityList().size();i++) {
             InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
             BigDecimal InSubClaim=new BigDecimal(0);
             if(i==0) {
                 //先结索赔
                 for (int j = 0; j < matchEntity.getClaimEntityList().size(); j++) {
                     HostWriterScreenEntity hostWriterScreenEntity=new HostWriterScreenEntity();

                     ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(j);
                     WriterScreenDataEntity writerScreenDataEntity = new WriterScreenDataEntity();

                     writerScreenDataEntity.setJv(invoiceEntity.getJvcode());
                     writerScreenDataEntity.setVender(invoiceEntity.getVenderid());
                     writerScreenDataEntity.setInv(invoiceEntity.getInvoiceNo());
                     writerScreenDataEntity.setError("${error}");
                     writerScreenDataEntity.setSeq(claimEntity.getSeq());
                     Date date = null;
                     try {
                         date = new SimpleDateFormat("yyyy-MM-dd").parse(invoiceEntity.getInvoiceDate());
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     Calendar now = Calendar.getInstance();
                     now.setTime(date);

                     String year = String.valueOf(now.get(Calendar.YEAR));
                     String month = String.valueOf(now.get(Calendar.MONTH) + 1); // 0-based!
                     if (month.length() == 1) {
                         month = "0" + month;
                     }
                     String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));

                     if (day.length() == 1) {
                         day = "0" + day;
                     }

                     Calendar now1 = Calendar.getInstance();
                     now1.setTime(dueDate);


                     if (month.length() == 1) {
                         month = "0" + month;
                     }
                     now1.add(Calendar.DAY_OF_MONTH,-2);

                     String day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));
                     String year1= String.valueOf(now1.get(Calendar.YEAR));
                     String month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
//                     now1.add(Calendar.DAY_OF_MONTH,+2);

                     if(now1.compareTo(thisdate)<0){
                         Calendar cc=Calendar.getInstance();
                         cc.setTime(new Date());
                         cc.add(Calendar.DAY_OF_MONTH,+3);

                         now1=cc;
                         year1= String.valueOf(now1.get(Calendar.YEAR));
                         month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
                         if (month.length() == 1) {
                             month = "0" + month;
                         }
                         day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));

                     }
//                      year1= String.valueOf(now1.get(Calendar.YEAR));
//                      month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
//                      day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));

                     if (day.length() == 1) {
                         day = "0" + day;
                     }
                     if (day1.length() == 1) {
                         day1 = "0" + day1;
                     }
                     if (month1.length() == 1) {
                         month1 = "0" + month1;
                     }
                     writerScreenDataEntity.setYY1(year1);
                     writerScreenDataEntity.setMM1(month1);
                     writerScreenDataEntity.setDD1(day1);
                     writerScreenDataEntity.setYY(year);
                     writerScreenDataEntity.setMM(month);
                     writerScreenDataEntity.setDD(day);
                     writerScreenDataEntity.setTaxTotal(String.valueOf(invoiceEntity.getTaxAmount()));
                     writerScreenDataEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                     if ("01".equals(invoiceEntity.getInvoiceType())) {
                         writerScreenDataEntity.setTaxTypeZ("X");
                         writerScreenDataEntity.setTaxType(" ");
                     } else if ("04".equals(invoiceEntity.getInvoiceType())) {
                         writerScreenDataEntity.setTaxType("X");
                         writerScreenDataEntity.setTaxTypeZ(" ");
                     }
                     writerScreenDataEntity.setInvTotal(String.valueOf(invoiceEntity.getTotalAmount()));
                     writerScreenDataEntity.setPoNbr(matchEntity.getPoEntityList().get(0).getPocode());
                     writerScreenDataEntity.setPayCode("0001");
                     writerScreenDataEntity.setError("${error}");
                    writerScreenDataEntity.setIfFapr("0");
                     writerScreenDataEntity.setCover("");
                     writerScreenDataEntity.setTransaction(claimEntity.getClaimno());
                     writerScreenDataEntity.setReceiver("0000000000");
                     writerScreenDataEntity.setInvPreTaxAmt(String.valueOf(claimEntity.getClaimAmount()));
                     writerScreenDataEntity.setIfCut("0");
                     hostWriterScreenEntity.setData(writerScreenDataEntity);
                     hostWriterScreenEntity.setLogin_store_id("");
                     hostWriterScreenEntity.setScreen_name("CICCNMP_FAPI");
                     hostWriterScreenEntity.setSystem_name("");

                     hostWriterScreenEntity.setId(claimEntity.getTractionIdSeq());
                     LOGGER.info("索赔 {}",claimEntity.getClaimno());

                     hostWriterScreenEntityList.add(hostWriterScreenEntity);

                 }
                 //发票和索赔总金额 InSubClaim
                  InSubClaim=invoiceEntity.getInvoiceAmount().subtract(claimTotal);

             }else{
                 //发票金额 InSubClaim
                  InSubClaim=invoiceEntity.getInvoiceAmount();

             }



            //如果InSubClaim余额大于0，再借订单
             if(InSubClaim.compareTo(BigDecimal.ZERO)>0) {



                     for (int k = 0; k < poEntities.size(); k++) {
                         HostWriterScreenEntity hostWriterScreenEntity=new HostWriterScreenEntity();

                         PoEntity poEntity = poEntities.get(k);
                         WriterScreenDataEntity writerScreenDataEntity = new WriterScreenDataEntity();
                         writerScreenDataEntity.setError("${error}");
                         writerScreenDataEntity.setJv(invoiceEntity.getJvcode());
                         writerScreenDataEntity.setVender(invoiceEntity.getVenderid());
                         writerScreenDataEntity.setInv(invoiceEntity.getInvoiceNo());

                         Date date = null;
                         try {
                             date = new SimpleDateFormat("yyyy-MM-dd").parse(invoiceEntity.getInvoiceDate());
                         } catch (ParseException e) {
                             e.printStackTrace();
                         }
                         Calendar now = Calendar.getInstance();
                         now.setTime(date);

                         String year = String.valueOf(now.get(Calendar.YEAR));
                         String month = String.valueOf(now.get(Calendar.MONTH) + 1); // 0-based!
                         if (month.length() == 1) {
                             month = "0" + month;
                         }
                         String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));

                         if (day.length() == 1) {
                             day = "0" + day;
                         }
                         Calendar now1 = Calendar.getInstance();
                         now1.setTime(dueDate);


                         if (month.length() == 1) {
                             month = "0" + month;
                         }
                         now1.add(Calendar.DAY_OF_MONTH,-2);
                         String year1= String.valueOf(now1.get(Calendar.YEAR));
                         String month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
                         String day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));
//                         now1.add(Calendar.DAY_OF_MONTH,+2);

                         if(now1.compareTo(thisdate)<0){
                             Calendar cc=Calendar.getInstance();
                             cc.setTime(new Date());
                             cc.add(Calendar.DAY_OF_MONTH,+3);

                             now1=cc;
                             year1= String.valueOf(now1.get(Calendar.YEAR));
                             month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
                             if (month.length() == 1) {
                                 month = "0" + month;
                             }
                             day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));

                         }
//                          year1= String.valueOf(now1.get(Calendar.YEAR));
//                          month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
//                          day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));

                         if (month1.length() == 1) {
                             month1 = "0" + month1;
                         }
                         if (day1.length() == 1) {
                             day1 = "0" + day1;
                         }
                         writerScreenDataEntity.setYY1(year1);
                         writerScreenDataEntity.setMM1(month1);
                         writerScreenDataEntity.setDD1(day1);
                         writerScreenDataEntity.setYY(year);
                         writerScreenDataEntity.setMM(month);
                         writerScreenDataEntity.setSeq(poEntity.getSeq());
                         writerScreenDataEntity.setDD(day);
                         writerScreenDataEntity.setTaxTotal(String.valueOf(invoiceEntity.getTaxAmount()));
                         writerScreenDataEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                         if ("01".equals(invoiceEntity.getInvoiceType())) {
                             writerScreenDataEntity.setTaxTypeZ("X");
                             writerScreenDataEntity.setTaxType(" ");
                         } else if ("04".equals(invoiceEntity.getInvoiceType())) {
                             writerScreenDataEntity.setTaxType("X");
                             writerScreenDataEntity.setTaxTypeZ(" ");

                         }
                         writerScreenDataEntity.setInvTotal(String.valueOf(invoiceEntity.getTotalAmount()));
                         writerScreenDataEntity.setPoNbr(poEntity.getPocode());
                         writerScreenDataEntity.setPayCode("0001");
                         writerScreenDataEntity.setIfFapr("0");
                         writerScreenDataEntity.setCover("");
                         //potype=1
                         if("1".equals(poEntity.getPoType())) {
                             writerScreenDataEntity.setTransaction(poEntity.getTractionNbr());
                             writerScreenDataEntity.setReceiver(poEntity.getReceiptid());
                         }else if("4".equals(poEntity.getPoType())){
                             //potype=2/4
                             writerScreenDataEntity.setTransaction(poEntity.getTractionNbr());
                             writerScreenDataEntity.setReceiver(poEntity.getPocode());
                         }else{
                             writerScreenDataEntity.setTransaction(poEntity.getTractionNbr());
                             writerScreenDataEntity.setReceiver(poEntity.getReceiptid());
                         }
                         Boolean flag = true;
                         if (InSubClaim.subtract(poEntity.getAmountpaid()).compareTo(new BigDecimal(0)) >= 0) {
                             writerScreenDataEntity.setInvPreTaxAmt(String.valueOf(poEntity.getAmountpaid()));
                             InSubClaim = InSubClaim.subtract(poEntity.getAmountpaid());
                             writerScreenDataEntity.setIfCut("0");
                             poEntityList.remove(0);
                         } else {
                             writerScreenDataEntity.setInvPreTaxAmt(String.valueOf(InSubClaim));
                             writerScreenDataEntity.setIfCut("1");
                             tempAmount = poEntity.getAmountpaid().subtract(InSubClaim);

                             poEntity.setAmountpaid(tempAmount);

                             poEntityList.set(0,poEntity);
                             flag = false;
                         }
                         hostWriterScreenEntity.setData(writerScreenDataEntity);
                         hostWriterScreenEntity.setLogin_store_id("");
                         hostWriterScreenEntity.setScreen_name("CICCNMP_FAPI");
                         hostWriterScreenEntity.setSystem_name("");
                         hostWriterScreenEntity.setId(poEntity.getTractionIdSeq());
                         LOGGER.info("订单 {}",poEntity.getReceiptid());
                         hostWriterScreenEntityList.add(hostWriterScreenEntity);
                         if (!flag) {
                             poEntities.clear();
                             poEntities.addAll(poEntityList);
                             break;
                         }
                     }



             }

         }
            if(hostWriterScreenEntityList.size()>0){
                if((matchEntity.getCover().compareTo(new BigDecimal(0)))<0){
                    hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().setInvPreTaxAmt(String.valueOf(new BigDecimal(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getInvPreTaxAmt()).subtract(matchEntity.getCover())));
                }

                if(matchEntity.getCover().compareTo(new BigDecimal(20))>0||matchEntity.getCover().compareTo(new BigDecimal(-20))<0){
                    hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().setIfFapr("1");

                    hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().setCover(String.valueOf(matchEntity.getCover()));
                }
            }


        return  this.List2Json(hostWriterScreenEntityList);

    }

    @Override
    public void runWritrScreen() {
         List<OrgEntity> list1=matchDao.getPartion("write");
        LOGGER.info("-------------------------写屏定时任务开启--------------------");
        if(list1.size()>0&&"OPEN".equals(list1.get(0).getDictcode())){

            Destination destination = new ActiveMQQueue(queue);


            List<MatchEntity> list=matchDao.getMatchLists();

            Connection conn=null;
            try {
                conn = DB2Conn.getConnection();
                Connection finalConn = conn;
                list.forEach((MatchEntity matchEntity) -> {
                    List<SubmitOutstandingReportEntity> list2=this.checkWriteScreen(matchEntity, finalConn);
                    List<SubmitOutstandingReportEntity> list3=Lists.newArrayList();
//                    list3.addAll(list2);
                    if(list2.size()==0){
                    	LOGGER.info("-------sendQueueBegin-------");
                        matchDao.upDateHostStatus(matchEntity.getMatchno());

                        producer.sendMessage(destination, String.valueOf(this.writeScreen(matchEntity)));
                        LOGGER.info("-------sendQueueSuccess-------");

                    }else {

                        if (matchEntity.getInvoiceEntityList().size() > 0) {
                            for (int i = 0; i < matchEntity.getInvoiceEntityList().size(); i++) {

                                InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
                                list3.add(new SubmitOutstandingReportEntity("", "", list2.get(0).getJv(), list2.get(0).getVendorNo(), invoiceEntity.getInvoiceNo(), String.valueOf(invoiceEntity.getInvoiceAmount()), "", matchEntity.getMatchno(), "", "", "", "", "", "",invoiceEntity.getInvoiceDate()));



                            }
                            for (int j = 0; j < matchEntity.getPoEntityList().size(); j++) {
                                PoEntity poEntity = matchEntity.getPoEntityList().get(j);
                                if (j < list3.size()) {
                                    for (int k = 0; k < list2.size(); k++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity = list2.get(k);

                                        if (String.valueOf(submitOutstandingReportEntity.getId()).equals(poEntity.getTractionIdSeq())) {

                                            list3.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                            list3.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                            list3.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                            list3.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                            list3.get(j).setErrcode(String.valueOf(submitOutstandingReportEntity.getErrcode()));
                                            list3.get(j).setErrdesc(String.valueOf(submitOutstandingReportEntity.getErrdesc()));
                                        } else {
                                            list3.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                            list3.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                            list3.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                            list3.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                        }

                                    }
                                } else {
                                    Boolean flag=true;
                                    for (int s = 0; s < list2.size(); s++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity1 = list2.get(s);


                                        if (String.valueOf(submitOutstandingReportEntity1.getId()).equals(poEntity.getTractionIdSeq())) {

                                            list3.add(new SubmitOutstandingReportEntity("", "", list2.get(0).getJv(), list2.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), submitOutstandingReportEntity1.getErrcode(), submitOutstandingReportEntity1.getErrdesc(), ""));
                                            flag=false;
                                        }

                                    }
                                    if(flag){
                                        list3.add(new SubmitOutstandingReportEntity("", "", list2.get(0).getJv(), list2.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), "", "", ""));

                                    }
                                }


                            }
                            for (int v = 0; v < matchEntity.getClaimEntityList().size(); v++) {
                                ClaimEntity claimEntity=matchEntity.getClaimEntityList().get(v);
                                if (v < list3.size()-matchEntity.getInvoiceEntityList().size()) {
                                    for (int k = 0; k < list2.size(); k++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity2 = list2.get(k);

                                        if (String.valueOf(submitOutstandingReportEntity2.getId()).equals(claimEntity.getTractionIdSeq())) {

                                            list3.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                            list3.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));
                                            list3.get(v).setErrcode(String.valueOf(submitOutstandingReportEntity2.getErrcode()));
                                            list3.get(v).setErrdesc(String.valueOf(submitOutstandingReportEntity2.getErrdesc()));
                                        } else {
                                            list3.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                            list3.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));

                                        }

                                    }
                                } else {
                                    Boolean flag=true;
                                    for (int s = 0; s < list2.size(); s++) {

                                        SubmitOutstandingReportEntity submitOutstandingReportEntity3 = list2.get(s);


                                        if (String.valueOf(submitOutstandingReportEntity3.getId()).equals(claimEntity.getTractionIdSeq())) {

                                            list3.add(new SubmitOutstandingReportEntity("", "", list2.get(0).getJv(), list2.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", submitOutstandingReportEntity3.getErrcode(), submitOutstandingReportEntity3.getErrdesc(), ""));
                                            flag=false;
                                        }

                                    }
                                    if(flag){
                                        list3.add(new SubmitOutstandingReportEntity("", "", list2.get(0).getJv(), list2.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", "", "", ""));
                                    }
                                }
                            }
                        }
                    }
                    if(list3.size()>0){
                        list3.forEach(en->{
                            submitOutstandingReportDao.insertSubmitOutstandingReport(en);
                        });

                    }
                });
            } catch (Exception e) {
            	LOGGER.info("mqException",e);
                matchDao.insertTaskLog("runWritrScreen","exception","");

                e.printStackTrace();

            }finally {
                System.out.println("close connect");
                System.out.println(new Date().toString());
                DB2Conn.closeConnection(conn);
            }
        }

        //step1 遍历匹配关系
//        detailsDao.upDateWriteStatus();
        matchDao.insertTaskLog("runWritrScreen","end","");

        LOGGER.info("-------------------------写屏定时任务结束--------------------");

    }


    @Transactional
    @Override
    public Integer deleteQuestion(Integer id){
        return matchDao.deleteQuestion(id);
    }
    @Override
    public List<SubmitOutstandingReportEntity> checkWriteScreen(MatchEntity matchEntity,Connection conn) {

        matchEntity.setPoEntityList(matchDao.hostPoList(matchEntity.getMatchno()));
        matchEntity.setClaimEntityList(matchDao.claimList(matchEntity.getMatchno()));
        matchEntity.setInvoiceEntityList(matchDao.invoiceList(matchEntity.getMatchno()));
        List<SubmitOutstandingReportEntity> list=Lists.newArrayList();
        matchEntity.getClaimEntityList().forEach(claimEntity -> {
            SubmitOutstandingReportEntity submitOutstandingReportEntity=checkHost(matchEntity,claimEntity,conn);

            if(!"0".equals(submitOutstandingReportEntity.getErrcode())){
                list.add(submitOutstandingReportEntity);

                if("208".equals(submitOutstandingReportEntity.getErrcode())){

                }else{
                    this.matchCancel(matchEntity.getMatchno());
                    matchDao.cancelInvoiceMatch(matchEntity.getMatchno());
                }
            }

        });

        matchEntity.getPoEntityList().forEach(poEntity -> {
            SubmitOutstandingReportEntity submitOutstandingReportEntity=checkHost(matchEntity,poEntity,conn);
            if(!"0".equals(submitOutstandingReportEntity.getErrcode())){
                list.add(submitOutstandingReportEntity);
                if("208".equals(submitOutstandingReportEntity.getErrcode())){

                }else{
                    this.matchCancel(matchEntity.getMatchno());
                    matchDao.cancelInvoiceMatch(matchEntity.getMatchno());
					matchDao.hostUpdatePoAmount(poEntity);
//                    matchDao.cancelInvoiceMatch(matchEntity.getMatchno());
                }

            }
        });
//        matchEntity.getInvoiceEntityList().forEach(invoiceEntity -> {
//            SubmitOutstandingReportEntity submitOutstandingReportEntity=checkHost(invoiceEntity,conn);
//            if(!"0".equals(submitOutstandingReportEntity.getErrcode())){
//                list.add(submitOutstandingReportEntity);
//            }
//        });


        return list;
    }

    public  SubmitOutstandingReportEntity checkHost(MatchEntity matchEntity,ClaimEntity claimEntity,Connection conn){


        PreparedStatement st1 = null;
        ResultSet rs1 = null;

        String sql2= "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+claimEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+claimEntity.getSeq()+"')  OC ) a where a.rnum =1";
        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();
        String hostStatus="1";
        BigDecimal cost=new BigDecimal(0);
        try {
            st1=conn.prepareStatement(sql2);
            rs1=st1.executeQuery();


            while(rs1.next()){
                hostStatus=rs1.getString(1);
                cost=rs1.getBigDecimal(2);
            }
        } catch (SQLException e) {
             hostStatus="";
            LOGGER.info("claim",e);
        }
//        matchEntity.getInvoiceEntityList().forEach(invoiceEntity -> {
//            if(StringUtils.isEmpty(submitOutstandingReportEntity.getInvNo())){
//                submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
//            }else{
//                submitOutstandingReportEntity.setInvNo(submitOutstandingReportEntity.getInvNo()+"/"+invoiceEntity.getInvoiceNo());
//            }
//        });


            if("8".equals(hostStatus)){

                submitOutstandingReportEntity.setErrcode("204");
                submitOutstandingReportEntity.setErrdesc("货款冻结");
                submitOutstandingReportEntity.setTrans(claimEntity.getClaimno());
                submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
                submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
//                submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
                submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));

                submitOutstandingReportEntity.setRece("");
            }else if(!("1".equals(hostStatus)) && !StringUtils.isEmpty(hostStatus)){
                submitOutstandingReportEntity.setErrcode("203");
                submitOutstandingReportEntity.setErrdesc("索赔已结");
                submitOutstandingReportEntity.setTrans(claimEntity.getClaimno());
                submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
                submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));

                submitOutstandingReportEntity.setRece("");
            }else if((cost.compareTo(new BigDecimal(0).subtract(claimEntity.getClaimAmount()))!=0)){
                submitOutstandingReportEntity.setErrcode("401");
                submitOutstandingReportEntity.setErrdesc("索赔金额多开");
                submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));

                submitOutstandingReportEntity.setTrans(claimEntity.getTractionId());
                submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
                submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
                submitOutstandingReportEntity.setRece("");

            }else if(StringUtils.isEmpty(hostStatus)){
                submitOutstandingReportEntity.setErrcode("208");
                submitOutstandingReportEntity.setErrdesc("订单或索赔不存在");
                submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));

                submitOutstandingReportEntity.setTrans(claimEntity.getTractionId());
                submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
                submitOutstandingReportEntity.setRece("");
            }
            else {
                submitOutstandingReportEntity.setErrcode("0");
            }

        return submitOutstandingReportEntity;
    }

    public  SubmitOutstandingReportEntity checkHost(MatchEntity matchEntity,PoEntity poEntity,Connection conn){
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;

        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String sql2="SELECT t1.comment_text  from cnpurord.po_comment t1 left join cninvmat.financial_txn t2 on t2.purchase_order_id=t1.purchase_order_id  where t2.transaction_id='"+poEntity.getTractionId()+"'  AND t2.txn_seq_nbr='"+poEntity.getSeq()+"' and t1.COMMENT_TYPE_CODE='100'";
        String sql1= "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"')  OC ) a where a.rnum =1";

//        String sql2 = "SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"'";
        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();

        try {
            st1=conn.prepareStatement(sql1);
            st2=conn.prepareStatement(sql2);
            rs1=st1.executeQuery();
            rs2=st2.executeQuery();
            Date postDate=poEntity.getReceiptdate();
            Date dueDate=null;
            Integer next=0;
            while(rs2.next()){

                try{

                    String payterm = rs2.getString(1);
                    payterm=payterm.trim();
                    System.out.println(payterm);
                    String lastIndex=payterm.substring(payterm.length()-1,payterm.length());
                     payterm=getNumber(payterm);
                     if(!StringUtils.isEmpty(payterm)){
                         next= Integer.valueOf(payterm);
                         System.out.println("----------------------------Net:"+next);
                     }else {
                         submitOutstandingReportEntity.setErrcode("409");
                         submitOutstandingReportEntity.setErrdesc("订单DueDate问题");
                         submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                         submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                         submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                         submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                         submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                         submitOutstandingReportEntity.setRece("");
                         return submitOutstandingReportEntity;
                     }

                    System.out.println("----------------------------eom:"+lastIndex);



                    Calendar now1 = Calendar.getInstance();
                    now1.setTime(postDate);
                    Calendar now = Calendar.getInstance();

                    Integer day=now1.get(Calendar.DAY_OF_MONTH);
                    System.out.println("----------------------------postdate:"+day);

                    if("m".equals(lastIndex)){
                        if(day>24){

                            now1.add(Calendar.MONTH,1);
                            now.set(now1.get(Calendar.YEAR),now1.get(Calendar.MONTH),24);
                            now.add(Calendar.DAY_OF_MONTH,next);
                        }else{
                            now.set(now1.get(Calendar.YEAR),now1.get(Calendar.MONTH),24);
                            now.add(Calendar.DAY_OF_MONTH,next);
                        }

                    }else{
                        now.set(now1.get(Calendar.YEAR),now1.get(Calendar.MONTH),now1.get(Calendar.DAY_OF_MONTH));
                        now.add(Calendar.DAY_OF_MONTH,next);
                    }


                    dueDate =now.getTime();
                }catch (Exception e){
                    LOGGER.info("",e);
                    dueDate=null;
                     next=0;
                    submitOutstandingReportEntity.setErrcode("409");
                    submitOutstandingReportEntity.setErrdesc("订单DueDate问题");
                    submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                    submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                    submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                    submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                    submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                    submitOutstandingReportEntity.setRece("");
                    return submitOutstandingReportEntity;
                }

            }
            if(dueDate!=null){
                matchDao.updatePoDueDate(dueDate,poEntity.getTractionId()+poEntity.getSeq());
            }
            String hostStatus="0";
            BigDecimal cost=new BigDecimal(0);
            while(rs1.next()){

                try{
                    hostStatus=rs1.getString(1);
                    cost=rs1.getBigDecimal(2);
                }catch (Exception e){
                    LOGGER.info("",e);
                    hostStatus="";
                    continue;
                }finally {
                    continue;
                }

            }
//            matchEntity.getInvoiceEntityList().forEach(invoiceEntity -> {
//                if(StringUtils.isEmpty(submitOutstandingReportEntity.getInvNo())){
//                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
//                }else{
//                    submitOutstandingReportEntity.setInvNo(submitOutstandingReportEntity.getInvNo()+"/"+invoiceEntity.getInvoiceNo());
//                }
//            });
            if("8".equals(hostStatus)){
                submitOutstandingReportEntity.setErrcode("204");
                submitOutstandingReportEntity.setErrdesc("货款冻结");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setRece(poEntity.getReceiptid());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                poEntity.setHoststatus(hostStatus);
                poEntity.setAmountpaid(cost);
            } else if(!("1".equals(hostStatus)) && !StringUtils.isEmpty(hostStatus)){
                submitOutstandingReportEntity.setErrcode("205");
                submitOutstandingReportEntity.setErrdesc("订单已结");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setRece("");
                poEntity.setHoststatus(hostStatus);
                poEntity.setAmountpaid(cost);
            }else if((cost.compareTo(new BigDecimal(0).subtract(poEntity.getAmountpaid()))!=0)){
                submitOutstandingReportEntity.setErrcode("402");
                submitOutstandingReportEntity.setErrdesc("订单金额多开");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setRece("");
                poEntity.setHoststatus(hostStatus);
                poEntity.setAmountpaid(cost);
            }else if(StringUtils.isEmpty(hostStatus)){
                submitOutstandingReportEntity.setErrcode("208");
                submitOutstandingReportEntity.setErrdesc("订单或索赔不存在");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setRece("");
                
            }
            else {
                submitOutstandingReportEntity.setErrcode("0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submitOutstandingReportEntity;
    }

    public  SubmitOutstandingReportEntity checkHost(InvoiceEntity invoiceEntity,Connection conn){
        PreparedStatement st1 = null;
        ResultSet rs1 = null;
        String sql2= "SELECT\n" +
                "\tCOUNT (1)\n" +
                "FROM\n" +
                "\tCNINVMAT.INVOICE B\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\ta.*\n" +
                "\tFROM\n" +
                "\t\t(\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\tOC.*, Row_Number () OVER (\n" +
                "\t\t\t\t\tpartition BY invoice_id\n" +
                "\t\t\t\t\tORDER BY\n" +
                "\t\t\t\t\t\tprocess_status_ts DESC\n" +
                "\t\t\t\t) rnum\n" +
                "\t\t\tFROM\n" +
                "\t\t\t\tCNINVMAT.INVC_PROCESS_LOG OC\n" +
                "\t\t) a\n" +
                "\tWHERE\n" +
                "\t\ta.rnum = 1\n" +
                ") AS D ON D.INVOICE_ID = B.INVOICE_ID\n" +
                "WHERE\n" +
                "\tB.invoice_nbr = ''\n" +
                "AND B.invoice_date = ''\n" +
                "AND B.vendor_nbr = ''\n" +
                "AND D.PROCESS_STAT_CODE IN (0, 1, 10)";
//        String sql2= "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"')  OC ) a where a.rnum =1";

//        String sql2 = "SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"'";
        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();

        try {
            st1=conn.prepareStatement(sql2);
            rs1=st1.executeQuery();
            String hostStatus="0";
            BigDecimal cost=new BigDecimal(0);
            while(rs1.next()){
                try{
                    hostStatus=rs1.getString(1);
                    cost=rs1.getBigDecimal(2);
                }catch (Exception e){
                    hostStatus="";
                    continue;
                }finally {
                    continue;
                }

            }
            if("1".equals(hostStatus)||"0".equals(hostStatus)||"10".equals(hostStatus)){

                submitOutstandingReportEntity.setErrcode("0");

            }
            else {
                submitOutstandingReportEntity.setErrcode("402");
                submitOutstandingReportEntity.setErrdesc("发票已匹配");
                submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submitOutstandingReportEntity;
    }
    public List<PoEntity> setList(ResultSet rs) throws SQLException {
        List<PoEntity> list= Lists.newArrayList();
        while(rs.next()){
            String pocode = rs.getString(1);


            String poType = rs.getString(2);
            String venderid = rs.getString(3);
            int length=venderid.length();
            if(6-length>0){
                for(int i=0;i<6-length;i++){
                    venderid="0"+venderid;
                }
            }
            String receipt=rs.getString(4);
            BigDecimal amount = new BigDecimal(0).subtract(rs.getBigDecimal(5));
            java.sql.Date postDate = rs.getDate(6);
            BigDecimal taxRate = rs.getBigDecimal(7);
            String jvcode = rs.getString(8);
            String vendername;
            try {
                vendername= rs.getString(9);
            }catch (Exception e){
                vendername="4420";
            }
            String tractionNo=rs.getString(10);
            String invoiceId=rs.getString(11);
            java.sql.Date tractionDate=rs.getDate(12);
            String tractionid=rs.getString(13);
            String hoststatus=rs.getString(14);
            java.sql.Date dueDate=rs.getDate(15);
            String seq=rs.getString(16);
            String dept=rs.getString(17);
            String storeNbr=rs.getString(18);
            String tractionIdSeq=tractionid+seq;
            PoEntity po= new PoEntity(pocode,venderid,receipt,amount,poType,postDate,taxRate,hoststatus,invoiceId,tractionNo,tractionDate,jvcode,vendername,tractionid,dueDate,dept,seq,tractionIdSeq);
            po.setStoreNbr(storeNbr);
            po.setDueDate(dueDate);
            list.add(po);
        }
        return list;
    }
    public List<PoEntity> setListPoType1(ResultSet rs) throws SQLException {
        List<PoEntity> list= Lists.newArrayList();
        while(rs.next()){
            String pocode = rs.getString(1);


            String poType = rs.getString(2);
            String venderid = rs.getString(3);
            int length=venderid.length();
            if(6-length>0){
                for(int i=0;i<6-length;i++){
                    venderid="0"+venderid;
                }
            }
            String receipt=rs.getString(4);
            BigDecimal amount = new BigDecimal(0).subtract(rs.getBigDecimal(5));
            java.sql.Date postDate = rs.getDate(6);
            BigDecimal taxRate = rs.getBigDecimal(7);
            String jvcode = rs.getString(8);
            String vendername;
            try {
                vendername= rs.getString(9);
            }catch (Exception e){
                vendername="4420";
            }


            String tractionNo=rs.getString(10);
            String invoiceId=rs.getString(11);
            java.sql.Date tractionDate=rs.getDate(12);
            String tractionid=rs.getString(13);
            String hoststatus=rs.getString(14);

            java.sql.Date dueDate=rs.getDate(15);
            String seq=rs.getString(16);
            String dept=rs.getString(17);
            String storeNbr=rs.getString(18);
            String tractionIdSeq=tractionid+seq;
            PoEntity po= new PoEntity(pocode,venderid,receipt,amount,poType,postDate,taxRate,hoststatus,invoiceId,tractionNo,tractionDate,jvcode,vendername,tractionid,dueDate,dept,seq,tractionIdSeq);
            po.setStoreNbr(storeNbr);
            po.setDueDate(dueDate);
            list.add(po);
        }
        return list;
    }

    public void getHostData(List<PoEntity> list,String type,String hostStatus,int i) throws Exception {
        try {
            if("1".equals(type)||"4".equals(type)||"2".equals(type)){

                if("u".equals(hostStatus)){
                    //清空临时表
                    Boolean deletCount=true;
                    try{
                        this.deletePoCopy();
                    }catch (Exception e){
                        e.printStackTrace();
                        deletCount=false;
                        matchDao.insertTaskLog("ReconnHostPo"+type,"deletePoCopyException","");
                        this.deletePoCopy();
                    }
                    if(deletCount){
                        try{
                            this.insertPoListCopy(list);
                        }catch (Exception e){
                            e.printStackTrace();
                            matchDao.insertTaskLog("ReconnHostPo"+type,"insertPoListCopyException","");

                        }

                        matchDao.transferProcPo();
                        matchDao.adjustment();
                    }



                }

            }else if("3".equals(type)||"5".equals(type)) {

                if ("u".equals(hostStatus)) {
                    Boolean deletCount=true;
                    try{
                        this.deleteClaimCopy();
                    }catch (Exception e){
                        e.printStackTrace();
                        deletCount=false;
                        matchDao.insertTaskLog("ReconnHostClaimType"+type,"deleteClaimCopyException","");
                        this.deleteClaimCopy();
                    }

                    if(deletCount) {


                        //将数据插入临时表
                        try {
                            this.insertClaimListCopy(list);
                        } catch (Exception e) {
                            matchDao.insertTaskLog("ReconnHostClaimType" + type, "insertClaimListCopyException", "");
                            e.printStackTrace();
                        }
                        matchDao.transferProcClaim();
                    }


                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            if("3".equals(type) ||"2".equals(type)){
                matchDao.insertTaskLog(type,String.valueOf(i),"");
                getHostData(list,type,hostStatus,i);
            }else{
                throw new Exception();
            }



        }

    }


    @Override
    public String uploadFile(MultipartFile file) {
        LOGGER.debug("----------------上传问题单文件开始--------------------");
        String filePath = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteQuestionPaperFileTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            filePath = handler.uploadDate(file);
        } catch (Exception e){
            LOGGER.debug("----------------上传问题单文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------上传问题单文件完成--------------------");
        return filePath;
    }

    @Override
    @Transactional
    public Integer saveFile(SettlementFileEntity fileEntity) {
        return matchDao.saveFile(fileEntity);
    }

    @Override
    public SettlementFileEntity getFileInfo(Long id) {
        return matchDao.getFileInfo(id);
    }

    @Override
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName,"UTF-8"));
            OutputStream output = response.getOutputStream();
            handler.download(filePath, fileName);
            File file = new File(handler.getLocalImageRootPath()+fileName);
            FileInputStream in = new FileInputStream(file);// 获取实体类对应Byte
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
            in.close();
            output.close();
        } catch (Exception e) {
            LOGGER.info("----下载文件异常--- {}",e);

        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    @Override
    public List<SettlementFileEntity> viewFile(String id) {
        return matchDao.getFileList(id);
    }

    @Override
    public Boolean check(Map<String,Object> param) {
        Boolean yesornor;
        if("2".equals(String.valueOf(param.get("result")))){
            param.put("reject",new Date());
            yesornor = matchDao.checkTwo(param)>0;
        }else{

            if("4".equals(String.valueOf(param.get("result")))){
                QuestionPaperEntity entity = matchDao.getPaperById(param);
                purchaseOrderService.sendSingle(0,entity);
                yesornor = matchDao.check(param)>0;
            }else{
                yesornor = matchDao.check(param)>0;
            }
        }
        return yesornor;
    }

    @Override
    public Boolean cheXiao(Map<String,Object> param) {
        Boolean yesornor = matchDao.cheXiao(param)>0;
        return yesornor;
    }


    @Transactional
    public Boolean matchDelete(String matchno) {

        try{
            final Boolean flag=detailsDao.deleMatch(matchno)>0;
            if(flag){
                detailsDao.cancelClaim(matchno);
                detailsDao.cancelInvoice(matchno);
                List<PoEntity> list=detailsDao.getPoJiLu(matchno);

                for(int k=0;k<list.size();k++){
                    PoEntity poEntity=list.get(k);


                        detailsDao.deletePo(poEntity.getId(),poEntity.getChangeAmount(),"6");


//                    Integer discount=detailsDao.ifChangePoFatherStatus(poEntity.getPocode());
//                    String status="1";
//                    if(discount>0){
//                        status="0";
//                    }
//                    detailsDao.cancelPoFather(poEntity.getPocode(),poEntity.getChangeAmount(),status);
                }
            }else {
               return flag;
            }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }
        return true;
    }

    @Transactional
    public String matchCancel(String matchno) {
        String msg="取消匹配成功！";
        try{
            final Boolean flag=detailsDao.cancelMatch(matchno)>0;
            if(flag){
                detailsDao.cancelClaim(matchno);
                detailsDao.cancelInvoice(matchno);
                List<PoEntity> list=detailsDao.getPoJiLu(matchno);
                BigDecimal changeTotal=new BigDecimal(0);
                for(int k=0;k<list.size();k++){
                    PoEntity poEntity=list.get(k);
                    Integer count=detailsDao.ifBFPP(poEntity.getId());
                    if(count>0){
                        detailsDao.cancelPo(poEntity.getId(),poEntity.getChangeAmount(),"6");
                    }else {
                        detailsDao.cancelPo(poEntity.getId(),poEntity.getChangeAmount(),"6");
                    }
                    Integer discount=detailsDao.ifChangePoFatherStatus(poEntity.getPocode());
                    String status="1";
                    if(discount>0){
                        status="0";
                    }
//                    detailsDao.cancelPoFather(poEntity.getPocode(),poEntity.getChangeAmount(),status);
                }
            }else {
                msg="该条匹配无法取消！";
            }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }
        return msg;
    }
    public  JSONArray List2Json(List<HostWriterScreenEntity> list){
        JSONArray json = JSONArray.fromObject(list);
        LOGGER.info("json {}",json);
        return json;
    }
    @Override
   public List<InvoicesEntity> getInvoice(String venderid,String invoice_no,String invoice_amount){
        List<InvoicesEntity> lists=matchDao.getInvoice(venderid,invoice_no,invoice_amount);
        return  lists;
    }
    @Override
   public List<MatchEntity> getMatch(String matchno){
        return matchDao.getMatch(matchno);
    }

    @Transactional
    @Override
   public Integer updateMatchHostStatus(String host_status,String id){
        return matchDao.updateMatchHostStatus(host_status,id);
    }

    @Override
    public String checkPoSupplement() {
        return matchDao.checkPoSupplement();
    }

    @Override
    public String checkClaimSupplement() {
        return matchDao.checkClaimSupplement();
    }

    @Override
    public Integer onClaimSupplement() {
        return matchDao.onClaimSupplement();
    }

    @Override
    public Integer onPoSupplement() {
        return matchDao.onPoSupplement();
    }

    @Override
    public Integer offClaimSupplement() {
        return matchDao.offClaimSupplement();
    }

    @Override
    public Integer offPoSupplement() {
        return matchDao.offPoSupplement();
    }


    @Transactional
    Integer deletePoCopy(){
        return matchDao.deletePoCopy();
    }

    @Transactional
    Integer deleteClaimCopy(){
        return matchDao.deleteClaimCopy();
    }


    public Integer insertClaimListCopy(List<PoEntity> list) {
        List<PoEntity> claimList=Lists.newArrayList();
        Integer s=0;
        for(Integer c=0;c<list.size();c++){
            if(s==100){
                //批量插入订单数据
                Integer count=1;
                try {
                    count=this.insertClaimCopyList(claimList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(count>0){
                    claimList.clear();
                    s=0;
                }

            }   //批量插入索赔数据
            claimList.add(list.get(c));
            s++;

        }
        if(claimList.size()>0){
            try {
                return this.insertClaimCopyList(claimList);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }

    }


    public Integer insertPoListCopy(List<PoEntity> list) {
        List<PoEntity> poEntityList1=Lists.newArrayList();
        Integer s=0;
        for(Integer c=0;c<list.size();c++){
            if(s==100){
                //批量插入订单数据
                Integer count=1;
                try {
                    count=this.insertPoListCopy1(poEntityList1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(count>0){
                    poEntityList1.clear();
                    s=0;
                }

            }   //批量插入订单数据
            poEntityList1.add(list.get(c));
            s++;

        }
        if(poEntityList1.size()>0){
            try {
                return this.insertPoListCopy1(poEntityList1);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }

    }
    @Transactional
    public Integer insertClaimCopyList( List<PoEntity> list){
        return matchDao.insertClaimCopyList(list);
    }

    @Transactional
    public Integer insertPoListCopy1( List<PoEntity> list){
        return matchDao.insertPoListCopy(list);
    }

    public static String getNumber(String payterm) {
        String str2 = "";
        if (payterm != null && !"".equals(payterm)) {
            for (int i = 0; i < payterm.length(); i++) {
                if (payterm.charAt(i) >= 48 && payterm.charAt(i) <= 57) {
                    str2 += payterm.charAt(i);
                }

            }
        }
        return str2;
    }

    /**
     * 采购同意
     */
    public Boolean updataY(String id){
        Integer a=matchDao.updataY(id);
        if(a>0){
            return  true;
        }else{
            return  false;
        }
    }

    /**
     * 采购不同意
     */
    public Boolean updataN(String id){
        Integer a=matchDao.updataN(id);
        if(a>0){
            return  true;
        }else{
            return  false;
        }
    }
    /**
     * 删除超两年货款转收入的信息，订单号为700888、756888以及538520
     */
    public Integer delPo(){
      return matchDao.delPo();
   }

   @Override
   public List<QuestionPaperExcelEntity> toExcel(List<QuestionPaperEntity> list){
       List<QuestionPaperExcelEntity> list1=new ArrayList<>();
       for (QuestionPaperEntity qu:list) {
           QuestionPaperExcelEntity qe=new QuestionPaperExcelEntity();
           qe.setRownumber(qu.getRownumber());
           qe.setPartition(formatPartition(qu.getPartition()));
           qe.setProblemStream(qu.getProblemStream());
           qe.setPurchaser(qu.getPurchaser());
           qe.setJvcode(qu.getJvcode());
           qe.setCity(qu.getCity());
           qe.setUsercode(qu.getUsercode());
           qe.setUsername(qu.getUsername());
           qe.setTelephone(qu.getTelephone());
           qe.setDepartment(qu.getDepartment());
           qe.setStoreNbr(qu.getStoreNbr());
           qe.setInvoiceNo(qu.getInvoiceNo());
           qe.setInvoiceDate(formatDateString(qu.getInvoiceDate()));
           qe.setTotalAmount(qu.getTotalAmount());
           qe.setQuestionType(formatQuestionType(qu.getQuestionType()));
           qe.setProblemCause(formatProblemCause(qu.getProblemCause()));
           qe.setDescription(qu.getDescription());
           qe.setCreatedDate(formatDateString(qu.getCreatedDate()));
           qe.setCheckstatus(formatCheckStatus(qu.getCheckstatus()));
           qe.setCheckDate(formatDateString(qu.getCheckDate()));
           qe.setRejectDate(formatDateString(qu.getRejectDate()));
           qe.setUnPassReason(qu.getUnPassReason());
           qe.setReplyDate(formatDateString(qu.getReplyDate()));
           list1.add(qe);
       }
       return list1;
   }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String formatPartition(String partition){
        String par="—— ——";
        if(StringUtils.isNotBlank(partition)){
            if(partition.equals("1001")){
                par="SC超市";
            }else if(partition.equals("1002")){
                par="SAMS商店";
            }else if(partition.equals("1003")){
                par="99自用品";
            }else if(partition.equals("1004")){
                par="财务部";
            }else if(partition.equals("1005")){
                par="Store店";
            }else if(partition.equals("1007")){
                par="其他";
            }
        }
        return par;
    }
    private String formatQuestionType(String partition){
        String par="—— ——";
        if(StringUtils.isNotBlank(partition)){
            if(partition.equals("2001")){
                par="协议";
            }else if(partition.equals("2002")){
                par="订单单价差异";
            }else if(partition.equals("2003")){
                par="订单折扣差异";
            }else if(partition.equals("2004")){
                par="收退货数量问题";
            }else if(partition.equals("2005")){
                par="协议或年佣差异";
            }else if(partition.equals("2006")){
                par="供应商对审计金额有异议";
            }else if(partition.equals("2007")){
                par="其他";
            }else if(partition.equals("2008")){
                par="索赔单价差异（财务）";
            }
        }
        return par;
    }
    private String formatProblemCause(String partition){
        String par="—— ——";
        if(StringUtils.isNotBlank(partition)){
            if(partition.equals("100101")){
                par="采购确认";
            }else if(partition.equals("100102")){
                par="协议";
            }else if(partition.equals("100201")){
                par="订单单价差异";
            }else if(partition.equals("100301")){
                par="订单折扣差异";
            }else if(partition.equals("100401")){
                par="收货数量";
            }else if(partition.equals("100402")){
                par="退货数量";
            }else if(partition.equals("100701")){
                par="无索赔供应商";
            }else if(partition.equals("100702")){
                par="索赔冲账差异";
            }else if(partition.equals("100703")){
                par="索赔自行退货";
            }else if(partition.equals("100704")){
                par="索赔不应定案";
            }else if(partition.equals("100705")){
                par="下错税率";
            }else if(partition.equals("100706")){
                par="身份变更";
            }else if(partition.equals("100707")){
                par="下错对象";
            }else if(partition.equals("100708")){
                par="涉税问题单";
            }else if(partition.equals("100709")){
                par="逻辑关系";
            }else if(partition.equals("100801")){
                par="财务确认";
            }else if(partition.equals("100501")){
                par="模板1";
            }else if(partition.equals("100502")){
                par="模板2";
            }else if(partition.equals("100503")){
                par="模板3";
            }else if(partition.equals("100504")){
                par="模板4";
            }else if(partition.equals("100505")){
                par="其他";
            }else if(partition.equals("100601")){
                par="模板1";
            }else if(partition.equals("100601")){
                par="模板2";
            }
        }
        return par;
    }
    private String formatCheckStatus(String partition){
        String par="—— ——";
        if(StringUtils.isNotBlank(partition)){
            if(partition.equals("0")){
                par="未审核";
            }else if(partition.equals("1")){
                par="审核中";
            }else if(partition.equals("2")){
                par="审核不通过";
            }else if(partition.equals("3")){
                par="审核完成";
            }else if(partition.equals("4")){
                par="采购审批中";
            }else if(partition.equals("5")){
                par="采购已同意";
            }else if(partition.equals("6")){
                par="采购不同意";
            }
        }
        return par;
    }

}
