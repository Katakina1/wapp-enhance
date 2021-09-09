package com.xforceplus.wapp.modules.redInvoiceManager.dao;

import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface UploadScarletLetterDao extends BaseDao<UploadScarletLetterEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryListAll(@Param("map") Map<String, Object> map);

    List<UploadScarletLetterEntity> queryListAllExport(@Param("map") Map<String, Object> map);
    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryListByStore(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListByStoreAll( @Param("map") Map<String, Object> map);

    /**
     * 查询类型
     * @param map
     * @return
     */
    UploadScarletLetterEntity getTypeById(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResultByStore(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

//    void updateStatus(@Param("id")Integer id,@Param("redNoticeNumber") String redNoticeNumber, @Param("redNoticeAssociation") Integer  redNoticeAssociation);

    //保存文件路径、名称
//    int saveFilePathRed(@Param("filePath")String path, @Param("fileType")String fileType, @Param("fileName")String fileName);
//    int saveFilePathRed(@Param("fileEntity")FileEntity fileEntity);

    /**
     *
     *
     * @param s 文件目录
     * @param fileType 文件类型
     */
    void saveFilePath(@Param("filePath")String s, @Param("fileType")String fileType, @Param("fileNumber")String fileNumber, @Param("localFileName")String localFileName,@Param("fileName")String fileName);


    //保存红字通知单号
    int saveRedDetail(@Param("redLetterNotice")String redLetterNotice, @Param("serialNumber")String serialNumber);


    /**
     * 查询所有文件数据(分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> getfileName( @Param("map") Map<String, Object> map);

    /**
     * 查询所有文件数据(分页)
     * @param id
     * @return
     */
    UploadScarletLetterEntity getfileName1( @Param("id") Long id);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity getfileNameCount( @Param("map") Map<String, Object> map);

    /**
     * 查询文件数量
     * @param localFileName
     * @return
     */
    Integer getfileCount( @Param("localFileName") String localFileName);


    /**
     * 根据文件名删除文件
     * @param localFileName
     * @return
     */
    int delete( @Param("localFileName") String localFileName);

    int delete1( @Param("redLetterNotice") String redLetterNotice);

    /**
     * 查询文件数量
     * @param serialNumber
     * @return
     */
    Integer getfileCount1( @Param("serialNumber") String serialNumber);
    int updateStatus2(@Param("serialNumber") String serialNumber);

    int updateStatus(@Param("serialNumber") String serialNumber);
    int updateStatus1(@Param("redLetterNotice") String redLetterNotice,@Param("redNoticeAssociation") Integer  redNoticeAssociation);
    String getRedNoticeNumber(@Param("serialNumber") String serialNumber);

}
