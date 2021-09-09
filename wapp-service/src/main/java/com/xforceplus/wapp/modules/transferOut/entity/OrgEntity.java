package com.xforceplus.wapp.modules.transferOut.entity;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/18
 * Time:10:03
*/

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrgEntity extends AbstractBaseDomain {
    private Long orgid;// 机构id
    private String orgcode;//机构编码
    private String orgname;//机构名称
    private String taxno;//纳税人识别号
    private String taxname;//纳税人名称
    private Long parentid;//上级机构id
    private String orgtype;//机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
    private String linkman;//联系人
    private String phone;//联系电话
    private String address;//联系地址
    private String email;//电子邮箱
    private String postcode;//邮政编码
    private String bank;//开户行
    private String account;//银行帐号
    private String isbottom;//是否有下级[０－无；１－有]
    private Long orglevel;//机构级别
    private String orglayer;//机构层级代码
    private String company;//所属中心企业
    private String remark;//备注
    private String sortno;//排序字段
    private Date createTime;//创建时间
    private String createBy;//创建人
    private Date lastModifyTime;//修改时间
    private String lastModifyBy;//修改人
    private String comType;//公司类型 0国家，1企业
    private String isBlack;//是否加入黑名单  0未加入 1 已加入

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
