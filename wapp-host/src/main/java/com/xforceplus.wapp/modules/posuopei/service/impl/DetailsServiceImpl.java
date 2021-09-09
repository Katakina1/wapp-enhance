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
import com.xforceplus.wapp.modules.posuopei.dao.DetailsDao;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
            if(list.size()>0){
                for (MatchEntity entity :list){
                    String s = detailsDao.selectVenderName(entity.getVenderid());
                    entity.setVenderName(s);
                }
            }
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
    @Transactional
    @Override
    public Integer updatePodetail(BigDecimal receiptAmount,BigDecimal amountunpaid,Integer id){
        return detailsDao.updatePodetail(receiptAmount,amountunpaid,id);
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
}
