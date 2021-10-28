package com.xforceplus.wapp.modules.rednotification.mapstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class ConvertHelper {
    /**
     * 处理税率% 问题
     */
    public static BigDecimal handleTaxRate(String taxRate){
        //处理带百分号的税率
        try {
            //如果是标准的数字格式字符串
            BigDecimal decimal = new BigDecimal(taxRate);
            return  decimal;
        }catch (Exception e){
            String substring = taxRate.substring(0,taxRate.length()-1);
            try {
                BigDecimal decimal = new BigDecimal(substring);
                BigDecimal divide = decimal.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
                return  divide;
            }catch (Exception e1) {
                log.error("field {} value {} e {}", "taxRate", taxRate, e1);

            }
        }
        return null;
    }

    /**
     * 获取税编版本
     */
    public static String getGoodsNoVer(String goodsNoVer){
        if (StringUtils.isEmpty(goodsNoVer)){
            return "33.0";
        }else {
            return goodsNoVer;
        }

    }
}
