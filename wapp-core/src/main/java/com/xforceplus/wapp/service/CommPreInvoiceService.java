package com.xforceplus.wapp.service;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import org.apache.commons.lang.StringUtils;
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
     * 回填红字信息
     *
     * @param preInvoiceId
     * @param redNotification
     * @return
     */
    @Transactional
    public void fillPreInvoiceRedNotification(Long preInvoiceId, String redNotification) {
        if (preInvoiceId == null || StringUtils.isBlank(redNotification)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //修改预制发票表
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setId(preInvoiceId);
        tXfPreInvoiceEntity.setRedNotificationNo(redNotification);
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity);
    }

    /**
     * 沃尔玛申请红字信息失败
     * @param preInvoiceId
     */
    @Transactional
    public void applyPreInvoiceRedNotificationFail(Long preInvoiceId) {
        if (preInvoiceId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //修改预制发票表
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setId(preInvoiceId);
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity);
    }

}
