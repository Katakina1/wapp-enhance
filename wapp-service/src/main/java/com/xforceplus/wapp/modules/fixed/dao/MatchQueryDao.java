package com.xforceplus.wapp.modules.fixed.dao;


import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MatchQueryDao {
    /**
     * 查询总条数
     * */
    int queryTotal(@Param("map")Map<String,Object> map);
    /**
     * 查询
     * */
    List<MatchQueryEntity> querylist(@Param("map")Map<String,Object> map);
    /**
     * 查询详细图片
     * */
    List<String> queryDetail(@Param("uuid") String uuid);
    /**
     * 取消匹配
     * */
    List<Long> queryOrderid(@Param("entity") MatchQueryEntity entity);
    List<String> queryInvoiceNo(@Param("entity") MatchQueryEntity entity);
    void cancelMatchStatus(@Param("entity") MatchQueryEntity entity);
    void delMatch(@Param("entity") MatchQueryEntity entity);
    void cancelOrderStatus(@Param("orderidList") List<Long> orderid);
    void cancelInvoiceStatus(@Param("uuidList") List<String> uuid);

    List<RecordInvoiceEntity> getDetailInvoice(Long matchId);
    List<OrderEntity> getDetailOrder(Long matchId);

    //导出匹配成功的发票明细
    List<RecordInvoiceDetail> exportDetailInvoice(@Param("map")Map<String, Object> map);
    /**
     * 获取明细中抵账表销方购方明细信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoicesEntity getDetailInfo(@Param("schemaLabel")String schemaLabel, @Param("id") Long id);

    /**
     * 获取转出信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoicesEntity> getOutInfo(@Param("schemaLabel")String schemaLabel, @Param("uuid") String uuid);

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    FileEntity getFileInfo(@Param("id") Long id);

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    DetailVehicleEntity getVehicleDetail(@Param("schemaLabel")String schemaLabel, @Param("id") Long id) throws Exception;

    /**
     * 获取明细表信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    List<DetailEntity> getInvoiceDetail(@Param("schemaLabel")String schemaLabel, @Param("id")Long id);
    /*
     *
     * 查询文件
     */
    List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
    //查询图片
    FileEntity getFileImage(@Param("id")Long id);

}
