package com.xforceplus.wapp.modules.fixed.service.impl;


import com.xforceplus.wapp.modules.fixed.dao.SapUnconfirmDao;
import com.xforceplus.wapp.modules.fixed.service.SapUnconfirmService;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SapUnconfirmImpl implements SapUnconfirmService {
    private static final Logger LOGGER = getLogger(SapUnconfirmImpl.class);

    @Autowired
    private SapUnconfirmDao sapUnconfirmDao;

    @Override
    public List<InvoiceImportAndExportEntity> sapList(Map<String, Object> map) {
        return sapUnconfirmDao.saplist(map);
    }

    @Override
    public Integer sapCount(Map<String, Object> map) {
        return sapUnconfirmDao.sapCount(map);
    }

    @Override
    public boolean sapSuccess(Long id) {
        return sapUnconfirmDao.sapSuccess(id)>0;
    }

    @Override
    public void refund(Map<String, Object> param) {
        param.put("scanFailReason","退票");
        sapUnconfirmDao.refund(param);
        sapUnconfirmDao.refundInvoice(param);
    }


}
