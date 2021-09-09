package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 采集列表数据实体
 *
 * @author Colin.hu
 * @date 4/12/2018
 */
@Getter
@Setter
@ToString
public class CollectListStatisticExcelEntity extends BaseRowModel {

    /**
     * 购方税号
     */
    @ExcelProperty(value={"购方税号"},index = 1)
    private String gfTaxNo;

    /**
     * 采集时间
     */
    @ExcelProperty(value={"采集时间"},index = 0)
    private String createDate;

    /**
     * 购方名称
     */
    @ExcelProperty(value={"购方名称"},index = 2)
    private String gfName;

    /**
     * 采集数量合计
     */
    @ExcelProperty(value={"集数量合计"},index = 3)
    private Integer collectCount;

    /**
     * 未税金额合计
     */
    @ExcelProperty(value={"未税金额合计"},index = 4)
    private String sumTotalAmount;

    /**
     * 税额合计
     */
    @ExcelProperty(value={"税额合计"},index = 5)
    private String sumTaxAmount;
}
