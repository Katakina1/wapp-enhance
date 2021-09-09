package com.xforceplus.wapp.modules.fixed.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.entity.QuestionOrderEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import com.xforceplus.wapp.modules.posuopei.service.impl.MatchServiceImpl;

public interface QuestionOrderService {
	 
	
	
	List<QuestionOrderEntity> queryCheckOrderList( String schemaLabel, @Param("map") Map<String, Object> map);

	 /**
     * 查询总数
     * @param map
     * @return
     */
    Integer countOrders(String schemaLabel,Map<String, Object> map);
    
    
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) ;
  //问题单审批
    Boolean check(Map<String,Object> param);

    public void getInvoiceImageForAll(Long id, UserEntity user, HttpServletResponse response) ;

    FileEntity getFileInfo(@Param("id") Long id);
    /**
     * 查询发票信息
     */
    List<InvoiceEntity> queryInvoice(String schemaLabel,Map<String, Object> map);
    //查询订单
    List<OrderEntity> queryOrder(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
   
    List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
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

    /**
     * 获取转出信息
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid);

    /**
     * 获取明细中抵账表销方购方明细信息
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoicesEntity getDetailInfo(String schemaLabel, Long id);
    
}
