package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_xf_taxcode_manage")
public class TaxCodeManageEntity {
     private int id;
     private String taxNo;
     private String taxName;
     private String taxRemark;
     private Date createTime;
     private String province;
}