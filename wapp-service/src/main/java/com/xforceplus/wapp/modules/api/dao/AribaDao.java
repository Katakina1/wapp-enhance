package com.xforceplus.wapp.modules.api.dao;

import com.xforceplus.wapp.modules.api.entity.AribaCheckEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @atuthor wyman
 * @date 2020-05-06 13:53
 **/
@Mapper
public interface AribaDao {

    int saveRequest(@Param("request") String request, @Param("basic") String basic, @Param("sessionAuth") String sessionAuth, @Param("response") String response);
    int checkSave(@Param("jvcode") String jvcode, @Param("venderid") String venderid, @Param("supplierNumber") String supplierNumber, @Param("id") Long id,@Param("companyCode") String companyCode);




    /**
     * 插入前删除明细数据
     * @param
     */
    void deleteDetail( @Param("invoiceDetailInfo") InvoiceDetailInfo invoiceDetailInfo);

    Integer insertNoDetailedInvoice( @Param("list")List<InvoiceDetailInfo> invoiceDetailInfoList);

    Integer selectRecordInvoiceCount( @Param("uuid") String uuid);

    Integer insertRecordInvoiceScan( @Param("item") InvoiceCollectionInfo infoList, @Param("cyYoN") int cyYoN);

    Integer updateRecordInvoiceScan( @Param("item") InvoiceCollectionInfo infoList, @Param("cyYoN") int cyYoN);

    Integer signInMarkInsert( @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    Integer signInMarkQuery( @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    int authQuery(@Param("fapiaosBean")AribaCheckEntity.FapiaosBean fapiaosBean, @Param("itemsBean")AribaCheckEntity.FapiaosBean.ItemsBean itemsBean);

    void authInsert(@Param("fapiaosBean")AribaCheckEntity.FapiaosBean fapiaosBean, @Param("itemsBean")AribaCheckEntity.FapiaosBean.ItemsBean itemsBean);

    void updateRecordInvoiceConfirmStatus(@Param("fapiaosBean")AribaCheckEntity.FapiaosBean fapiaosBean);

    int deleteAuth(@Param("fapiaosBean")AribaCheckEntity.FapiaosBean fapiaosBean, @Param("itemsBean")AribaCheckEntity.FapiaosBean.ItemsBean itemsBean);


    List<RecordInvoiceEntity> getTaxInformation();

    int insertInvoiceImgforCustomerOne( @Param("item")Map<String, String> imgMap);

    void checkUpdate(@Param("fapiaosBean")AribaCheckEntity.FapiaosBean fapiaosBean, @Param("item")ComprehensiveInvoiceQueryEntity item);
    Integer insertScanInvoice( @Param("item")InvoiceScan invoiceScanList);

    void updateTaxInformation(@Param("item")JSONObject result);

    List<ComprehensiveInvoiceQueryEntity> queryList( @Param("map") Map<String, Object> map);


    int signInMarkSave(@Param("venderid") String venderid, @Param("supplierNumber") String supplierNumber, @Param("invoiceCode") String invoiceCode,@Param("invoiceNo") String invoiceNo);
}
