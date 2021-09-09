package com.xforceplus.wapp.modules.posuopei.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;

import com.xforceplus.wapp.interfaceBPMS.Table;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import net.sf.json.JSONArray;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface MatchService {

    PagedQueryResult<PoEntity> poQueryList(Map<String, Object> map);

    PagedQueryResult<ClaimEntity> claimQueryList(Map<String, Object> map);

    PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String, Object> map);

    PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String, Object> map);

    Integer saveInvoice(Map<String, Object> map);

    List<InvoiceEntity> ifExist(Map<String, Object> map) ;

    Boolean match(MatchEntity matchEntity);

    /**
     * 获取购方名称和税号
     * @param userId
     * @return
     */
    List<OrgEntity> getGfNameAndTaxNo(Long userId);

    /**
     * 获取分区
     * @param
     * @return
     */
    List<OrgEntity> getPartion(String theKey);

    /**
     * 获取城市
     * @param
     * @return
     */
    List<Table> getCity();


    /**
     * 带出业务字典信息
     * @param
     * @return
     */
    List<OrgEntity> getDicdeta(String theKey);


    /**
     * 获取默认信息
     * @param userId
     * @return
     */
    OrgEntity getDefaultMessage(Long userId);


    /**
     * 校验发票信息
     * @param invoiceCode
     * @param invoiceNo
     * @param invoiceDate
     * @param invoiceAmount
     * @param totalAmount
     * @param taxRate
     * @param taxAmount
     * @return
     */
    Boolean checkInvoiceMessage(String invoiceCode,String invoiceNo,String invoiceDate,String invoiceAmount,String totalAmount,String taxRate,String taxAmount);
    /**
     * 导入发票excel
     * @param file 需要导入的文件
     * @return 结果 成功则包含发票信息集
     */
    Map<String, Object> importInvoice(Map<String, Object> map , MultipartFile file) ;

    /**
     * 导入匹配关系
     * @param maps
     * @param file
     * @return
     */
     Map<String, Object> importMatch(Map<String, Object> maps, MultipartFile file,String f);
    /**
     * 导入索赔差异明细
     * @param file
     * @return
     */
     Map<String, Object> importClaim(MultipartFile file);
    /**
     * 导入其他明细
     * @param file
     * @return
     */
    public Map<String, Object> importOther(MultipartFile file);
    /**
     * 导入订单单价差异明细
     * @param file
     * @return
     */
     Map<String, Object> importPo(MultipartFile file);
    /**
     * 导入订单折扣明细
     * @param file
     * @return
     */
     Map<String, Object> importPoDiscount(MultipartFile file);
    /**
     * 导入订单折扣明细
     * @param file
     * @return
     */
     Map<String, Object> importCount(MultipartFile file,String type);
    /**
     * 保存匹配
     */
    String saveMatch(MatchEntity matchEntity,String venId);

    /**
     * 判断发票类型是否为普票
     * @param invoiceCode
     * @return
     */
    String getFplx(String invoiceCode);

    /**
     * 保存采购问题单
     * @param questionPaperEntity
     * @return true/false
     */
    Boolean saveQuestionPaper(QuestionPaperEntity questionPaperEntity);
    /**
     * 保存采购问题单
     * @param questionPaperEntity
     * @return true/false
     */
    Boolean updateQuestionPaper(QuestionPaperEntity questionPaperEntity);
    Integer deleteQuestion(Integer id);
    PagedQueryResult<QuestionPaperEntity> questionPaperQuery(Map<String,Object> map);
    PagedQueryResult<Object> questionPaperDetailQuery(Map<String,Object> map);

    /**上传文件到sftp
     *
     * @param file
     * @return 文件路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 保持上传文件的信息
     * @param fileEntity
     * @return
     */
    Integer saveFile(SettlementFileEntity fileEntity);

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    SettlementFileEntity getFileInfo(Long id);

    /**
     * 下载文件
     * @param filePath
     * @param response
     */
    void downloadFile(String filePath,String fileName, HttpServletResponse response);

    /**
     * 查詢附件列表
     * @param id
     * @return
     */
    List<SettlementFileEntity> viewFile(String id);

    /**
     * 保存审核结果
     * @param param
     * @return
     */
    Boolean check(Map<String,Object> param);

    Boolean cheXiao(Map<String,Object> param);

    public  void ReGetconnHostPo1(String date,String dateEnd,String vender);
    public  void ReGetconnHostPo2(String date,String dateEnd,String vender);
    public  void ReGetconnHostPo4(String date,String dateEnd,String vender);



    public  void ReconnHostClaimType2(String date,String dateEnd,String vender);
    public  void ReconnHostClaimType3(String date,String dateEnd,String vender);
    public  void ReconnHostAgain(String date,String dateEnd,String vender);


    public JSONArray writeScreen(MatchEntity matchEntity);

    public void runWritrScreen();
    public List<SubmitOutstandingReportEntity> checkWriteScreen(MatchEntity matchEntity,Connection conn);
   public List<InvoicesEntity> getInvoice(String venderid,String invoice_no,String invoice_amount);
   public List<MatchEntity>getMatch(String matchno);
   public Integer updateMatchHostStatus(String host_status,String id);

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
     * 采购同意
     */
    Boolean updataY(String id);

    /**
     * 采购不同意
     */
    Boolean updataN(String id);
    /**
     * 删除超两年货款转收入的信息，订单号为700888、756888以及538520
     */
    Integer delPo();

    List<QuestionPaperExcelEntity> toExcel(List<QuestionPaperEntity> list);

}
