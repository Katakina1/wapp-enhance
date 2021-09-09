package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.certification.dao.ManualCheckDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ManualCheckService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 手工勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
@Service
public class ManualCheckImpl implements ManualCheckService {

    private ManualCheckDao manualCheckDao;

    @Autowired
    public ManualCheckImpl(ManualCheckDao manualCheckDao) {
        this.manualCheckDao = manualCheckDao;
    }

    /**
     * 手工勾选
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    @Override
    public List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return manualCheckDao.queryList(schemaLabel,map);
    }

    /**
     * 手工勾选
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据数
     */
    @Override
    public ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return manualCheckDao.queryTotal(schemaLabel,map);
    }


    /**
     * 手工勾选
     * @param ids 处理的的id
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    @Override
    public Boolean manualCheck(String schemaLabel,String ids,String loginName,String userName){
        Boolean flag=true;
        if (ids.split(",").length > 0) {
            final String[] id = ids.split(",");
            for (String anId : id) {
                flag=manualCheckDao.manualCheck(schemaLabel,anId,loginName,userName)>0;
                if(!flag){
                    return false;
                }
            }
        }
       return flag;
    }

    


}
