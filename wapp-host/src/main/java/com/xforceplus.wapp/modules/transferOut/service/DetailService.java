package com.xforceplus.wapp.modules.transferOut.service;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:05
*/

import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.entity.OrgEntity;

import java.util.List;

public interface DetailService {

    /**
     * 获取转出信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    List<InvoiceEntity> getOutInfo(String schemaLabel, String uuid);

    /**
     *获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    List<DetailEntity> getInvoiceDetail(String schemaLabel,Long  id);

    /**
     * 获取购方名称和税号
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OrgEntity> getGfNameAndTaxNo(String schemaLabel,Long  userId);

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    DetailVehicleEntity getVehicleDetail (String schemaLabel,Long id)throws  Exception;

    /**
     * 获取明细表信息数量
     * @param schemaLabel
     * @param id
     * @return
     */
    int getInvoiceDetailTotal(String schemaLabel,Long id);

}
