package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.modules.signin.entity.InvoiceImgSavePo;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScanResultVo;
import com.xforceplus.wapp.modules.signin.entity.SignedInvoiceVo;

import java.util.List;

public interface ScannerSignService {

    String sjnum();
    int signUseRecord(SignedInvoiceVo invoiceVo, Long id);

    void saveImg(InvoiceImgSavePo savePo);

    InvoiceScanResultVo insertFromScan(List<SignedInvoiceVo> invoiceList, Long id);

    void invoiceDelete(String uuId);
}
