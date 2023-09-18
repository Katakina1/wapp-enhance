package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * upc_test
 * @author 
 */
@Data
@TableName("t_xf_manager_seller_setting")
public class ManagerSellerSettingEntity {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String sellerName;
    private String sellerNo;
    private String lockFlag;
    private String deleteFlag;
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
}