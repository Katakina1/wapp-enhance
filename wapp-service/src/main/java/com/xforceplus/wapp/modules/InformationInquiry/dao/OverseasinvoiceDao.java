package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OverseasinvoiceDao {
    /**
     * 查询海外发票信息
     * @param map
     * @return
     */
    List<OverseasInvoiceEntity> list(@Param("map")Map<String, Object> map);
    /**
     * 查询海外发票信息条数
     * @param map
     * @return
     */
    Integer listCount(@Param("map")Map<String, Object> map);
    /**
     * 导入海外发票
     * @return
     */
    Integer saveInvoice(@Param("list")List<OverseasInvoiceEntity> entity);
    /**
     * 查询海外发票问题信息
     * @return
     */
    List<OverseasInvoiceEntity> failedlist(@Param("createBy")String createBy);
    /**
     * 导入海外问题发票
     * @param entity
     * @return
     */
    Integer saveErrorInvoice(@Param("map")OverseasInvoiceEntity entity,@Param("createBy")String createBy,@Param("createTime")Date createTime,@Param("errorDescription")String errorDescription);
    /**
     * 删除当前登录人以前导错的数据
     * @param loginname
     * @return
     */
    Integer delete(@Param("loginname")String loginname);
    /**
     * 查询购方信息
     */
    Integer getGfCount(@Param("map")OverseasInvoiceEntity entity);
    /**
     * 修改海外发票信息
     */
    Integer updateInvoice(@Param("map")OverseasInvoiceEntity map);
    /**
     * 查询供应商信息
     */
    Integer getXfCount(@Param("map")OverseasInvoiceEntity entity);
}
