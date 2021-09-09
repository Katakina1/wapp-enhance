package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;

import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface SignForQueryDao {

    /**
     * 查询所有数据
     * @param map
     * @return
     */
    List<SignForQueryEntity> queryList(@Param("map") Map<String, Object> map);

    /**
     * 发票查询条数
     * @param map
     * @return
     */
    Integer invoiceMatchCount(@Param("map") Map<String, Object> map);
    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<SignForQueryEntity> queryListAll(@Param("map") Map<String, Object> map);



}
