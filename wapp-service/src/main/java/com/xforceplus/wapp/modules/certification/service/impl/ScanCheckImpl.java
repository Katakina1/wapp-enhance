package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.certification.dao.ScanCheckDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ScanCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 扫码勾选
 * @author kevin.wang
 * @date 4/16/2018
 */
@Service
public class ScanCheckImpl implements ScanCheckService {

    private ScanCheckDao scanCheckDao;

    @Autowired
    public ScanCheckImpl(ScanCheckDao scanCheckDao) {
        this.scanCheckDao = scanCheckDao;
    }

    @Override
    public InvoiceCertificationEntity selectCheck(String schemaLabel,Map<String, Object> map){

        //判断扫码发票是否在底账表且在当前关联税号下  true为是,false为否
        Boolean taxAccess=scanCheckDao.selectCheckTaxAccess(schemaLabel,map)>0;

        InvoiceCertificationEntity invoiceCertificationEntity = scanCheckDao.selectCheck(schemaLabel,map);
        if(null!=invoiceCertificationEntity){
            invoiceCertificationEntity.setTaxAccess(taxAccess);
        }

        return invoiceCertificationEntity;

    }
    @Override
    public Boolean scanCheck(String schemaLabel,String ids,String loginName,String userName){
        Boolean flag=true;
        if (ids.split(",").length > 0) {
            final String[] id = ids.split(",");
            for (String anId : id) {
                flag=scanCheckDao.scanCheck(schemaLabel,anId,loginName,userName)>0;
                if(!flag){
                    return false;
                }
            }
        }
        return flag;
    }


}
