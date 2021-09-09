package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.InformationInquiry.dao.QuestionnaireDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;

import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireOrLeadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.QuestionnaireImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.QuestionnaireService;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.export.InvoiceImport;
import com.xforceplus.wapp.modules.redTicket.service.impl.EntryRedTicketServiceImpl;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import javax.xml.crypto.Data;

import static org.slf4j.LoggerFactory.getLogger;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    private static final Logger LOGGER = getLogger(QuestionnaireServiceImpl.class);


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private QuestionnaireDao questionnaireDao;
    @Autowired
    private DetailsService detailsService;
    @Override
    public List<QuestionnaireEntity> questionnairelist(Map<String, Object> map){
        return questionnaireDao.questionnairelist(map);
    }
    @Override
    public List<QuestionnaireEntity> questionnairelistAll(Map<String, Object> map){
        return questionnaireDao.questionnairelistAll(map);
    }

    @Override
    public Integer questionnairelistCount(Map<String, Object> map){
        return questionnaireDao.questionnairelistCount(map);
    }

    @Override
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {
        final QuestionnaireImport invoiceImport = new QuestionnaireImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            int index = 0;
            List currentList= Lists.newArrayList();
            final List<QuestionnaireOrLeadEntity> certificationEntityList = invoiceImport.analysisExcel();
            index = certificationEntityList.size();
            if (certificationEntityList.size()>0) {
                certificationEntityList.forEach(importEntity->{
                    Map<String,Object> mapps= Maps.newHashMapWithExpectedSize(10);
                    mapps.put("ids",importEntity.getIds());
                    mapps.put("isDel",importEntity.getIsDel());
                    mapps.put("dateT",importEntity.getDateT());
                    mapps.put("inputUser",importEntity.getInputUser());
                    mapps.put("jV",importEntity.getjV());
                    mapps.put("vendorNo",importEntity.getVendorNo());
                    mapps.put("invNo",importEntity.getInvNo());
                    mapps.put("invoiceCost",importEntity.getInvoiceCost());
                    mapps.put("wMCost",importEntity.getwMCost());
                    mapps.put("batchID",importEntity.getBatchID());
                    mapps.put("pONo",importEntity.getpONo());
                    mapps.put("trans",importEntity.getTrans());
                    mapps.put("rece",importEntity.getRece());
                    mapps.put("errCode",importEntity.getErrCode());
                    mapps.put("errDesc",importEntity.getErrDesc());
                    mapps.put("errStatus",importEntity.getErrStatus());
                    mapps.put("invoiceDate",importEntity.getInvoiceDate());

                    //mapps.put("venderid",params.get("venderid"));
                    //mapps.put("jvcode",params.get("jvcode"));
                    //mapps.put("gfName",params.get("gfName"));
                    //mapps.put("xfTaxno",params.get("xfTaxno"));
                    //mapps.put("checkNo",importEntity.getId());
                    if(importEntity.getIds() == 0){
                        questionnaireDao.saveInvoice(mapps);
                        if (importEntity.getIsDel() == "1"){
                            inputrefundyesno(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }else if(importEntity.getIsDel() == "0"){
                            cancelTheRefund(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }else if(importEntity.getIsDel() == "2"){
                            invoiceCl(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }
                    }else{
                        questionnaireDao.questionnaireUpdate(mapps);
                        if (importEntity.getIsDel() == "1"){
                            inputrefundyesno(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }else if(importEntity.getIsDel() == "0"){
                            cancelTheRefund(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }else if(importEntity.getIsDel() == "2"){
                        invoiceCl(importEntity.getInvNo(),importEntity.getVendorNo(),importEntity.getInvoiceDate(),importEntity.getErrStatus(),importEntity.getInvoiceCost());
                        }else if(importEntity.getIsDel() == "3"){
                           detailsService.submitMatchCancel(importEntity.getBatchID());
                        }
                    }

                });
                map.put("invoiceQueryList",currentList);
                map.put("success", Boolean.TRUE);
                map.put("reason", "批量导入成功！总共导入{"+index+"}条");
            }else {
                LOGGER.info("读取到excel数据格式有误");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel数据格式有误！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }


        return map;
    }

    @Override
    public void inputrefundyesno(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String invoiceDate1="";
        if(invoiceDate!=null){
            invoiceDate1=sdf.format(invoiceDate);
        }
        String invoiceNo ="";
        if(StringUtils.isNotEmpty(invNo)){
            DecimalFormat g1=new DecimalFormat("00000000");
            invoiceNo = g1.format(Integer.valueOf(invNo));
        }
        int i =  questionnaireDao.inputrefundyesno(invoiceNo,vendorNo,invoiceDate1,errStatus,invoiceCost);
        if(i == 1){
            questionnaireDao.queryuuids(invNo,vendorNo,invoiceDate1,errStatus);
        }
        String uuid=questionnaireDao.getUuId(invoiceNo,vendorNo,invoiceDate1,invoiceCost);
        String matchno=questionnaireDao.queryMatchno(uuid);
        if(StringUtils.isNotEmpty(matchno)){
            questionnaireDao.updateIsDel("1",matchno);
        }


    }
    @Override
    public void invoiceCl(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String invoiceDate1="";
        if(invoiceDate!=null){
            invoiceDate1=sdf.format(invoiceDate);
        }
        String invoiceNo ="";
        if(StringUtils.isNotEmpty(invNo)){
            DecimalFormat g1=new DecimalFormat("00000000");
            invoiceNo = g1.format(Integer.valueOf(invNo));
        }
        int i =  questionnaireDao.invoiceCl(invoiceNo,vendorNo,invoiceDate1,errStatus,invoiceCost);
    }
    /***
     * 撤销退票
     */
    @Override
    public void cancelTheRefund(String invNo, String vendorNo, Date invoiceDate, String errStatus,String invoiceCost) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String invoiceDate1="";
        if(invoiceDate!=null){
            invoiceDate1=sdf.format(invoiceDate);
        }
        String invoiceNo ="";
        if(StringUtils.isNotEmpty(invNo)){
            DecimalFormat g1=new DecimalFormat("00000000");
            invoiceNo = g1.format(Integer.valueOf(invNo));
        }
        int i =  questionnaireDao.cancelTheRefund(invoiceNo,vendorNo,invoiceDate1,errStatus,invoiceCost);
        if(i == 1){
            questionnaireDao.cancelQueryuuids(invNo,vendorNo,invoiceDate1,errStatus);
        }
        String uuid=questionnaireDao.getUuId(invoiceNo,vendorNo,invoiceDate1,invoiceCost);
        String matchno=questionnaireDao.queryMatchno(uuid);
        if(StringUtils.isNotEmpty(matchno)){
            questionnaireDao.updateIsDel("0",matchno);
        }

    }

    @Override
    public List<QuestionnaireEntity> queryuuid(Long id) {


        return questionnaireDao.queryuuid(id);
    }
//    @Override
//    public void queryuuids(Long id) {
//        questionnaireDao.queryuuids(id);
//    }

    @Override
    public void xqueryuuids(Long id) {
        questionnaireDao.xqueryuuids(id);
    }

    /***
     * 撤销处理
     * @param id
     */
    @Override
    public void cancelTheProcess(String id) {
        questionnaireDao.cancelTheProcess(id);
    }

    @Override
    public void xqueryuuidss(Long id) {
        questionnaireDao.xqueryuuidss(id);
    }

    /***
     * 撤销处理
     * @param id
     */
    @Override
    public void cancelTheProcesss(String id) {
        questionnaireDao.cancelTheProcesss(id);
    }

    @Override
    public List<QuestionnaireExcelEntity> transformExcle(List<QuestionnaireEntity> list){
        List<QuestionnaireExcelEntity> list2=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            QuestionnaireEntity entity=list.get(i);
            QuestionnaireExcelEntity entity1=new QuestionnaireExcelEntity();
            entity1.setRownumber0( entity.getId()+"");
            entity1.setRownumber1(entity.getRownumber());
            entity1.setCell1(  getisDel(entity.getIsDel()));
            entity1.setCell2( formatDate(entity.getDateT()));
            entity1.setCell3(  entity.getInputUser());
            entity1.setCell4(  entity.getjV());
            entity1.setCell5(  entity.getVendorNo());
            entity1.setCell6(  entity.getInvNo());
            entity1.setCell7(  formatAmount(entity.getInvoiceCost()));
            entity1.setCell8(  formatAmount(entity.getTaxAmount()));
            entity1.setCell9( formatAmount(entity.getTaxRate()));
            entity1.setCell10(  entity.getTaxType());
            entity1.setCell11(  formatAmount(entity.getwMCost()));
            entity1.setCell12(  entity.getBatchID());
            entity1.setCell13(  entity.getpONo());
            entity1.setCell14(  entity.getTrans());
            entity1.setCell15(  entity.getRece());
            entity1.setCell16(  errCode(entity.getIsDel(),entity.getErrCode()));
            entity1.setCell17(  errCode(entity.getIsDel(),entity.getErrDesc()));
            entity1.setCell18(  errCode(entity.getIsDel(),entity.getErrStatus()));
            entity1.setCell19(  formatDate(entity.getInvoiceDate()));
            list2.add(entity1);
        }
        return list2;
    }
    @Override
    public String queryMatchno(String uuid){
        return questionnaireDao.queryMatchno(uuid);
    }
    @Override
    public int updateIsDel(String isdel,String matchno){
        return questionnaireDao.updateIsDel(isdel,matchno);
    }
    @Override
    public String getUuId(String invNo, String vendorNo, Date invoiceDate,String invoiceCost){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String invoiceDate1="";
        if(invoiceDate!=null){
            invoiceDate1=sdf.format(invoiceDate);
        }
        return  questionnaireDao.getUuId(invNo,vendorNo,invoiceDate1,invoiceCost);
    }
    @Override
    public String getBatchId(Long id){
     return   questionnaireDao.getBatchId(id);
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String getisDel(String getisdel){
        String value="";
        if("0".equals(getisdel)){
            value="未处理";
        }else if("1".equals(getisdel)){
            value="已退票";
        }else if("2".equals(getisdel)){
            value="已处理";
        }else if("3".equals(getisdel)){
            value="需重匹";
        }else if("4".equals(getisdel)){
            value="已重匹";
        }else{
            value="未处理";
        }
        return value;
    }
    private String errCode(String getisdel,String code){
        String value="";
        if("2".equals(getisdel)){
            value="";
        }else{
            value=code;
        }
        return value;
    }

    private String fromAmount(Double d){
        BigDecimal b=new BigDecimal(d);
        DecimalFormat df=new DecimalFormat("######0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(b);
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
