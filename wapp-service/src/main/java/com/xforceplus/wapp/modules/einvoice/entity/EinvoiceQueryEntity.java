package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Max.han on 2018/04/12.
 *
 * @author 电票查询实体类
 */
@Getter
@Setter
@ToString
public class EinvoiceQueryEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1841531024607174383L;

    /**
     * 购方税号
     */
    private String gfTaxNo;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 签收开始日
     */
    private String qsStartDate;

    /**
     * 签收结束日期
     */
    private String qsEndDate;

    /**
     * 用户关联的税号
     */
    private List<String> taxNos;

    private Long userId;

}
