package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 导入签收业务层接口
 *
 * @author Colin.hu
 * @date 4/23/2018
 */
public interface ImportSignService {

    

    
    /**
     * 客户端图片上传
     * @param imgFile
     * @param ocrMap
     * @return
     * @throws Exception 
     */
    String onlyUploadImg(ExportEntity exportEntity,MultipartFile imgFile, Map<String, String> ocrMap ) throws Exception;
    
    
    /**
     * 从sftp上下载获取base64图片
     *
     * @param params 请求参数（发票代码， 发票号码）
     * @return base64字符串
     */
    String getInvoiceImage(Map<String, String> params);
    
    
    /**
     * 从sftp上删除图片
     * @param params 请求参数（t_dx_invoice_img表的 uuid）
     * @return 字符串
     */
    String deleteInvoiceImage(Map<String, String> params);


    
    /**
     *  根据发票号码，发票代码查询发票税局信息
     * @param schemaLabel
     * @param invoiceNo
     * @param invoiceCode
     * @return
     */
     InvoiceCollectionInfo queryInvoiceInfo(@Param("schemaLabel")String schemaLabel, @Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode);
     
     
     Integer checkScanPoint(@Param("schemaLabel")String schemaLabel, @Param("userId") String userId, @Param("scanPoint") String scanPoint);
     
     Integer checkbilltypeCode(@Param("schemaLabel")String schemaLabel, @Param("userId") String userId, @Param("billtypeCode") String billtypeCode);

    List<RecordInvoiceEntity> toSignInvoiceVo(Map<String, String> ocrMap, String scanPathId);

    Map<String,Object> getUpdateRecordInvoiceEntity(ExportEntity exportEntity, Map<String, Object> invoicess);

    Map<String,Object> importSignExcel(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList2);
    
    String excuteUpload(ExportEntity entity, MultipartFile file, Map<String, String> ocrMapJson) throws Exception;
}
