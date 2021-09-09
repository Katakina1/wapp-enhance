package com.xforceplus.wapp.modules.enterprise.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 企业信息实体类(企业信息表)
 * Created by vito.xing on 2018/4/12
 */
@Getter @Setter @ToString
public class EnterpriseEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 3740169743643390074L;

    private String orgCode;//企业编码

    @Length(max = 50)
    private String orgName;//企业名称

    @Length(max = 30)
    private String taxNo; //纳税人税号

    private String taxName;//纳税人名称

    private String orgType;//机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)

    private String company;//中心企业

    private String email; //电子邮箱

    private String postcode; //邮政编码

    private String address;

    private String phone;

    private String addressPhone; //地址，电话

    private String linkman;//企业联系人

    private Date createTime; //创建时间

    private String bank;//银行名称

    private String account;//银行账号

    private String bankAccount; //开户行及银行账号

    private Integer isBlack; //是否加入黑名单(0-未加入 1-已加入)

    private String remark; //备注

    private String lastModifyBy; //修改人

    private String comType; //公司类型 (0-国家 1-企业)

    private String createBy;//创建人

    private Long userId;//当前登录人Id

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
