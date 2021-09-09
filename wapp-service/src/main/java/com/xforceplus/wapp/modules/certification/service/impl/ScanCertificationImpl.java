package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.certification.dao.ManualCertificationDao;
import com.xforceplus.wapp.modules.certification.dao.ScanCertificationDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ManualCertificationService;
import com.xforceplus.wapp.modules.certification.service.ScanCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 手工认证
 * @author kevin.wang
 * @date 4/14/2018
 */
@Service
public class ScanCertificationImpl implements ScanCertificationService {

    private ScanCertificationDao scanCertificationDao;

    @Autowired
    private ManualCertificationDao manualCertificationDao;

    @Autowired
    public ScanCertificationImpl(ScanCertificationDao scanCertificationDao) {
        this.scanCertificationDao = scanCertificationDao;
    }

    @Autowired
    private ManualCertificationImpl manualCertification;

    @Override
    public InvoiceCertificationEntity selectCheck(String schemaLabel,Map<String, Object> map){
        
        //判断扫码发票是否在底账表且在当前关联税号下  true为是,false为否
        Boolean taxAccess=scanCertificationDao.selectCheckTaxAccess(schemaLabel,map)>0;

        InvoiceCertificationEntity invoiceCertificationEntity = scanCertificationDao.selectCheck(schemaLabel,map);

        if(null!=invoiceCertificationEntity){
            invoiceCertificationEntity.setTaxAccess(taxAccess);
        }
        
        return invoiceCertificationEntity;

    }
    @Override
    public Boolean scanCertification(String schemaLabel,String ids,String loginName,String userName){
        Boolean flag=true;
        String rzhBelongDate="";
        if (ids.split(",").length > 0) {
            final String[] id = ids.split(",");
            for (String anId : id) {
                //获取税款所属期
                rzhBelongDate=manualCertification.getCurrentTaxPeriod(schemaLabel,anId);
                flag=scanCertificationDao.scanCertification(schemaLabel,anId,loginName,userName,rzhBelongDate)>0;
                if(!flag){
                    return false;
                }
            }
        }
        return flag;
    }

}
