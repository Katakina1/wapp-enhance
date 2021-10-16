package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * @program: wapp-generator
 * @description: 单据任务状态
 * @author: Kenny Wong
 * @create: 2021-10-14 14:20
 **/
public enum BillJobStatusEnum {
    /**
     * 任务失败
     */
    FAILED(0),

    /**
     * 任务初始化
     */
    INIT(1),

    /**
     * 数据文件下载完成
     */
    DOWNLOAD_COMPLETE(2),

    /**
     * 原始数据采集完成
     */
    SAVE_COMPLETE(3),

    /**
     * 数据梳理录入完成
     */
    FILTER_COMPLETE(4),

    /**
     * 全部完成
     */
    DONE(9);

    @Getter
    private final int jobStatus;

    BillJobStatusEnum(int jobStatus) {
        this.jobStatus = jobStatus;
    }
}
