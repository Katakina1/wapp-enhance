package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.redTicket.dao.InvoiceDetailDao;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.service.InvoiceDetailService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
@Service
public class InvoiceDetailServiceImpl implements InvoiceDetailService {
    private static final Logger LOGGER= getLogger(InvoiceDetailServiceImpl.class);
    private final InvoiceDetailDao invoiceDetailDao;
    @Autowired
    public InvoiceDetailServiceImpl(InvoiceDetailDao invoiceDetailDao){
        this.invoiceDetailDao=invoiceDetailDao;
    }

    @Override
    public List<InvoiceDetail> getInvoiceDetaillist(Map<String, Object> params){
       return invoiceDetailDao.getInvoiceDetaillist(params);
    }

    @Override
    public Integer invoiceDetailsCount(Map<String, Object> map){
       return invoiceDetailDao.invoiceDetailsCount(map);
    }
}
