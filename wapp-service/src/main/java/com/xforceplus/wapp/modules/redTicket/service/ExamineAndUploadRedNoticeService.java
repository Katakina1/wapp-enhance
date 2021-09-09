package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ExamineExcelEntity;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/22 13:56
 */

public interface ExamineAndUploadRedNoticeService {

    /**
     * 获取查询开红票分页数据对象
     * @param map 参数
     * @return 分页对象
     */
    List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map);
    /**
     * 获取查询开红票分页数据记录数
     * @param params 参数
     * @return
     */
    Integer getRedTicketMatchListCount(@Param("map") Map<String, Object> params);

    /**
     * 保存不同意理由
     * @param para
     * @return
     */
    String saveExamineRemarks(Map<String,Object> para);

    /**
     * 上传红字通知单
     * @param file
     * @param user
     * @param fileNumber
     * @return
     */
    Object uploadRedTicketRed(MultipartFile file, UserEntity user, String fileNumber, Integer id,String businessType);

    List<RedTicketMatchDetail> getRedTicketDetailsById(Map<String,Object> para);

    List<InvoiceEntity> getRedTicketInvoice(Map<String,Object> para);

    void updateTotalAmount(BigDecimal totalAmount, String invoiceCode, String invoiceNo);

    void updateRuturnNumber(Map<String,Object> para);

    void updateAgreementNumber(Map<String,Object> para);

    Object uploadRedTicketRedBatch(MultipartFile file);

    void updateMatchStatus(long id);

    Map<String, Object> sendMessageToTax(String ids);
	int revoke( Long id);
	 InvoiceEntity getRedInfo(String uuid);

    String seletcTaxCode(String taxname);

    List<ExamineExcelEntity> toExamineExcelEntity(List<Object> mergeInvoiceDetailList,Map<String, Object> map);
    ExamineExcelEntity toEntity(RedTicketMatchDetail rd,InvoiceEntity in,String tax_sortcode,Integer index);
}
