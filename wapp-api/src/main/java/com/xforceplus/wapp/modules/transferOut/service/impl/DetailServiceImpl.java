package com.xforceplus.wapp.modules.transferOut.service.impl;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:06
*/

import com.xforceplus.wapp.modules.transferOut.dao.DetailDao;
import com.xforceplus.wapp.modules.transferOut.dao.OrgDao;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.entity.OrgEntity;
import com.xforceplus.wapp.modules.transferOut.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailServiceImpl implements DetailService {

    @Autowired
    private DetailDao detailDao;

    @Autowired
    private OrgDao orgDao;


    @Override
    public List<InvoiceEntity> getOutInfo(String schemaLabel, String uuid) {
        return detailDao.getOutInfo(schemaLabel, uuid);
    }

    /**
     * 获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetail(String schemaLabel,Long  id) {
        List<DetailEntity> result  = detailDao.getInvoiceDetail(schemaLabel,id);
        return result;
    }

    /**
     * 获取购方名称和税号
     * @param schemaLabel
     * @param userId
     * @return
     */
    @Override
    public List<OrgEntity> getGfNameAndTaxNo(String schemaLabel,Long userId) {
        return orgDao.getGfNameAndTaxNo(schemaLabel,userId);
    }

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public DetailVehicleEntity getVehicleDetail(String schemaLabel,Long id)throws Exception{
        return detailDao.getVehicleDetail(schemaLabel,id);
    }

    /**
     *获取明细表信息数量
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public int getInvoiceDetailTotal(String schemaLabel,Long id) {
        return detailDao.getInvoiceDetailTotal(schemaLabel,id);
    }


}
