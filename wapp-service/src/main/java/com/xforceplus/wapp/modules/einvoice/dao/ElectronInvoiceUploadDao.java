package com.xforceplus.wapp.modules.einvoice.dao;

import com.xforceplus.wapp.modules.base.dao.BaseDao;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceImage;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceLog;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created on 2018/04/12.
 *
 * @author marvin
 * 电票上传持久层
 */
@Mapper
public interface ElectronInvoiceUploadDao extends BaseDao<ElectronInvoiceEntity> {

    /**
     * 保存上传的电票
     *
     * @param invoice     电票信息
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int saveElectronInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoice") ElectronInvoiceEntity invoice);

    /**
     * 保存电票上传记录
     *
     * @param invoiceLog  记录信息
     * @param schemaLabel 当前用户所在的分库名
     * @return 影响行数
     */
    int saveElectronLog(@Param("schemaLabel") String schemaLabel, @Param("invoiceLog") ElectronInvoiceLog invoiceLog);

    /**
     * 保存电票上传的图片路径
     *
     * @param invoiceImage 图片路径信息
     * @param schemaLabel  当前用户所在的分库名
     * @return 影响行数
     */
    int saveElectronImg(@Param("schemaLabel") String schemaLabel, @Param("invoiceImage") ElectronInvoiceImage invoiceImage);

    /**
     * 删除上传的电票数据
     *
     * @param id          id
     * @param schemaLabel 当前用户所在的分库名
     * @return 影响条数
     */
    int deleteElectronInvoice(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 保存底账表信息
     *
     * @param recordInvoice 要保存的底账信息
     * @param schemaLabel   当前用户所在的分库名
     * @return 影响条数
     */
    int saveRecordInvoice(@Param("schemaLabel") String schemaLabel, @Param("recordInvoice") RecordInvoice recordInvoice);

    /**
     * 保存查验回来的商品明细
     *
     * @param detailList  明细信息
     * @param schemaLabel 当前用户所在的分库名
     * @return 插入条数
     */
    int saveRecordInvoiceDetail(@Param("schemaLabel") String schemaLabel, @Param("detailList") List<RecordInvoiceDetail> detailList);

    /**
     * 根据id查询上传的电票信息
     *
     * @param id          电票的id
     * @param uuid        电票的uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    ElectronInvoiceEntity selectElectronInvoiceById(@Param("schemaLabel") String schemaLabel, @Param("id") Long id, @Param("uuid") String uuid);

    /**
     * 根据UUID删除底账表数据
     *
     * @param uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int deleteRecordInvoice(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 根据UUID删除明细表数据
     *
     * @param uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int deleteRecordInvoiceDetail(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 修改发票信息
     *
     * @param invoice
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int updateElectronInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoice") ElectronInvoiceEntity invoice);

    /**
     * 根据uuid或者scanId获取图片的image_path
     *
     * @param uuid
     * @param scanId
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    ElectronInvoiceImage getImg(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid, @Param("scanId") String scanId);

    /**
     * 删除上传的图片
     *
     * @param uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int deleteInvoiceImg(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 根据扫描版发票的id获取该发票的所有信息
     *
     * @param id          发票在扫描表的id
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询到的信息记录
     */
    ElectronInvoiceEntity selectElectronInvoiceAll(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 保存发票的删除记录
     *
     * @param invoice     要删除发票的信息
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int saveDelElectronInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoice") ElectronInvoiceEntity invoice);

    /**
     * 根据uuid查询底账表中的数据
     *
     * @param uuid        发票的uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    RecordInvoice selectRecordInvoiceAll(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 保存底账表的发票删除信息
     *
     * @param invoice     要删除的底账表的发票信息
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int saveDelRecordInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoice") RecordInvoice invoice);

    /**
     * 根据uuid查询此发票的删除次数
     *
     * @param uuid        发票的uuid
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    Integer selectDelInvoiceCount(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 更新删除的数据
     *
     * @param invoice     更新的信息
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int updateDelRecordInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoice") ElectronInvoiceEntity invoice);

    /**
     * 通行费签收成功后修改底账的签收方式、签收时间、备注
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param uuid        发票代码+发票号码    唯一索引
     * @return
     */
    int updateRecordInvoice(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 根据用户id查询用户关联的购方税号
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param userId      用户id
     * @return
     */
    List<String> selectGfTaxNo(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 更新图片表中保存的数据
     *
     * @param schemaLabel  当前用户所在的分库名
     * @param invoiceImage 要更新的图片信息：
     *                     imagePath：图片路径
     *                     scanID：图片唯一识别码
     *                     uuid：通过uuid寻找要更新的图片记录信息，不能为空
     * @return
     */
    int updateElectronInvoiceImg(@Param("schemaLabel") String schemaLabel, @Param("invoiceImage") ElectronInvoiceImage invoiceImage);
}
