package com.xforceplus.wapp.common.dto.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe: 红字信息表查询状态参数
 *
 * @Author xiezhongyong
 * @Date 2022/9/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedNotificationQueryParam {

    /** 红字信息申请状态*/
    private List<Integer> applyingStatus = new ArrayList<>();

    /** 红字信息审批状态*/
    private List<Integer> approveStatus = new ArrayList<>();


}
