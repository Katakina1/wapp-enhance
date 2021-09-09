package com.xforceplus.wapp.modules.redInvoiceManager.dao;

import com.xforceplus.wapp.modules.redInvoiceManager.entity.InputRedTicketInformationEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Mapper
public interface InputRedTicketInformationDao extends BaseDao<UploadScarletLetterEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<UploadScarletLetterEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListAll(@Param("map") Map<String, Object> map);
    List<UploadScarletLetterEntity> queryListAllExport(@Param("map") Map<String, Object> map);
    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);



    /**
     *
     * 查询红票明细信息
     * @param params
     * @return
     */
    List<InvoiceListEntity> getRedInvoiceList(@Param("map") Map<String, Object> params);
    List<InvoiceListEntity> getRedInvoiceList1(@Param("map") Map<String, Object> params);

    /**
     *
     * 查询红票明细条数
     * @param params
     * @return
     */
    Integer getRedInvoiceCount(@Param("map") Map<String, Object> params);

    List<OrgEntity> getGfNameAndTaxNo(@Param("userId")Long userId);

    RedTicketMatch selectNoticeById(@Param("map")Map<String,Object> params);

    List<InvoiceEntity> invoiceQueryList(@Param("map")Map<String,Object> map);
    InvoiceEntity invoiceQueryList1(@Param("uuid")String uuid);

    UploadScarletLetterEntity  queryJvCode(@Param("serialNumber") String serialNumber);

    UploadScarletLetterEntity  queryCompanyCode(@Param("jvCode") String jvCode);

    ImportEntity  querySerialNumber(@Param("redNoticeNumber") String redNoticeNumber);


    int updateRed(@Param("map")Map<String,Object> map);

    void saveInvoice(@Param("map")Map<String,Object> map);

    int saveInvoiceMatch(@Param("map")Map<String,Object> map);

    int updateRedNoticeNumber(@Param("map")Map<String,Object> map);

    int saveInvoiceMatchEntity(@Param("map")Map<String,Object> map);

    int allUpdate(@Param("map")Map<String,Object> map);

    int allUpdateMatch(@Param("map")Map<String,Object> map);


    int saveInvoiceMatchLR(@Param("map")Map<String,Object> map);

    int emptyRedInvoice(@Param("id")Long id);

    int emptyRecord(@Param("uuid")String uuid);

    InputRedTicketInformationEntity queryUuid(@Param("id")Long id);

    RedTicketMatch selectRedTicketById(@Param("map")Map<String,Object> map);

    Integer getRedInvoiceCount1(@Param("map") Map<String, Object> params);

    Integer getRedInvoiceCount2(@Param("redNoticeNumber")String redNoticeNumber);

    RedTicketMatch getRedNoticeMatch(@Param("redNoticeNumber")String redNoticeNumber);

    /**
     * 批量导入红票信息
     * @param redNoticeNumber
     * @return
     */
    int inputredExpressnoBatch(@Param("redNoticeNumber") String redNoticeNumber);


    //Excel导入
    void allUpdateBatchInvoice(ImportEntity invoiceEntity);

    void allUpdateMatchBatch(ImportEntity invoiceEntity);

    void saveInvoiceMatchBath(InvoiceEntity invoiceEntity);

    void insertRedTicketInvoice(ImportEntity importEntity);

    void updateRuteStatu(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);

    void updateProcloStatu(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);


    int saveRedNoticeNumber(@Param("redNoticeNumber")String redNoticeNumber,@Param("uuid") String uuid);

    String getCopyPerson(String dictcode);
}
