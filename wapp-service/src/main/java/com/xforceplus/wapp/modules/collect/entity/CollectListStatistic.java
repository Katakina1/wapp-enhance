package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 采集列表数据实体
 *
 * @author Colin.hu
 * @date 4/12/2018
 */
@Getter
@Setter
@ToString
public class CollectListStatistic extends AbstractBaseDomain {

    private static final long serialVersionUID = -4109272561450241091L;

    /**
     * 购方税号
     */
    private String gfTaxNo;

    /**
     * 采集时间
     */
    private String createDate;

    /**
     * 购方名称
     */
    private String gfName;

    /**
     * 采集数量合计
     */
    private Integer collectCount;

    /**
     * 未税金额合计
     */
    private String sumTotalAmount;

    /**
     * 税额合计
     */
    private String sumTaxAmount;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
