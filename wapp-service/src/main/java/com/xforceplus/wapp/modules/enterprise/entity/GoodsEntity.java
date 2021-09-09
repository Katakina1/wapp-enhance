package com.xforceplus.wapp.modules.enterprise.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

/**
 * 商品实体类 (商品表)
 * Created by vito.xing on 2018/4/12
 */
@Getter @Setter @ToString
public class GoodsEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -6964839761867978884L;

    @Length(max = 100)
    private String goodsCode; //商品编码

    @Length(max = 100)
    private String goodsName; //商品名称

    private String ssbmId; //税收分类编码Id

    @Length(max = 100)
    private String ssbmCode; //税收商品编码

    @Length(max = 100)
    private String ssbmName; //税收商品名称

    private String isBlack; //是否加入黑名单(0-未加入 1-已加入)

    private String goodsRemark; //商品备注

    /**
     * 所属中心企业
     */
    private String company;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
