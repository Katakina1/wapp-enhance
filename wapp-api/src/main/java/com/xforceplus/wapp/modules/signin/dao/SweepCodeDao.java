package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * CreateBy leal.liang on 2018/4/17.
 **/
@Mapper
public interface SweepCodeDao {



    /**
     * 根据uuid查验抵账表是否存在该数据
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    RecordInvoiceEntity getIncoiceData(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 保存信息到抵账表
     * @param schemaLabel
     * @param r
     */
    Boolean saveIncoiceData(@Param("schemaLabel") String schemaLabel,@Param("r") InvoiceCollectionInfo r);

    /**
     * 保存数据到扫描表
     *
     * @param schemaLabel
     * @param r
     * @return
     */
    Boolean saveIncoiceData2(@Param("schemaLabel") String schemaLabel,@Param("r") RecordInvoiceEntity r);

    /**
     *
     *
     * @param schemaLabel
     * @param userId
     * @return
     */
    RecordInvoiceEntity getUserData(@Param("schemaLabel") String schemaLabel,@Param("userId") Long userId);

    /**
     * 更新抵账表的状态
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean updateDataByUuid(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);

    /**
     * 保存发票明细
     *
     * @param schemaLabel
     * @param r
     * @return
     */
    Boolean saveIncoiceData3(@Param("schemaLabel") String schemaLabel,@Param("r") InvoiceDetailInfo r);

    /**
     * 保存数据到扫描表
     *
     * @param schemaLabel
     * @param rq
     * @return
     */
    Boolean saveData(@Param("schemaLabel") String schemaLabel,@Param("rq") RecordInvoiceEntity rq);

    /**
     * 根据id删除扫描表数据
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    Boolean deleteById(@Param("schemaLabel") String schemaLabel,@Param("uuid") String id);


    /**
     *根据传过来的扫描表uuid查询抵账表数据id
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Long getInvoiceId(@Param("schemaLabel")String schemaLabel,@Param("uuid")  String uuid);

    /**
     * 删除功能--抵账表签收状态改为未签收（0）
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean deleteDataByUuid(@Param("schemaLabel")String schemaLabel,@Param("uuid")String uuid);


    /**
     * 根据uuid查询扫描表的id(查验是否已进行了签收操作)
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Long getInvoiceData(@Param("schemaLabel")String schemaLabel,@Param("uuid") String uuid);
}
