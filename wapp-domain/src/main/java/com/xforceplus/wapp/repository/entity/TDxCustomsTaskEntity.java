package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;


/**
 * 海关票定时任务
 *
 * @author pengtao
 * @email pengtao@xforceplus.com
 * @date 2023-06-16 19:24:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_customs_task")
public class TDxCustomsTaskEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
        //任务流水号
    private String taskId;
    //海关缴款书Id
    private Long customsId;
        //海关缴款书号码
    private String customsNo;
        //购方税号
    private String buyerTaxNo;
        //所属期
    private String taxPeriod;
        //认证用途 1-抵扣勾选 10-撤销抵扣勾选  3-退税勾选 30-退税撤销勾选（确认后无法撤销）
        //02-入账（企业所得税税前扣除）,03-入账（企业所得税不扣除）,06-入账撤销
    private String authUse;
        //有效税额
    private String effectiveTaxAmount;
        //开票日期
    private String dateIssued;
        //请求文本
    private String sendMsg;
        //回执文本
    private String resultMsg;
        //定时任务状态，0未完成1已完成
    private Integer status;
        //执行次数
    private Integer num;
        //创建时间
    private Date createTime = new Date();
        //更新时间
    private Date updateTime= new Date();
}
