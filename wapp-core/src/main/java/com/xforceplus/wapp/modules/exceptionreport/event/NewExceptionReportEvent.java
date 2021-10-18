package com.xforceplus.wapp.modules.exceptionreport.event;

import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增例外报告事件
 *
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 20:24
 **/
@Setter
@Getter
public class NewExceptionReportEvent {
    private TXfExceptionReportEntity entity;
}
