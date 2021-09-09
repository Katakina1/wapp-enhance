package com.xforceplus.wapp.modules.posuopei.dao;


import com.xforceplus.wapp.interfaceBPMS.Table;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
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
    List<InvoiceEntity> ifExist(Map<String, Object> map) ;

    List<OrgEntity> getGfNameAndTaxNo(@Param("userId") Long userId);

    OrgEntity getDefaultMessage(@Param("userId") Long userId);

    Integer update(@Param("id") Long id, @Param("taxRate")BigDecimal taxRate);

    String getCompanyCode(@Param("jvcode") String jvcode);

    /**
     * 覆盖
     * @param map
     * @return
     */
    Integer allUpdate(Map<String, Object> map);

    Integer allUpdatePP(Map<String, Object> map);

    List<InvoiceEntity>invoiceList(@Param("matchno") String matchno);

    List<PoEntity>hostPoList(@Param("matchno") String matchno);
//    List<PoEntity>poList(@Param("matchno") String matchno);
    List<PoEntity>poListDetail(@Param("matchno") String matchno);
    List<ClaimEntity>claimList(@Param("matchno") String matchno);

    Integer updateInvioceMatch(Map<String, Object> map);
    Integer updateSubmit(@Param("invNo") String invNo,@Param("vendorNo") String venNo);
    Integer updatePoMatch(Map<String, Object> map);
    Integer updateCliamMatch(Map<String, Object> map);
    Integer insertMatch(@Param("entity")MatchEntity matchEntity);
    Integer insertSonOfMatch(Map<String, Object> map);
    Integer updateMatchMatchno(Map<String, Object> map);
    Integer updateCover(@Param("id") Integer id,@Param("cover") BigDecimal cover);

    Integer updateVenderid(Map<String, Object> map);

    BigDecimal checkPo(Map<String, Object> map);

    Integer checkClaim(Map<String, Object> map);

    OrgEntity getGfTaxNo(@Param("orgcode") String orgcode);

    BigDecimal getPoUnPay(Map<String, Object> map);

    List<PoEntity> getReceiptList(@Param("poCode") String poCode,@Param("venId") String venId,@Param("jvcode") String jvcode);

    OrgEntity getXfMessage(@Param("venderid") String venderid);

//    Integer updatePoFather (@Param("pocode")String pocode);
    /**
     * 获取分区
     * @param
     * @return
     */
    List<OrgEntity> getPartion(@Param("theKey")String theKey);

    /**
     * 获取城市
     * @param
     * @return
     */
    List<Table> getCity();


    /**
     * 获取业务字典
     * @param
     * @return
     */
    List<OrgEntity> getDicdeta(@Param("theKey")String theKey);

    /**
     * 保存采购问题单
     * @param questionPaperEntity
     * @return true/false
     */
    Integer saveQuestionPaper(@Param("entity")QuestionPaperEntity questionPaperEntity);
    /**
     * 修改采购问题单
     * @param questionPaperEntity
     * @return true/false
     */
    Integer updateQuestionPaper(@Param("entity")QuestionPaperEntity questionPaperEntity);
    Integer deleteQuestionDetail(@Param("id")Integer id);
    Integer deleteQuestionAttchment(@Param("id")Integer id);
    Integer deleteQuestion(@Param("id")Integer id);
    Integer saveOther(@Param("entity")OtherQuestionEntity object);
    Integer saveQuestionCount(@Param("entity")CountQuestionEntity object);
    Integer saveQuestionPo(@Param("entity")PoQuestionEntity object);
    Integer saveQuestionClaim(@Param("entity")ClaimtQuestionEntity object);
    Integer saveQuestionPoDiscount(@Param("entity")PoDiscountQuestionEntity object);
    List<QuestionPaperEntity> questionPaperQuery(Map<String,Object> map);
    Integer questionPaperQueryCount(Map<String,Object> map);

    List<Object> otherPaperQuery(Map<String,Object> map);
    List<Object> poDiscountPaperQuery(Map<String,Object> map);
    List<Object> countPaperQuery(Map<String,Object> map);
    List<Object> claimPaperQuery(Map<String,Object> map);
    List<Object> poPaperQuery(Map<String,Object> map);
    public Integer saveFile(@Param("entity")SettlementFileEntity fileEntity);

    public SettlementFileEntity getFileInfo(@Param("id") Long id);

    /**
     * 保存审核结果
     * @param param
     * @return
     */
    Integer check(Map<String,Object> param);

    Integer checkTwo(Map<String,Object> param);

    Integer cheXiao(Map<String,Object> param);



    Integer hostUpdateClaim(@Param("entity") PoEntity poEntity);


    String getPoMatchNo(@Param("entity") PoEntity poEntity);

    String getClaimMatchNo(@Param("entity") PoEntity poEntity);

    Integer insertPo(@Param("entity") PoEntity poEntity);

//    Integer insertPoFather(@Param("entity") PoEntity poEntity);
//    Integer insertUATPoFather(@Param("entity") PoEntity poEntity);

    Integer insertClaimFather(@Param("entity") PoEntity poEntity);
    Integer insertClaimDetail(@Param("entity") ClaimDetailEntity claimDetailEntity);

//    Integer upDatePoFather(@Param("pocode") String  pocode, @Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus,@Param("receiptdate") Date receiptdate);
//    Integer upUATDatePoFather(@Param("pocode") String  pocode, @Param("unChangeAmount")BigDecimal unChangeAmount,@Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus,@Param("receiptdate") Date receiptdate);

//    Integer ifAddPoFatherAmount(@Param("entity") PoEntity poEntity);
//    Integer ifAddPoFatherExist(@Param("entity") PoEntity poEntity);
    HostPoEntity ifAddPoExist(@Param("entity") PoEntity poEntity);
    Integer ifAddClaimExist(@Param("entity") PoEntity poEntity);

    Integer upDateInvoiceHostStatus(@Param("invoiceNo") String  invoiceNo,@Param("hostStatus") String hostStatus,@Param("invoiceDate") String invoiceDate ,@Param("vendor") String vendor,@Param("hostDate") Date hostDate);
    String getInvoiceMatchno(@Param("invoiceNo") String  invoiceNo,@Param("hostStatus") String hostStatus,@Param("invoiceDate") String invoiceDate ,@Param("vendor") String vendor);

    List<MatchEntity>getMatchLists();

    Date getDueDate(@Param("matchno") String matchno);

    List<InvoicesEntity>getInvoice(@Param("venderid") String venderid,@Param("invoice_no") String invoice_no,@Param("invoice_amount") String invoice_amount);
    List<MatchEntity>getMatch(@Param("matchno") String matchno);
    Integer updateMatchHostStatus(@Param("host_status") String host_status,@Param("id") String id);

    QuestionPaperEntity getPaperById(Map<String,Object> param);

    Integer insertTaskLog(@Param("taskName") String taskName,@Param("result") String result,@Param("exception") String exception);

    Integer checkInvoiceMatchStatus(@Param("uuid") String uuid);

    Integer upDateHostStatus(@Param("matchno") String matchno);
    Integer hostUpdatePoStatus(@Param("entity") PoEntity poEntity);
    Integer ifUPPoHostStatus(@Param("entity") PoEntity poEntity);

    List<SettlementFileEntity> getFileList(String id);

    Integer updatePoDueDate(@Param("dueDate") Date dueDate,@Param("tractionIdSeq") String tractionIdSeq);
    Integer getMatchHostStatus(@Param("matchno") String matchno);
    Integer hostUpdatePoAmount(@Param("entity") PoEntity poEntity);

    /**
     * 插入索赔临时表
     * @param list
     * @return
     */
    Integer insertClaimCopyList(@Param("list") List<PoEntity> list);

    /**
     * 删除索赔临时表数据
     * @return
     */
    Integer deleteClaimCopy();

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
     * 批量插入订单数据
     *
     * @param list
     * @return
     */
    Integer insertPoListCopy(@Param("List") List<PoEntity> list);

    /**
     * 删除订单临时表
     *
     * @return
     */
    Integer deletePoCopy();

    /**
     * 校验订单临时表占用状态
     */
    String checkPoSupplement();

    /**
     * 校验索赔临时表占用状态
     */
    String checkClaimSupplement();

    /**
     * 开启索赔临时表
     */
    Integer onClaimSupplement();
    /**
     * 开启订单临时表
     */
    Integer onPoSupplement();

    /**
     * 关闭索赔临时表
     */
    Integer offClaimSupplement();
    /**
     * 关闭订单临时表
     */
    Integer offPoSupplement();

    /**
     * 调整订单已结金额
     */
    Integer adjustment();

    /**
     * 采购同意
     */
    Integer updataY(@Param("id")String id);

    /**
     * 采购不同意
     */
    Integer updataN(@Param("id")String id);

    /**
     * 导入匹配关系时获取索赔对象
     * @param map
     * @return
     */
    List<ClaimEntity> claimQueryWillBeMactch(Map<String, Object> map);

    /**
     * 查询最大问题单流水号
     * */
    QuestionPaperEntity querymaxstream(@Param("entity")QuestionPaperEntity questionPaperEntity);
    /**
     * 删除超两年货款转收入的信息，订单号为700888、756888以及538520
     */
    Integer delPo();
    Integer updateDkAmount(@Param("amount")BigDecimal amount,@Param("uuid")String uuid);
}
