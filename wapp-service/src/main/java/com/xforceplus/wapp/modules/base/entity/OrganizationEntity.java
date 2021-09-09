package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 组织机构
 * <p>
 * Created by Daily.zhang on 2018/04/12.
 */
@Getter
@Setter
@ToString
public class OrganizationEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    /**
     * 用户税号关联表id
     */
    private Integer userTaxnoId;

    /**
     * 机构id
     */
    private Long orgid;

    /**
     * 机构编码
     */
    private String orgcode;

    /**
     * 机构名称
     */
    private String orgname;

    /**
     * 纳税人识别号
     */
    private String taxno;

    /**
     * 纳税人名称
     */
    private String taxname;

    /**
     * 上级机构id
     */
    private Integer parentid;

    /**
     * 机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     */
    private String orgtype;

    private String orgtypeStr;

    /**
     * 联系人
     */
    private String linkman;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 邮政编码
     */
    private String postcode;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String account;

    /**
     * 是否有下级[０－无；１－有]
     */
    private String isbottom;

    /**
     * 机构级别
     */
    private Integer orglevel;

    /**
     * 机构层级代码
     */
    private String orglayer;

    /**
     * 所属中心企业
     */
    private String company;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    private String sortno;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    /**
     * 修改人
     */
    private String lastModifyBy;

    /**
     * 公司类型 0国家，1企业
     */
    private Integer comType;

    /**
     * 是否加入黑名单  0未加入 1 已加入
     */
    private Integer isBlack;
    /**
     *  COMPANYCODE
     */
    private String companyCode;
    /**
     * 门店号
     */
    private String storeNumber;
    /**
     * 扩展字段1(extension field)
     */
    private String extf0;

    /**
     * 扩展字段2(extension field)
     */
    private String extf1;

    /**
     * 扩展字段3(extension field)
     */
    private String extf2;

    /**
     * 扩展字段4(extension field)
     */
    private String extf3;

    /**
     * 扩展字段5(extension field)
     */
    private String extf4;

    /**
     * 扩展字段6(extension field)
     */
    private String extf5;

    /**
     * 扩展字段7(extension field)
     */
    private String extf6;

    /**
     * 扩展字段8(extension field)
     */
    private String extf7;

    /**
     * 扩展字段9(extension field)
     */
    private String extf8;

    /**
     * 扩展字段10(extension field)
     */
    private String extf9;

    private Long[] orgIds;

    //数据库连接名
    private String linkName;

    //是否新增数据库(1：新增)
    private String isInsert;

    //三期接口升级添加是否升级
    private String isUpdate;

    //是否为叶子节点
    private Boolean isLeaf = Boolean.FALSE;

    //是否打开节点,默认关闭
    private Boolean open = Boolean.FALSE;

    //子节点
    private List<OrganizationEntity> children;

    /**
     * 子级组织id
     */
    private String orgChildStr;

    private String orgldStr;

    private  Integer type;

    private List<Long> ids;

    public Date getCreateTime() {
        return DateUtils.obtainValidDate(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = DateUtils.obtainValidDate(createTime);
    }

    public Date getLastModifyTime() {
        return DateUtils.obtainValidDate(this.lastModifyTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = DateUtils.obtainValidDate(lastModifyTime);
    }

    public Long[] getOrgIds() {
        return (orgIds == null) ? null : Arrays.copyOf(orgIds, orgIds.length);
    }

    public void setOrgIds(Long[] orgIds) {
        this.orgIds = orgIds == null ? null : Arrays.copyOf(orgIds, orgIds.length);
    }

}
