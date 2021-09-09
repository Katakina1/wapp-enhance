package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/19.
 **/
@Mapper
public interface SignatureProcessingDao {

    List<RecordInvoiceEntity> getRecordIncoiceList(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    int queryTotal(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    List<OptionEntity> searchGf(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    List<RecordInvoiceEntity> queryAllList(@Param("schemaLabel") String schemaLabel,@Param("params") Map<String, Object> params);

    Boolean updateByuuid(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);

    /**
     * 根据uuid删除扫描表信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean deleteInvice(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);

    /**
     * 根据uuid删除明细表信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean deleteAllByuuid(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);

    /**
     * 根据uuid查询扫描表信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    RecordInvoiceEntity selectInvoice(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 删除前保存备份数据
     *
     * @param schemaLabel
     * @param r
     * @return
     */
    Boolean saveCopyData(@Param("schemaLabel") String schemaLabel,@Param("r") RecordInvoiceEntity r,@Param("user") UserEntity user);

    /**
     * 扫描表的失败数据再次签收--更新抵账表签收信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean updateInvoice(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);



    /**
     *扫描表的失败数据再次签收--更新扫描表签收数据
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean updateDxInvoiceData(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid,@Param("r") RecordInvoiceEntity r);


    /**
     * 根据uuid获取抵账表发票详细数据
     *
     * @param schemaLabel
     * @param params
     * @return
     */
    RecordInvoiceEntity getDataByuuid(@Param("schemaLabel") String schemaLabel,@Param("params") Map<String, Object> params);

    /**
     * 根据uuid查询是否一存在该uuid的数据
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Long getCopyId(@Param("schemaLabel")String schemaLabel, @Param("uuid") String uuid);

    /**
     * 删除时保存备份--uuid已存在 更新备份数据
     * @param schemaLabel
     * @param r
     * @param uuid
     * @return
     */
    Boolean updateCopyData(@Param("schemaLabel")String schemaLabel,@Param("r") RecordInvoiceEntity r,@Param("uuid")String uuid,@Param("user")UserEntity user);

    /**
     *根据uuid删除img表的数据
     * @param schemaLabel
     * @param uuid
     */
    void deleteImage(@Param("schemaLabel")String schemaLabel,@Param("uuid") String uuid);
}
