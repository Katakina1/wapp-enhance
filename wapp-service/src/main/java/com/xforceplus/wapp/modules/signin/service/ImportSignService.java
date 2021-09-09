package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
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
     * excel文件导入签收
     *
     * @param exportEntity 分库标识 帐号信息
     * @param excelFile   excel文件
     * @return 签收结果集
     */
    List<RecordInvoiceEntity> importSignExcel(ExportEntity exportEntity, MultipartFile excelFile, Integer count) throws ExcelException, RRException;

    /**
     * 图片导入签收
     * @param exportEntity 分库标识 帐号信息
     * @param imgFile 图片文件
     * @return 签收结果集
     */
    List<RecordInvoiceEntity> importSignImg(ExportEntity exportEntity, MultipartFile imgFile, Integer count);

    /**
     * 从sftp上下载获取base64图片
     *
     * @param params 请求参数（发票代码， 发票号码）
     * @return base64字符串
     */
    String getInvoiceImage(Map<String, String> params);

    /**
     * 修改发票 重新做一次签收流程
     *
     * @param map 发票map
     * @param exportEntity 分库标识 帐号信息
     * @return 结果
     */
    List<RecordInvoiceEntity> modifyInvoice(Map<String, String> map, ExportEntity exportEntity);
}
