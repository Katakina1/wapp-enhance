package com.xforceplus.wapp.modules.posuopei.dao;

        import com.aisinopdf.text.I;
        import com.aisinopdf.text.pdf.S;

        import com.xforceplus.wapp.modules.posuopei.entity.*;
        import io.swagger.models.auth.In;
        import org.apache.ibatis.annotations.Mapper;
        import org.apache.ibatis.annotations.Param;

        import java.math.BigDecimal;
        import java.util.Date;
        import java.util.List;
        import java.util.Map;

@Mapper
public interface MatchDao {
    List<PoEntity> poQueryList(Map<String, Object> map);

    Integer poQueryCount(Map<String, Object> map);

    Integer cancelInvoiceMatch(@Param("matchno") String matchno);

    List<ClaimEntity> claimQueryList(Map<String, Object> map);

    Integer claimQueryCount(Map<String, Object> map);

    List<InvoiceEntity> invoiceQueryList(Map<String, Object> map);

    List<InvoiceEntity> invoiceQueryListPP(Map<String, Object> map);

    Integer invoiceQueryCount(Map<String, Object> map);

    Integer saveInvoice(Map<String, Object> map);

    Integer saveInvoicePP(Map<String, Object> map);

    List<InvoiceEntity> ifExist(Map<String, Object> map);

    List<OrgEntity> getGfNameAndTaxNo(@Param("userId") Long userId);

    OrgEntity getDefaultMessage(@Param("userId") Long userId);

    Integer update(@Param("id") Long id, @Param("taxRate") BigDecimal taxRate);

    String getCompanyCode(@Param("jvcode") String jvcode);

    /**
     * 覆盖
     *
     * @param map
     * @return
     */
    Integer allUpdate(Map<String, Object> map);

    Integer allUpdatePP(Map<String, Object> map);

    List<InvoiceEntity> invoiceList(@Param("matchno") String matchno);

    List<PoEntity> hostPoList(@Param("matchno") String matchno);

    //    List<PoEntity>poList(@Param("matchno") String matchno);
    List<PoEntity> poListDetail(@Param("matchno") String matchno);

    List<ClaimEntity> claimList(@Param("matchno") String matchno);

    Integer updateInvioceMatch(Map<String, Object> map);

    Integer updatePoMatch(Map<String, Object> map);

    Integer updateCliamMatch(Map<String, Object> map);

    Integer insertMatch(@Param("entity") MatchEntity matchEntity);

    Integer insertSonOfMatch(Map<String, Object> map);

    Integer updateMatchMatchno(Map<String, Object> map);

    Integer updateCover(@Param("id") Integer id, @Param("cover") BigDecimal cover);

    Integer updateVenderid(Map<String, Object> map);

    BigDecimal checkPo(Map<String, Object> map);

    Integer checkClaim(Map<String, Object> map);

    OrgEntity getGfTaxNo(@Param("orgcode") String orgcode);

    BigDecimal getPoUnPay(Map<String, Object> map);

    List<PoEntity> getReceiptList(@Param("poCode") String poCode);

    OrgEntity getXfMessage(@Param("venderid") String venderid);

//    Integer updatePoFather (@Param("pocode")String pocode);

    /**
     * 获取分区
     *
     * @param
     * @return
     */
    List<OrgEntity> getPartion(@Param("theKey") String theKey);


    /**
     * 获取业务字典
     *
     * @param
     * @return
     */
    List<OrgEntity> getDicdeta(@Param("theKey") String theKey);


    /**
     * 保存审核结果
     *
     * @param param
     * @return
     */
    Integer check(Map<String, Object> param);

    Integer hostUpdatePo(@Param("entity") PoEntity poEntity);

    Integer hostUpdateClaim(@Param("entity") PoEntity poEntity);


    String getPoMatchNo(@Param("entity") PoEntity poEntity);

    String getClaimMatchNo(@Param("entity") PoEntity poEntity);

    Integer insertPo(@Param("entity") PoEntity poEntity);


    Integer insertClaimFather(@Param("entity") PoEntity poEntity);

    Integer insertClaimDetail(@Param("entity") ClaimDetailEntity claimDetailEntity);


    HostPoEntity ifAddPoExist(@Param("entity") PoEntity poEntity);

    Integer ifAddClaimExist(@Param("entity") PoEntity poEntity);

    Integer upDateInvoiceHostStatus(@Param("invoiceNo") String invoiceNo, @Param("hostStatus") String hostStatus, @Param("invoiceDate") String invoiceDate, @Param("vendor") String vendor, @Param("hostDate") Date hostDate);

    String getInvoiceMatchno(@Param("invoiceNo") String invoiceNo, @Param("hostStatus") String hostStatus, @Param("invoiceDate") String invoiceDate, @Param("vendor") String vendor);

    List<MatchEntity> getMatchLists();
    List<MatchEntity> getMatchListsAm();
    Integer updateWriteScreenType(@Param("theKey") String theKey);

    List<MatchEntity> getChooseMatchLists(@Param("ids") Long[] ids);

    Date getDueDate(@Param("matchno") String matchno);

    List<InvoicesEntity> getInvoice(@Param("venderid") String venderid, @Param("invoice_no") String invoice_no, @Param("invoice_amount") String invoice_amount);

    List<MatchEntity> getMatch(@Param("matchno") String matchno);

    Integer updateMatchHostStatus(@Param("host_status") String host_status, @Param("id") String id);


    Integer insertTaskLog(@Param("taskName") String taskName, @Param("result") String result, @Param("exception") String exception);

    Integer checkInvoiceMatchStatus(@Param("uuid") String uuid);

    Integer upDateHostStatus(@Param("matchno") String matchno,@Param("hostStatus") String hostStatus);

    Integer revertDateHostStatus(@Param("matchno") String matchno);

    Integer hostUpdatePoStatus(@Param("entity") PoEntity poEntity);

    Integer ifUPPoHostStatus(@Param("entity") PoEntity poEntity);


    Integer updatePoDueDate(@Param("dueDate") Date dueDate, @Param("tractionIdSeq") String tractionIdSeq);

    Integer getMatchHostStatus(@Param("matchno") String matchno);

    Integer hostUpdatePoAmount(@Param("entity") PoEntity poEntity);


    /**
     * 批量插入订单数据
     *
     * @param list
     * @return
     */
    Integer insertPoList(@Param("List") List<PoEntity> list);

    /**
     * 批量插入订单数据
     *
     * @param list
     * @return
     */
    Integer insertPoListCopy(@Param("List") List<PoEntity> list);

//    /**
//     * 批量更新订单数据
//     * @param list
//     * @return
//     */
//    Integer updatePoList(@Param("List") List<PoEntity> list);

    /**
     * 获取要批量更新的已经匹配的订单数据
     *
     * @return
     */
    List<PoEntity> getPoCopyList();

    /**
     * 获取要批量更新的未匹配的订单数据
     *
     * @return
     */
    List<PoEntity> getPoCopyList1();


    /**
     * 获取要批量插入的订单数据
     *
     * @return
     */
    List<PoEntity> getPoCopyList2();


    /**
     * 获取要批量插入的订单数据test
     *
     * @return
     */

    List<PoEntity> getPoCopyList3();

    /**
     * 删除订单临时表
     *
     * @return
     */
    Integer deletePoCopy();

    Integer hostUpdatePoTraction(@Param("list") List<PoEntity> list);

    Integer hostUpdatePoStatusTraction(@Param("list") List<PoEntity> list);

    /**
     * 插入索赔临时表
     * @param list
     * @return
     */
    Integer insertClaimCopyList(@Param("list") List<PoEntity> list);


    /**
     * 插入索赔表
     * @param list
     * @return
     */
    Integer insertClaimList(@Param("list") List<ClaimEntity> list);
    /**
     * 更新索赔表
     * @param list
     * @return
     */
    Integer updateClaimList(@Param("list") List<ClaimEntity> list);

    /**
     * 删除索赔临时表数据
     * @return
     */
    Integer deleteClaimCopy();

    /**
     * 获取要插入的索赔数据
     * @return
     */
    List<ClaimEntity> getClaimCopyList();


    /**
     * 获取要更新的索赔数据
     * @return
     */
    List<ClaimEntity> getClaimCopyList1();

    List<PoEntity> getClaimCopyList2();

    /**
     * 调用存储过程
     * @return
     */
    Integer transferProcPo();

    /**
     * 调用存储过程
     * @return
     */
    Integer transferProcClaim();

    /**
     * 调整订单已结金额
     */
    Integer adjustment();
    /**
     * 删除超两年货款转收入的信息，订单号为700888、756888以及538520
     */
    Integer delPo();

    Integer insertRobotMessage(@Param("entity") RobotMessageEntity robotMessageEntity);
    String selectRequestMessage(@Param("messageId")String messageId);
    Integer updateRobotMessage(@Param("entity") RobotMessageEntity robotMessageEntity);
    int updateIsDel(@Param("isdel") String isdel,@Param("matchno") String matchno);
}
