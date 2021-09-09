package com.xforceplus.wapp.modules.redTicket.dao;


import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface QueryOpenRedTicketDataDao {

    /**
     *
     * 获取红票匹配表信息
     * @param params
     * @return
     */
    List<RedTicketMatch> getRedTicketMatchList(@Param("map") Map<String, Object> params);
    /**
     *
     * 获取红票匹配表信息条数
     * @param params
     * @return
     */
    Integer getRedTicketMatchListCount(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过序列号查询退货表的信息
     * @param params
     * @return
     */
    List<ReturnGoodsEntity> getReturnGoodsList(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过序列号查询退货表的条数
     * @param params
     * @return
     */
    Integer getReturnGoodsListCount(@Param("map") Map<String, Object> params);

    /**
     *
     * 通过代号查询蓝票的信息
     * @param params
     * @return
     */
    List<InvoiceEntity> getRecordInvoiceList(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过代号查蓝票的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过序列号查询抵账表明细的信息
     * @param params
     * @return
     */
    List<InvoiceDetail> getRecordInvoiceDetailList(@Param("map") Map<String, Object> params);

    /**
     *
     * 通过代号查明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceDetailListCount(@Param("map") Map<String, Object> params);
    /**
     *
     * 查询红票明细的信息
     * @param params
     * @return
     */
    List<RedTicketMatchDetail> getMergeInvoiceDetailList(@Param("map") Map<String, Object> params);
    /**
     *
     * 通过序列号查询合并条数
     * @param params
     * @return
     */
    Integer getMergeInvoiceDetailListCount(@Param("map") Map<String, Object> params);

    List<AgreementEntity> getAgreementList(@Param("map") Map<String, Object> params);

    Integer getAgreementListCount(@Param("map") Map<String, Object> params);

    /**
     * 查询上传资料文件
     * @param params
     * @return
     */
    List<FileEntity> getQueryImg( @Param("map")Map<String,Object> params);


    /**
     * 通过id  找文件实体
     * @param id
     * @return
     */
    FileEntity getFielImage(@Param("id")Long id);

    /**
     *
     *
     * @param s 文件目录
     * @param fileType 文件类型
     */
    void saveFilePath(@Param("filePath")String s, @Param("fileType")String fileType, @Param("fileNumber")String fileNumber, @Param("fileName")String fileName);


    /**
     * 修改上传资料状态
     * @param id id
     */
    void updateStatus(@Param("id")Integer id);
    /**
     * 修改审核状态
     * @param id id
     */
    void updateExamineStatus(@Param("id")Integer id);
    /**
     * 查询税率
     */
    List<OptionEntity> queryXL();
    /**
     * 查询开红票资料类型
     */
    List<OptionEntity> queryRedTicketType();

    String selectBusinessType(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);
}
