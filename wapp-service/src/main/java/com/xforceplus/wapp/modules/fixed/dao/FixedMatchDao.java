package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.fixed.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Mapper
public interface FixedMatchDao {
    /**
     * 获取订单信息列表
     * @param params
     * @return
     */
    List<OrderEntity> queryOrderList(Map<String, Object> params);

    /**
     * 获取税率信息
     * @return
     */
    List<SelectionOptionEntity> getRate();

    /**
     * 获取发票信息
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    InvoiceEntity searchInvoice(@Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);
    List<InvoiceEntity> searchInvoiceQuery(@Param("invoiceQueryDate1")String invoiceQueryDate1, @Param("invoiceQueryDate2")String invoiceQueryDate2, @Param("invoiceNo")String invoiceNo,@Param("gfTaxNo")String gfTaxNo,@Param("orgid")String orgid);
    /**
     * 获取JV信息
     * @param jvcode
     * @return
     */
    MatchQueryEntity getJVInfo(String jvcode);

    /**
     * 保存匹配表
     * @param match
     * @return
     */
    Integer saveMatch(MatchQueryEntity match);

    /**
     * 更新发票
     * @param invoice
     * @return
     */
    Integer updateInvoice(InvoiceEntity invoice);
    Integer updateInvoiceByEntering(InvoiceEntity invoice);

    /**
     * 添加发票
     * @param invoice
     * @return
     */
    Integer saveInvoice(InvoiceEntity invoice);

    /**
     * 更新订单状态
     * @param id
     * @return
     */
    Integer updateOrder(Long id);

    /**
     * 保存关联表
     * @return
     */
    Integer saveLink(List<LinkEntity> list);

    /**
     * 保存问题单信息
     * @param entity
     * @return
     */
    Integer saveFile(FileEntity entity);

    /**
     * 更新文件路径,匹配号
     * @return
     */
    Integer updateFile(@Param("path")String path, @Param("matchId")Long matchId, @Param("id")Long id);

    Integer matchInsertOrUpdate(@Param("item")Map<String, Object> invoiceEntity);


    OrganizationEntity getXf(Long userId);
}
