package com.xforceplus.wapp.common.event;

import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 事件
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-14 14:35
 **/

@Getter
@Setter
@ToString
public class DeductRedNotificationEvent {

    /**
     * 当前时间戳
     */
    private Long timestamp;
    private DeductRedNotificationEventEnum event;

    private DeductRedNotificationModel body;


    @Getter
    @Setter
    @ToString
    public static class DeductRedNotificationModel{
        /**
         * 预制发票ID
         */
        private Long preInvoiceId;
        /**
         * 红字信息ID
         */
        private Long redNotificationId;
        /**
         * 红字信息编号
         */
        private String redNotificationNo;
        /**
         * 是否需要申请
         */
        private Boolean applyRequired;
    }

}
