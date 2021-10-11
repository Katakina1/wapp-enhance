package com.xforceplus.wapp.service;


import com.xforceplus.wapp.enums.XfPreInvoiceEnum;
import org.springframework.stereotype.Service;

/**
 * 预制发票操作
 */
@Service
public class PreInvoiceService {

    /**
     * 修改预制发票状态
     * 撤销预制发票需要撤销红字信息
     * @param xfPreInvoiceId
     * @param xfPreInvoiceEnum
     * @return
     */
    public boolean updatePreInvoice(Long xfPreInvoiceId, XfPreInvoiceEnum xfPreInvoiceEnum){
        return true;
    }

}
