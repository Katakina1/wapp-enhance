package com.xforceplus.wapp.modules.blue.service;

import com.xforceplus.wapp.modules.backFill.model.BackFillVerifyBean;

import java.util.List;

public interface BlueInvoiceRelationService {

    boolean saveBatch(String originInvoiceNo, String originInvoiceCode, List<BackFillVerifyBean> blueInvoices) ;
}
