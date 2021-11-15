package com.xforceplus.wapp.modules.rednotification.validator;


import com.xforceplus.phoenix.split.model.PriceMethod;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetailAmountCheckTools {

    public static String checkAmountField(ImportInfo importInfo, PriceMethod priceMethod){
        StringBuilder errorBuilder = new StringBuilder();

        BigDecimal amountWithTax = importInfo.getAmountWithTax();
        BigDecimal amountWithoutTax = importInfo.getAmountWithoutTax();
        BigDecimal taxAmount = importInfo.getTaxAmount();
        BigDecimal quantity = importInfo.getNum()==null? null:new BigDecimal(importInfo.getNum());
        BigDecimal unitPrice = importInfo.getUnitPrice()==null? null :new BigDecimal(importInfo.getUnitPrice());
        BigDecimal unitPriceWithTax = importInfo.getUnitPriceWithTax()==null? null:new BigDecimal(importInfo.getUnitPriceWithTax());
        BigDecimal taxRate = importInfo.getTaxRate()==null ?null:new BigDecimal(importInfo.getTaxRate());
        BigDecimal deduction = importInfo.getDeduction();

        if(null!=quantity){

            //单价不含税时的校验
            if(PriceMethod.WITHOUT_TAX == priceMethod){
                if(null==unitPrice || unitPrice.stripTrailingZeros().equals(BigDecimal.ZERO)){
                    errorBuilder.append("数量不为空，不含税单价必须大于0;");
                }
                if(null!=unitPrice){
                    if(null==amountWithoutTax || null == amountWithTax || null == taxAmount){
                        errorBuilder.append("不含税金额、含税金额和税额均不能为空;");
                    }else{
                        //单价不含税时：不含税单价*数量 与 传入的不含税金额 误差不能超过0.01
                        BigDecimal result1 = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP).subtract(amountWithoutTax).setScale(2, RoundingMode.HALF_UP);
                        if(result1.doubleValue()<0){
                            result1 = result1.negate();
                        }
                        if(result1.compareTo(new BigDecimal("0.01").setScale(2,RoundingMode.HALF_UP ))>0){
                            errorBuilder.append("单价不含税时：不含税单价*数量 与 传入的不含税金额 误差不能超过0.01;");
                        }

                        //单价不含税时：不含税金额+ 税额= 含税金额
                        BigDecimal calTaxAmount = amountWithTax.subtract(amountWithoutTax).setScale(2, RoundingMode.HALF_UP);
                        if (calTaxAmount.subtract(taxAmount.setScale(2, RoundingMode.HALF_UP)).abs().compareTo(new BigDecimal("0.01")) > 0) {
                            errorBuilder.append("单价不含税时：不含税金额+ 税额= 含税金额;");
                        }

                        //差额征税时：税额  =（ 含税金额 减去 扣除额 ）*  税率 \ ( 1 + 税率 ）
                        if(deduction !=null && deduction.doubleValue()>0){
                            BigDecimal calTax = ((amountWithTax.subtract(deduction.negate())).multiply(taxRate)).divide(taxRate.add(new BigDecimal("1").setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP);
                            if(!calTax.equals(taxAmount)){
                                errorBuilder.append("差额征税时，税额不正确;");
                            }
                        }

                        //单价不含税时:不含税金额* 税率 与 传入 税额误差单条不能超过 0.06
                        BigDecimal result2 = amountWithoutTax.multiply(taxRate).setScale(2, RoundingMode.HALF_UP).subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);
                        if(result2.doubleValue()<0){
                            result2 = result2.negate();
                        }
                        if(result2.compareTo(new BigDecimal("0.06").setScale(2,RoundingMode.HALF_UP ))>0){
                            errorBuilder.append("单价不含税时:不含税金额* 税率 与 传入 税额误差单条不能超过 0.06;");
                        }

                    }
                }

            }else{
                if(null==unitPriceWithTax || unitPriceWithTax.stripTrailingZeros().equals(BigDecimal.ZERO)){
                    errorBuilder.append("数量不为空，含税单价必须大于0;");
                }
                if(null!=unitPriceWithTax){
                    if(null==amountWithoutTax || null == amountWithTax || null == taxAmount){
                        errorBuilder.append("不含税金额、含税金额和税额均不能为空;");
                    }else{
                        //单价含税时：含税金额 - 税额 = 传入的不含税金额
                        BigDecimal calTaxAmount = amountWithTax.subtract(amountWithoutTax).setScale(2, RoundingMode.HALF_UP);
                        if (calTaxAmount.subtract(taxAmount.setScale(2, RoundingMode.HALF_UP)).abs().compareTo(new BigDecimal("0.01")) > 0) {
                            errorBuilder.append("单价含税时：含税金额 - 税额 = 传入的不含税金额;");
                        }
//                        if(!amountWithTax.setScale(2, RoundingMode.HALF_UP).equals(amountWithoutTax.add(taxAmount).setScale(2, RoundingMode.HALF_UP))){
//                            errorBuilder.append("单价含税时：含税金额 - 税额 = 传入的不含税金额;");
//                        }

                        //单价含税时：单价 * 数量 与传入含税金额相差不超过0.01
                        BigDecimal result1 = unitPriceWithTax.multiply(quantity).setScale(2, RoundingMode.HALF_UP).subtract(amountWithTax).setScale(2, RoundingMode.HALF_UP);
                        if(result1.doubleValue()<0){
                            result1 = result1.negate();
                        }
                        if(result1.compareTo(new BigDecimal("0.01").setScale(2,RoundingMode.HALF_UP ))>0){
                            errorBuilder.append("单价含税时：单价 * 数量 与传入含税金额相差不超过0.01;");
                        }

                        //差额征税时：税额  =（ 含税金额 减去 扣除额 ）*  税率 \ ( 1 + 税率 ）,非差额征税时税额  =含税金额*税率/(1+税率)
                        if(deduction !=null && deduction.doubleValue()>0){
                            BigDecimal calTax = ((amountWithTax.subtract(deduction.negate())).multiply(taxRate)).divide(taxRate.add(new BigDecimal("1").setScale(2, RoundingMode.HALF_UP)),2, BigDecimal.ROUND_HALF_UP);
                            if(!calTax.equals(taxAmount)){
                                errorBuilder.append("差额征税时，税额不正确;");
                            }
                        }else{
                            BigDecimal calTax = (amountWithTax.multiply(taxRate)).divide(taxRate.add(new BigDecimal("1").setScale(2, RoundingMode.HALF_UP)),2, BigDecimal.ROUND_HALF_UP);
                            if(!calTax.equals(taxAmount)){
                                errorBuilder.append("税额不正确;");
                            }
                        }
                    }
                }
            }

        }else{
            if (null!=unitPriceWithTax && PriceMethod.WITH_TAX == priceMethod){
                errorBuilder.append("数量为空，含税单价必须为空;");
                if(unitPriceWithTax.stripTrailingZeros().equals(BigDecimal.ZERO)){
                    errorBuilder.append("数量为空，含税单价必须为空且不能为0;");
                }
            }
            if (null!=unitPrice && PriceMethod.WITHOUT_TAX == priceMethod){
                errorBuilder.append("数量为空，不含税单价必须为空;");
                if(unitPrice.stripTrailingZeros().equals(BigDecimal.ZERO)){
                    errorBuilder.append("数量为空，不含税单价必须为空且不能为0;");
                }
            }
        }

        return errorBuilder.toString();
    }

}
