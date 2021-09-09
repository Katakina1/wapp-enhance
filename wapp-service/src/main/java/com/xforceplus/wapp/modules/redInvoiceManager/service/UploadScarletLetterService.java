package com.xforceplus.wapp.modules.redInvoiceManager.service;



import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceListExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarleQueryExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface UploadScarletLetterService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryListAll(Map<String, Object> map);
    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryListAllExport(Map<String, Object> map);
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryListByStore(String schemaLabel, Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListByStoreAll(Map<String, Object> map);
    /**
     * 类型
     * @param map
     * @return
     */
    UploadScarletLetterEntity getTypeById(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResultByStore(String schemaLabel, Map<String, Object> map);

    Object uploadRedTicketRed(MultipartFile file, UserEntity user, String fileNumber, Integer id);

    /**
     * 文件分页列表
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> getfileName(Map<String, Object> map);

    /**
     * 文件列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity getfileNameCount(Map<String, Object> map);

    /**
     * 文件数量
     * @param fileName
     * @return
     */
    Integer getfileCount(String fileName);
    Integer getfileCount1(String serialNumber);

    /**
     * 根据文件名删除文件
     *
     * @param fileName
     */
//    void delete(String fileName);
    void delete1(String redLetterNotice);

    int updateStatus2(String serialNumber);

    int deleteRedData(Map<String,Object> para);

    String getRedNoticeNumber(String serialNumber);
    List<UploadScarleQueryExcelEntity> toExcel(List<UploadScarletLetterEntity> list);
    List<RedInvoiceListExcelEntity> toExcel2(List<UploadScarletLetterEntity> list);
}
