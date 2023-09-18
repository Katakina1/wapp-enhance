package com.xforceplus.wapp.repository.entity;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/18
 * Time:10:03
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_xf_supplier_sap_no_6d_mapping")
public class TXfSupplierSapNo6dMappingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("supplier_sap_no")
    private String supplierSapNo;
    @TableField("supplier_6d")
    private String supplier6d;
    @TableField("supplier_name")
    private String supplierName;

    public static final String ID = "id";

    public static final String SUPPLIER_SAP_NO = "supplier_sap_no";

    public static final String SUPPLIER_6D = "supplier_6d";

    public static final String SUPPLIER_NAME = "supplier_name";

}
