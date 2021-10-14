package com.xforceplus.wapp.dto;

import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApplyProInvoiceRedNotificationDTO implements Serializable {

    /**
     * 预制发票主信息
     */
    private TXfPreInvoiceEntity tXfPreInvoiceEntity;

    /**
     * 预制发票明细信息
     */
    private List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntityList;
}
