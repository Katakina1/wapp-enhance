package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.exceptionreport.BillJobOriginDataTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 11:30
 **/
@Setter
@Getter
public class BillJobOriginDataExportDto {
    private BillJobOriginDataTypeEnum type;
    private String exchangeNo;
    private Date startDate;
    private Date endDate;
    private String jobName;

    private Long logId;

    private Long userId;


    /**
     *
     */
    private String loginName;
}
