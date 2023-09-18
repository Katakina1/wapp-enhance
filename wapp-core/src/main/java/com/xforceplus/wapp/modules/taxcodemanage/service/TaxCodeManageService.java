package com.xforceplus.wapp.modules.taxcodemanage.service;

import com.xforceplus.wapp.repository.daoExt.TaxCodeManageDao;
import com.xforceplus.wapp.repository.entity.TaxCodeManageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxCodeManageService {
    @Autowired
    TaxCodeManageDao taxCodeManageDao;

    public List<TaxCodeManageEntity> selectAll(String taxNo, String taxName) {
        return taxCodeManageDao.selectAll(taxNo, taxName);
    }

    public int addTaxCode(TaxCodeManageEntity taxCodeManageEntity) {
        return taxCodeManageDao.addTaxCode(taxCodeManageEntity);
    }

    public int editTaxCode(TaxCodeManageEntity taxCodeManageEntity) {
        return taxCodeManageDao.editTaxCode(taxCodeManageEntity);
    }

    public boolean deleteTaxCode(TaxCodeManageEntity taxCodeManageEntity) {
        return taxCodeManageDao.deleteTaxCode(taxCodeManageEntity);
    }

}
