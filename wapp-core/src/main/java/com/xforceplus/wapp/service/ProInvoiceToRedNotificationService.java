package com.xforceplus.wapp.service;

import com.xforceplus.wapp.dto.ApplyProInvoiceRedNotificationDTO;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import org.springframework.stereotype.Service;

/**
 * 预制发票与红字信息申请撤销 相关操作
 */
@Service
public class ProInvoiceToRedNotificationService {

    /**
     * 申请红字信息
     * @param businessNo 业务单号
     * @param businessType 业务单号类型
     * @param applyProInvoiceRedNotificationDTO 预制发票信息
     * @return
     */
    public boolean applyProInvoiceRedNotification(String businessNo,Integer businessType,
                                                  ApplyProInvoiceRedNotificationDTO applyProInvoiceRedNotificationDTO){


        RedNotificationMain redNotificationMain = convertApplyProInvoiceRedNotificationDTOToRedNotificationMain(applyProInvoiceRedNotificationDTO);

        //TODO 调用申请
        return true;
    }

    /**
     * 撤销红字信息
     * @param proInvoiceId 预制发票id
     * @return
     */
    public boolean repealProInvoiceClaimRedNotification(Long proInvoiceId){
        //TODO 调用撤销
        return true;
    }

    /**
     * 回填红字信息 回填红字信息
     * @param proInvoiceId
     * @param redNotification
     * @return
     */
    public boolean fillProInvoiceClaimRedNotification(Long proInvoiceId,String redNotification){
        //TODO 修改预制发票表
        return true;
    }

    private RedNotificationMain convertApplyProInvoiceRedNotificationDTOToRedNotificationMain(ApplyProInvoiceRedNotificationDTO applyProInvoiceRedNotificationDTO){
        RedNotificationMain redNotificationMain = new RedNotificationMain();

        return redNotificationMain;
    }

}
