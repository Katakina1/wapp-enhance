package com.xforceplus.wapp.modules.transferOut.service.impl;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:06
*/

import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.modules.transferOut.dao.DetailDao;
import com.xforceplus.wapp.modules.transferOut.dao.InvoiceDao;
import com.xforceplus.wapp.modules.transferOut.dao.OrgDao;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.entity.OrgEntity;
import com.xforceplus.wapp.modules.transferOut.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * 根据红票信息获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetailList(String schemaLabel,Long  id) {
        List<DetailEntity> result  = detailDao.getInvoiceDetailList(schemaLabel,id);
        return result;
    }

    /**
     * 根据红票信息获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoiceEntity getInvoiceDetailList1(String schemaLabel,Long id) throws Exception{
        InvoiceEntity invoiceEntity=detailDao.getInvoiceDetailList1(schemaLabel,id);
        invoiceEntity.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(invoiceEntity.getTotalAmount()*100)));
        return invoiceEntity;
    }

    @Override
    public List<DetailEntity> getCheckInvoiceDetail(String schemaLabel,Long  id) {
        List<DetailEntity> result  = detailDao.getCheckInvoiceDetail(schemaLabel,id);
        return result;
    }

    /**
     * 根据uuid获取明细表信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetailByUUID(String schemaLabel,String  uuid) {
        List<DetailEntity> result  = detailDao.getInvoiceDetailByUUID(schemaLabel,uuid);
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

    @Override
    public DetailVehicleEntity getCheckVehicleDetail(String schemaLabel,Long id)throws Exception{
        return detailDao.getCheckVehicleDetail(schemaLabel,id);
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
