package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Mapper
public interface PaymentInvoiceUploadDao extends BaseDao<PaymentInvoiceUploadEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryList(@Param("map") Map<String, Object> map);
    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult( @Param("map") Map<String, Object> map);

    /**
     * 保存扣款发票信息
     */
//    int saveInvoice(@Param("entity") PaymentInvoiceUploadEntity entity);
    int saveInvoice(@Param("list") List<PaymentInvoiceUploadEntity> entity);
    /**
     * 保存扣款问题发票信息
     */
    int saveInvoiceFail(@Param("entity") PaymentInvoiceUploadEntity entity);
    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryListAll(@Param("map") Map<String, Object> map);

    /**
     * 查询所有问题发票数据(不分页)
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryListAllFail(@Param("map") Map<String, Object> map);

    /**
     * 查询表内索赔号数量
     * @param supplierAssociation
     * @return
     */
    int queryreturnGoodsCode(@Param("supplierAssociation") String supplierAssociation,@Param("returnGoodsCode") String returnGoodsCode,@Param("paymentInvoiceNo") String paymentInvoiceNo,@Param("purchaseInvoiceNo") String purchaseInvoiceNo);

    /**
     * 根据索赔号修改日期
     * @param entity
     * @return
     */
    int inputreturnGoodsCode(@Param("entity") PaymentInvoiceUploadEntity entity);


    int selectIsExists(@Param("supplierAssociation") String supplierAssociation, @Param("returnGoodsCode") String returnGoodsCode, @Param("returnGoodsDate") String returnGoodsDate, @Param("returnCostAmount") String returnCostAmount,@Param("paymentInvoiceNo") String paymentInvoiceNo,@Param("purchaseInvoiceNo") String purchaseInvoiceNo);

    int delete(@Param("loginName") String loginName);

    int deletefail(@Param("id") Long id);


}
