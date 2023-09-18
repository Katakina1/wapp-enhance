package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @Description  海关票勾选操作记录
 * @Author pengtao
 * @return
**/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_customs_log")
public class TDxCustomsLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
        //缴款书表主键
    private Long customsId;
        //缴款书号码
    private String customsNo;
        //类型抵扣勾选、不抵扣勾选、撤销勾选、统计、撤销统计、确认、撤销确认、入账、撤销入账
    private String type;
        //申请时间
    private Date checkTime;
        //申请完成时间
    private String checkResultTime;
        //返回报文
    private String resultMsg;
        //完成结果
    private String resultStatus;
        //失败原因
    private String failMsg;
        //操作人Id
    private Long userId;
        //操作人名称
    private String userName;
        //创建时间
    private Date createTime = new Date();
        //更新时间
    private Date updateTime = new Date();
}
