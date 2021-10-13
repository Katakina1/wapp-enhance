package com.xforceplus.wapp.service;

import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公共的预制发票逻辑操作
 */
@Service
public class CommPreInvoiceService {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;

    /**
     * 回填红字信息 回填红字信息
     *
     * @param proInvoiceId
     * @param redNotification
     * @return
     */
    @Transactional
    public void fillPreInvoiceClaimRedNotification(Long proInvoiceId, String redNotification) {
        //修改预制发票表
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setId(proInvoiceId);
        tXfPreInvoiceEntity.setRedNotificationNo(redNotification);
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfPreInvoiceEntity.setRedNotificationFlag(2);
        tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity);
    }

}
