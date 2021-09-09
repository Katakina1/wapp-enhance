package com.xforceplus.wapp.modules.posuopei.service.impl;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:06
*/

import com.aisinopdf.text.pdf.B;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.posuopei.dao.DetailsDao;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolExcelEntity;
import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@Service
public class DetailsServiceImpl implements DetailsService {

    private static final Logger LOGGER= getLogger(DetailsServiceImpl.class);

    private DetailsDao detailsDao;

    private MatchDao matchDao;
    @Autowired
    public DetailsServiceImpl(DetailsDao detailsDao,MatchDao matchDao){
        this.detailsDao=detailsDao;
        this.matchDao=matchDao;
    }

    /**
     * 获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetail(String schemaLabel, Long  id) {
        List<DetailEntity> result  = detailsDao.getInvoiceDetail(schemaLabel,id);
        return result;
    }



    /**
     * 获取转出信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    @Override
    public List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid) {
        return detailsDao.getOutInfo(schemaLabel, uuid);
    }

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public DetailVehicleEntity getVehicleDetail(String schemaLabel, Long id)throws Exception{
        return detailsDao.getVehicleDetail(schemaLabel,id);
    }

    /**
     * 获取明细中抵账表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoicesEntity getDetailInfo(String schemaLabel, Long id) {
        InvoicesEntity invoiceEntity=detailsDao.getDetailInfo(schemaLabel,id);
        invoiceEntity.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(invoiceEntity.getTotalAmount()*100)));
        return invoiceEntity;
    }


    /**
     * 获取结果明细
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public MatchEntity getResultDetail(String schemaLabel, Long id){
        return detailsDao.getResultDetail(schemaLabel,id);
    }

    @Override
    public PagedQueryResult<MatchEntity> getMatchList(Map<String, Object> params) {
        List<MatchEntity> list = Lists.newArrayList();
        final Integer count =detailsDao.getMatchCount(params);
        if(count>0){
            list=detailsDao.getMatchList(params);
        }
        //根据Matchno查询抵账发票的hostStatus,赋值至匹配查询结果的hostStatus
        for (MatchEntity me:list) {
            List<InvoicesEntity> li=detailsDao.selectHostStatus(me.getMatchno());
            if(li.size()>0){
                if(null!=li.get(0).getHostStatus()){
                    me.setHoststatus(li.get(0).getHostStatus());
                }
            }
        }
        return new PagedQueryResult<MatchEntity>(list,count);
    }

    @Override
    public Integer invoiceMatchCount(Map<String, Object> params) {
        List<MatchEntity> list = Lists.newArrayList();
        return detailsDao.getTheMatchCount(params);

    }
    @Override
    public Integer getMatchCount(Map<String, Object> params) {
        return detailsDao.getMatchCount(params);

    }
    @Override
    public List<MatchEntity> queryList(Map<String, Object> params) {
        List<MatchEntity> list = Lists.newArrayList();

            list=detailsDao.getTheMatchList(params);


        return list;
    }

    @Override
    public PagedQueryResult<MatchEntity> getTheMatchList(Map<String, Object> params) {
        List<MatchEntity> list = Lists.newArrayList();
        final Integer count =detailsDao.getTheMatchCount(params);
        if(count>0){
            list=detailsDao.getTheMatchList(params);

        }
        return new PagedQueryResult<MatchEntity>(list,count);
    }
    @Override
    public MatchEntity getMatchDetail(String matchno) {
        MatchEntity matchEntity=new MatchEntity();
        List<InvoiceEntity> invoiceEntities = matchDao.invoiceList(matchno);
        if(invoiceEntities.size()>0){
            for (InvoiceEntity entity :invoiceEntities){
                if("04".equals(CommonUtil.getFplx(entity.getInvoiceCode())) ){
                   entity.setInvoiceAmount(entity.getDkinvoiceAmount());
                }
                String s = detailsDao.selectVenderName(entity.getVenderid());

                entity.setVendername(s);
            }
        }
        matchEntity.setInvoiceEntityList(invoiceEntities);
        matchEntity.setPoEntityList(matchDao.poListDetail(matchno));
        matchEntity.setClaimEntityList(matchDao.claimList(matchno));
        return matchEntity;
    }

    @Override
    @Transactional
    public String matchCancel(String matchno) {
        String msg="取消匹配成功！";
        try{
                Boolean flag = detailsDao.cancelMatch(matchno) > 0;
                if (flag) {
                    detailsDao.cancelClaim(matchno);
                    detailsDao.cancelInvoice(matchno);
                    List<PoEntity> list = detailsDao.getPoJiLu(matchno);
                    BigDecimal changeTotal = new BigDecimal(0);
                    for (int k = 0; k < list.size(); k++) {
                        PoEntity poEntity = list.get(k);
                        detailsDao.cancelPo(poEntity.getId(), poEntity.getChangeAmount(), "6");
                    }
                } else {
                    msg = "该条匹配无法取消！";
                }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }



        return msg;
    }
    @Override
    @Transactional
    public String submitMatchCancel(String matchno) {
        String msg="取消匹配成功！";
        try{
                Boolean  flag = detailsDao.cancelMatch(matchno) > 0;
                if (flag) {
                    detailsDao.cancelClaim(matchno);
                    detailsDao.cancelInvoice(matchno);
                    List<PoEntity> list = detailsDao.getPoJiLu(matchno);
                    BigDecimal changeTotal = new BigDecimal(0);
                    for (int k = 0; k < list.size(); k++) {
                        PoEntity poEntity = list.get(k);
                        detailsDao.cancelPo(poEntity.getId(), poEntity.getChangeAmount(), "6");
                    }
                } else {
                    msg = "该条匹配无法取消！";
                }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }



        return msg;
    }
    @Override
    public List<String> getImg(String matchno) {
        return detailsDao.getImg(matchno);
    }



    @Override
    public void exportPoPdf(Map<String, Object> map, HttpServletResponse response) {

        String fileName = "invoicePoPDF"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        try {
            String html = PdfUtils.getPdfContent("invoicePoPDF.ftl", map);
            OutputStream out = null;
            ITextRenderer render = null;
            out = response.getOutputStream();

            render = PdfUtils.getRender();
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(out);
            render.finishPDF();
            render = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void exportChaXunPdf(Map<String,Object> map, HttpServletResponse response){
        String fileName = "invoicePoChaXunPDF"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        try {
            String html = PdfUtils.getPdfContent("invoicePoChaXunPDF.ftl", map);
            OutputStream out = null;
            ITextRenderer render = null;
            out = response.getOutputStream();

            render = PdfUtils.getRender();
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(out);
            render.finishPDF();
            render = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public MatchEntity selectMatchEntity(String matchno) {
        return detailsDao.selectMatchEntity(matchno);
    }

    @Override
    public PoEntity selectPoDetail(String receiptid){
        return detailsDao.selectPoDetail(receiptid);
    }

    @Override
    public PoEntity selectPo(String pocode){
        return null;
    }
    @Override
    public Integer updatePo(BigDecimal receiptAmount,BigDecimal amountunpaid,Integer id){
        return null;
    }

    /*@Override
    public String selectVenderName(String venderid) {
        return detailsDao.selectVenderName(venderid);
    }*/
    @Override
    public List<MatchEntity> queryListAll(Map<String,Object> params){
      return  detailsDao.queryListAll(params);
    }
    @Override
    public List<MatchExcelEntity> transformExcle(List<MatchEntity> list){
        List<MatchExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            MatchEntity entity = list.get(i);
            MatchExcelEntity matchExcelEntity = new MatchExcelEntity();


            //序号
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
           matchExcelEntity.setGfTaxNo( entity.getGfTaxNo());
           matchExcelEntity.setVenderid(  entity.getVenderid());
           matchExcelEntity.setVenderName(  entity.getVenderName());
           matchExcelEntity.setInvoiceAmount(  formatAmount(entity.getInvoiceAmount().toString()));
           matchExcelEntity.setInvoiceNum(  entity.getInvoiceNum()+"");
           matchExcelEntity.setPoAmount(  formatAmount(entity.getPoAmount().toString()));
           matchExcelEntity.setPoNum(  entity.getPoNum()+"");
           matchExcelEntity.setClaimAmount(  formatAmount(entity.getClaimAmount().toString()));
           matchExcelEntity.setClaimNum(  entity.getClaimNum()+"");
           matchExcelEntity.setMatchDate(  formatDate(entity.getMatchDate()));
           matchExcelEntity.setSettlementamount(  formatAmount(entity.getSettlementamount().toString()));
           matchExcelEntity.setWalmartStatus(walmartStatus(entity.getHoststatus()));
           matchExcelEntities.add(matchExcelEntity);
        }
        return matchExcelEntities;
    }
    @Override
    public List<DetailInvExcelEntity> transformInvExcle(List<InvoiceEntity> list){
        List<DetailInvExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            InvoiceEntity entity = list.get(i);
            DetailInvExcelEntity matchExcelEntity = new DetailInvExcelEntity();
            //序号
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
            matchExcelEntity.setInvoiceCode(entity.getInvoiceCode());
            matchExcelEntity.setInvoiceNo(entity.getInvoiceNo());
            matchExcelEntity.setInvoiceAmount(formatAmount(entity.getInvoiceAmount().toString()));
            matchExcelEntity.setInvoiceTaxAmount(formatAmount(entity.getTaxAmount().toString()));
            matchExcelEntity.setInvoiceTotal(formatAmount(entity.getTotalAmount().toString()));
            matchExcelEntity.setTaxTate(formatAmount(entity.getTaxRate().toString()));
            matchExcelEntity.setVenderId(entity.getVenderid());
            matchExcelEntity.setVenderName(entity.getVendername());
            matchExcelEntity.setCreateDate(entity.getInvoiceDate().substring(0,10));
            matchExcelEntities.add(matchExcelEntity);

        }
        return matchExcelEntities;
    }
    @Override
    public List<DetailPoExcelEntity> transformPoExcle(List<PoEntity> list){
        List<DetailPoExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            PoEntity entity = list.get(i);
            DetailPoExcelEntity matchExcelEntity = new DetailPoExcelEntity();
            //序号
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
            matchExcelEntity.setPoCode(entity.getPocode());
            matchExcelEntity.setPoAmount(formatAmount(entity.getReceiptAmount().toString()));
            matchExcelEntity.setReceipti(entity.getReceiptid());
            matchExcelEntity.setReceiptiDate(formatDate(entity.getReceiptdate()));
            matchExcelEntity.setReceiptiAmount(formatAmount(entity.getReceiptAmount().toString()));
            matchExcelEntity.setYiJieAmount(formatAmount(entity.getAmountpaid().toString()));
            matchExcelEntity.setWeiJieAmount(entity.getAmountunpaid().toString());
            matchExcelEntities.add(matchExcelEntity);
        }
        return matchExcelEntities;
    }
    @Override
    public List<DetailClaimExcelEntity> transformClaimExcle(List<ClaimEntity> list){
        List<DetailClaimExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            ClaimEntity entity = list.get(i);
            DetailClaimExcelEntity matchExcelEntity = new DetailClaimExcelEntity();
            //序号
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
            matchExcelEntity.setClaimCode(entity.getClaimno());
            matchExcelEntity.setClaimAmount(formatAmount(entity.getClaimAmount().toString()));
            matchExcelEntity.setClaimDate(formatDate(entity.getPostdate()));
            matchExcelEntities.add(matchExcelEntity);
        }
        return matchExcelEntities;
    }
    @Override
    public String selectMatchNo(String uuid){
       return detailsDao.selectMatchNo(uuid);
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
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String walmartStatus(String hostStatus){
        String value="";
        if(StringUtils.isEmpty(hostStatus)){
            value="—— ——";
        }else{
            if("0".equals(hostStatus)){
                value="未处理";
            }else if("1".equals(hostStatus)){
                value="未处理";
            }else if("10".equals(hostStatus)){
                value="已处理";
            }else if("13".equals(hostStatus)){
                value="已删除";
            }else if("14".equals(hostStatus)){
                value="待付款";
            }else if("11".equals(hostStatus)){
                value="已匹配";
            }else if("12".equals(hostStatus)){
                value="已匹配";
            }else if("15".equals(hostStatus)){
                value="已付款";
            }else if("19".equals(hostStatus)){
                value="已付款";
            }else if("9".equals(hostStatus)){
                value="待付款";
            }else if("99".equals(hostStatus)){
                value="已付款";
            }else if("999".equals(hostStatus)){
                value="已付款";
            }else{
                value="未处理";
            }
        }
        return value;
    }

}
