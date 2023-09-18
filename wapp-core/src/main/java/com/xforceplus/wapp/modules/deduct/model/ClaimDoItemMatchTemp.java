package com.xforceplus.wapp.modules.deduct.model;

import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 索赔单明细匹配计算金额中间处理
 * @date : 2022/10/27 10:13
 **/
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClaimDoItemMatchTemp {

    /**
     * 更新信息
     */
    private TXfBillDeductItemEntity itemUpdate;

    /**
     * 明细关系
     */
    private TXfBillDeductItemRefEntity itemRefEntity;

    /**
     * 明细原数量是否整数
     */
    private boolean integerFlag;

    /**
     * 明细剩余可用数量
     */
    private BigDecimal canUseQuantity;

    /**
     * 此次使用数量
     */
    private BigDecimal useQuantity;

    /**
     * 明细剩余可用金额
     */
    private BigDecimal remainingAmount;

    /**
     * 此次使用金额
     */
    private BigDecimal useAmount;

    /**
     * 明细税率
     */
    private BigDecimal taxRate;

    public void setItem(TXfBillDeductItemEntity itemEntity) {
        if (itemUpdate == null) {
            itemUpdate = new TXfBillDeductItemEntity();
        }
        itemUpdate.setId(itemEntity.getId());
        itemUpdate.setItemNo(itemEntity.getItemNo());
        itemUpdate.setGoodsTaxNo(itemEntity.getGoodsTaxNo());
        itemUpdate.setTaxPre(itemEntity.getTaxPre());
        itemUpdate.setTaxPreCon(itemEntity.getTaxPreCon());
        itemUpdate.setZeroTax(itemEntity.getZeroTax());
        itemUpdate.setItemShortName(itemEntity.getItemShortName());
    }

    /**
     * 针对最后一条匹配明细， 判断明细是否需要因为拆分数量取整失败，而让其他明细进行拆分
     * @return true-需要改变拆分的明细
     */
    public boolean needChangeSplit() {
        // 原数量非整数，无需保持数量整数
        if (!this.isIntegerFlag()) {
            return false;
        }

        // 剩余数量全部使用了，但可用金额还未使用完
        return this.getCanUseQuantity().abs().compareTo(BigDecimal.ONE) == 0
                && this.getRemainingAmount().abs().compareTo(this.getUseAmount().abs()) > 0;
    }

    /**
     * 除最后一条匹配明细外，从后往前判断当前明细是否需要需要因为拆分数量取整失败，而让其他明细进行拆分
     * 不需要的话，说明当前明细可以进行拆分
     * @param overAmount 改变拆分明细时，最后一条明细需整个使用，多出来的金额需要在其他确定拆分的明细中扣除
     * @return true-需要改变拆分的明细
     */
    public boolean needChangeSplit(BigDecimal overAmount) {
        // 待拆分金额大于明细剩余金额
        if (overAmount.abs().compareTo(this.getRemainingAmount().abs()) > 0) {
            return true;
        }
        // 原数量非整数，无需保持拆分数量为整数
        if (!this.isIntegerFlag()) {
            return false;
        }

        return this.getCanUseQuantity().abs().compareTo(BigDecimal.ONE) == 0;
    }
}
