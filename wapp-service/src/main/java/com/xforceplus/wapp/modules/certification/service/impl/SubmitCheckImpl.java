package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.certification.dao.ManualCertificationDao;
import com.xforceplus.wapp.modules.certification.dao.SubmitCheckDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.SubmitCheckService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 勾选勾选发票确认
 * @author kevin.wang
 * @date 4/14/2018
 */
@Service
public class SubmitCheckImpl implements SubmitCheckService {

    private SubmitCheckDao submitCheckDao;
    @Autowired
    private ManualCertificationImpl manualCertification;
    
    @Autowired
    private ManualCertificationDao manualCertificationDao;

    @Autowired
    public SubmitCheckImpl(SubmitCheckDao submitCheckDao) {
        this.submitCheckDao = submitCheckDao;
    }

    @Override
    public List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return submitCheckDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return submitCheckDao.queryTotal(schemaLabel,map);
    }

    @Override
    public Boolean submitCheck(String schemaLabel,String ids,String loginName,String userName){
        Boolean flag=true;
        String rzhBelongDate="";
        if (ids.split(",").length > 0) {
            final String[] id = ids.split(",");
            for (String anId : id) {
                //获取税款所属期
                rzhBelongDate=manualCertification.getCurrentTaxPeriod(schemaLabel,anId);
                flag=submitCheckDao.submitCheck(schemaLabel,anId,loginName,userName,rzhBelongDate)>0;
                if(!flag){
                    return false;
                }
            }
        }
        return flag;
    }

    @Override
    public Boolean cancelCheck(String schemaLabel,String ids){
        Boolean flag=true;
        if (ids.split(",").length > 0) {
            final String[] id = ids.split(",");
            for (String anId : id) {
                flag=submitCheckDao.cancelCheck(schemaLabel,anId)>0;
                if(!flag){
                    return false;
                }
            }
        }
        return flag;
    }


}
