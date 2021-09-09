package com.xforceplus.wapp.modules.posuopei.service;




import com.xforceplus.wapp.modules.posuopei.entity.*;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface MatchService {


    List<InvoiceEntity> ifExist(Map<String, Object> map);

    Boolean match(MatchEntity matchEntity);


    /**
     * 带出业务字典信息
     *
     * @param
     * @return
     */
    List<OrgEntity> getDicdeta(String theKey);


    /**
     * 校验发票信息
     *
     * @param invoiceCode
     * @param invoiceNo
     * @param invoiceDate
     * @param invoiceAmount
     * @param totalAmount
     * @param taxRate
     * @param taxAmount
     * @return
     */
    Boolean checkInvoiceMessage(String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount);

    /**
     * 判断发票类型是否为普票
     *
     * @param invoiceCode
     * @return
     */
    String getFplx(String invoiceCode);

    public Integer upDatePoList(List<PoEntity> list, Integer i);

    public Integer insertPoListCopy(List<PoEntity> list);

    /**
     * 获取要批量更新的已匹配的订单数据
     *
     * @return
     */
    public Integer upDatePoListMatched(List<PoEntity> list, Integer i);

    public Integer insertPoList(List<PoEntity> list);

    public void connHostPo1(String day, int i);

    public void connHostPo2(String day, int i);

    public void connHostPo4(String day, int i);

    public void connHostClaimType2(String day, int i);

    public void connHostClaimType3(String day, int i);

    public void connHostAgain(String date1);

    public void getHostClaimDetail(Integer day);

    public JSONArray writeScreen(MatchEntity matchEntity);

    public void runWritrScreen(Long[] ids);
    public void runWritrScreen1(Long[] ids);

    public List<SubmitOutstandingReportEntity> checkWriteScreen(MatchEntity matchEntity);

    public List<InvoicesEntity> getInvoice(String venderid, String invoice_no, String invoice_amount);

    public List<MatchEntity> getMatch(String matchno);

    public Integer updateMatchHostStatus(String host_status, String id);

    public void test();

    Integer upDateClaimList(List<ClaimEntity> list, Integer i);
    int updateIsDel(String isdel,String matchno);

}
