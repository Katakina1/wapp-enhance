package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.AribaBillTypeDao;
import com.xforceplus.wapp.modules.base.dao.BillTypeDao;
import com.xforceplus.wapp.modules.base.entity.AribaBillTypeEntity;
import com.xforceplus.wapp.modules.base.entity.AribaBillTypeExcelEntity;
import com.xforceplus.wapp.modules.base.entity.BillTypeEntity;
import com.xforceplus.wapp.modules.base.service.AribaBillTypeService;
import com.xforceplus.wapp.modules.base.service.BillTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jingsong.mao on 2018/08/10.
 */
@Service("aribaBillTypeService")
public class AribaBillTypeServiceImpl implements AribaBillTypeService {
    @Autowired
    private AribaBillTypeDao aribaBillTypeDao;

    @Override
    public AribaBillTypeEntity queryObject(String schemaLabel, Long roleId) {
        return aribaBillTypeDao.queryObject(schemaLabel, roleId);
    }

    @Override
    public List<AribaBillTypeEntity> queryList(String schemaLabel, AribaBillTypeEntity entity) {
        return aribaBillTypeDao.queryList(schemaLabel, entity);
    }
    @Override
    public List<AribaBillTypeEntity> queryExcelList(Map<String, Object> params){
        return aribaBillTypeDao.queryExcelList(params);
    }
    @Override
    public int queryTotal(String schemaLabel, AribaBillTypeEntity entity) {
        return aribaBillTypeDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public int roleTotal(String schemaLabel, Long[] orgIds) {
        return aribaBillTypeDao.roleTotal(schemaLabel, orgIds);
    }

    @Override
    public void save(String schemaLabel, AribaBillTypeEntity entity) {
        aribaBillTypeDao.save(schemaLabel, entity);
    }

    @Override
    public void update(String schemaLabel, AribaBillTypeEntity entity) {
        aribaBillTypeDao.update(schemaLabel, entity);
    }
    @Override
    public void updateImport(String schemaLabel, AribaBillTypeEntity entity){
        aribaBillTypeDao.updateImport(schemaLabel, entity);
    }

    @Override
    public void delete(String schemaLabel, Long roleid) {
        aribaBillTypeDao.delete(schemaLabel, roleid);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] roleids) {
        return aribaBillTypeDao.deleteBatch(schemaLabel, roleids);
    }
    
    @Override
    public int queryByNameAndCode(String schemaLabel, String name, String code,String account,Integer id){
    	return aribaBillTypeDao.queryByNameAndCode(schemaLabel, name, code,account,id);
    }

    @Override
    public String queryServiceName(String code,String account){
      return  aribaBillTypeDao.queryServiceName(code,account);
    }
    @Override
    public List<AribaBillTypeExcelEntity> toExcel(List<AribaBillTypeEntity> list){
        List<AribaBillTypeExcelEntity> excelList=new ArrayList<>();
        for (AribaBillTypeEntity ae:list) {
            AribaBillTypeExcelEntity aribaExcel= new AribaBillTypeExcelEntity();
            aribaExcel.setMccCode(ae.getMccCode());
            aribaExcel.setGlAccount(ae.getGlAccount());
            aribaExcel.setServiceName(ae.getServiceName());
            if(ae.getServiceType().equals("0")){
                aribaExcel.setServiceType("资产类");
            }else{
                aribaExcel.setServiceType("费用类");
            }
            aribaExcel.setRownumber(ae.getRownumber());
            excelList.add(aribaExcel);
        }
        return  excelList;
    }
}
