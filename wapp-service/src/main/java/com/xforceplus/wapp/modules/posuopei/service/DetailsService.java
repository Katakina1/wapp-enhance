package com.xforceplus.wapp.modules.posuopei.service;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:05
*/


import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DetailsService {

    /**
     * 获取明细表信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    List<DetailEntity> getInvoiceDetail(String schemaLabel, Long id);


    /**
     * 获取转出信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid);


    /**
     * 获取机动车销售发票明细
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    DetailVehicleEntity getVehicleDetail(String schemaLabel, Long id) throws Exception;

    /**
     * 获取明细中抵账表销方购方明细信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoicesEntity getDetailInfo(String schemaLabel, Long id);

    /**
     * 获取结果明细
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    MatchEntity getResultDetail(String schemaLabel, Long id);

    /**
     * 匹配查询
     *
     * @param params
     * @return
     */
    PagedQueryResult<MatchEntity> getMatchList(Map<String, Object> params);
    Integer invoiceMatchCount(Map<String, Object> params);
    Integer getMatchCount(Map<String, Object> params);
    List<MatchEntity> queryList(Map<String, Object> params);
    /**
     * 匹配查询专用
     *
     * @param params
     * @return
     */
    PagedQueryResult<MatchEntity> getTheMatchList(Map<String, Object> params);

    MatchEntity getMatchDetail(String matchno);

    String matchCancel(String matchno);
    String submitMatchCancel(String matchno);
    List<String> getImg(String matchno);

    void exportPoPdf(Map<String,Object> params, HttpServletResponse response);

    void exportChaXunPdf(Map<String,Object> params, HttpServletResponse response);

    MatchEntity selectMatchEntity(String matchno);

    PoEntity selectPoDetail(String receiptid);
    PoEntity selectPo(String pocode);
    Integer updatePo(BigDecimal receiptAmount, BigDecimal amountunpaid, Integer id);

    //String selectVenderName(String venderid);

    //导出
    List<MatchEntity> queryListAll(Map<String,Object> params);

    List<MatchExcelEntity> transformExcle(List<MatchEntity> list);
    List<DetailInvExcelEntity> transformInvExcle(List<InvoiceEntity> list);
    List<DetailPoExcelEntity> transformPoExcle(List<PoEntity> list);
    List<DetailClaimExcelEntity> transformClaimExcle(List<ClaimEntity> list);
    String selectMatchNo(String uuid);
}