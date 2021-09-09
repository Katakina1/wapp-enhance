package com.xforceplus.wapp.modules.cost.service;

import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CostApplicationService {
    /**
     * 获取供应商的信息
     * @param id
     * @return
     */
    SettlementEntity getUserInfo(Long id);

    /**
     * 获取供应商的信息
     * @param venderid
     * @return
     */
    SettlementEntity getUserInfo(String venderid);

    /**
     * 获取费用类型
     * @param venderId
     * @return
     */
    List<SelectionOptionEntity> getCostType(String venderId, String businessType);

    /**
     * 根据发票代码号码查询抵账表发票
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    RecordInvoiceEntity searchInvoice(String invoiceCode, String invoiceNo, String orgcode);

    /**
     * 获取购方信息
     * @return
     */
    List<SelectionOptionEntity> getGfInfo();

    /**
     * 获取购方信息
     * @return
     */
    List<SelectionOptionEntity> getGfInfoByStaff(String staff);

    /**
     * 获取税率信息
     * @return
     */
    List<SelectionOptionEntity> getRateOptions();

    /**
     * 获取成本中心信息
     * @return
     */
    List<SelectionOptionEntity> getDeptInfo(String staffNo);

    /**
     * 获取邮箱与工号对应关系
     * @param vendorNo
     * @return
     */
    List<SelectionOptionEntity> getEmail(String vendorNo);

    /**
     * 保持上传文件的信息
     * @param fileEntity
     * @return
     */
    Integer saveFile(SettlementFileEntity fileEntity);

    /**上传文件到sftp
     *
     * @param file
     * @return 文件路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    SettlementFileEntity getFileInfo(Long id);

    /**
     * 查看图片
     * @param filePath
     * @param response
     */
    void viewImg(String filePath, String fileName, HttpServletResponse response);

    /**
     * 下载文件
     * @param filePath
     * @param response
     */
    void downloadFile(String filePath, String fileName, HttpServletResponse response);

    /**
     * 保存费用申请数据
     * @param settlement
     */
    void submitAll(SettlementEntity settlement) throws Exception;

    /**
     * 解析导入的excel
     * @param multipartFile
     * @return
     */
    Map<String,Object> parseExcel(MultipartFile multipartFile);

    /**
     * 根据jvcode获取购方税号
     * @param jvcode
     * @return
     */
    String getGfTaxNo(String jvcode);

    /**
     * 根据成本中心获取购方税号
     * @param dept
     * @return
     */
    String getGfTaxNoByDept(String dept);

    /**
     * 根据员工工号获取购方税号
     * @param staffNo
     * @return
     */
    String getGfTaxNoByStaffNo(String staffNo);
}
