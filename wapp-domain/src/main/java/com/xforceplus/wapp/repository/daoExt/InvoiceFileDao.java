package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceFileDao extends BaseDao<TXfInvoiceFileEntity> {

    List<TXfInvoiceFileEntity> getByInvoice(@Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode);

    /**
     * 获取一张发票的多个类型的附件文件
     *
     * @param invoiceNo
     * @param invoiceCode
     * @param types
     * @return
     */
    List<TXfInvoiceFileEntity> getByInvoiceAndTypes(@Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode, @Param("types") List<Integer> types);

    /**
     * 批量获取多张发票 多个类型的文件
     * @param map  key: invoiceNo,invoiceCode
     * @param types 多个文件类型
     * @return
     */
    List<TXfInvoiceFileEntity> selectByInvoicesAndTypes(@Param("maps") List<Map<String, String>> map, @Param("types") List<Integer> types);

}
