package com.xforceplus.wapp.modules.fixed.service;


import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface MatchQueryService {
    /**
     * 查询匹配信息
     * */
    List<MatchQueryEntity> querylist(Map<String,Object> map);
    /**
     * 查询总条数
     * */
    int queryTotal(Map<String,Object> map);

    /**
     * 查询详细图片
     * */
    List<String> queryDetail(String uuid);
    /**
     * 取消匹配
     * */
    void cancelMatch(MatchQueryEntity entity);

    /**
     * 获取明细中抵账表销方购方明细信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoicesEntity getDetailInfo(String schemaLabel, Long id);

    /**
     * 获取转出信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid);

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    DetailVehicleEntity getVehicleDetail(String schemaLabel, Long id) throws Exception;

    /**
     * 获取明细表信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    List<DetailEntity> getInvoiceDetail(String schemaLabel, Long id);

    List<RecordInvoiceEntity> getDetailInvoice(Long matchId);
    List<OrderEntity> getDetailOrder(Long matchId);

    //导出匹配成功的发票明细
    List<RecordInvoiceDetail> exportDetailInvoice(Map<String, Object> params);

    List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
    void getInvoiceImageForAll(Long id, UserEntity user, HttpServletResponse response) ;
    FileEntity getFileInfo(@Param("id") Long id);

    void downloadFile(String filePath, String fileName, HttpServletResponse response) ;
}
