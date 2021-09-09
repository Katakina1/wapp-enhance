package com.xforceplus.wapp.modules.fixed.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xforceplus.wapp.modules.base.dao.BaseDao;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.entity.QuestionOrderEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;

@Mapper
public interface QuestionOrderCheckDao extends BaseDao<QuestionOrderEntity>{
	//查询问题单
	List<QuestionOrderEntity> queryCheckOrderList( @Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
    //查询总计
	Integer countOrders(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
	   
    /**
     * 查询发票信息
     */
    List<InvoiceEntity> queryInvoice(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
    /**
     * 查询订单信息
     */
    List<OrderEntity> queryOrder(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);

    /*
     * 
     * 查询文件
     */
    List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
   //查询图片
    FileEntity getFileImage(@Param("id")Long id);
    
    /**
     * 保存审核结果
     * @param param
     * @return
     */
    Integer check(Map<String,Object> param);
    
    
    
    QuestionOrderEntity getPaperById(Map<String,Object> param);

    
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
    DetailVehicleEntity getVehicleDetail(@Param("schemaLabel")String schemaLabel,@Param("id") Long id) throws Exception;
       

    /**
     * 获取明细表信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    List<DetailEntity> getInvoiceDetail(@Param("schemaLabel")String schemaLabel, @Param("id")Long id);

    /**
     * 获取转出信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoicesEntity> getOutInfo(@Param("schemaLabel")String schemaLabel, @Param("uuid") String uuid);

    /**
     * 获取明细中抵账表销方购方明细信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoicesEntity getDetailInfo(@Param("schemaLabel")String schemaLabel,@Param("id") Long id);
}
