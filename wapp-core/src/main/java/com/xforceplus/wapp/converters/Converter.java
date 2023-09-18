package com.xforceplus.wapp.converters;

import cn.hutool.core.util.NumberUtil;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-12-10 15:06
 **/
public abstract class Converter {

    public static final String PERCENT="%";
    public static BigDecimal convertTaxRate(String taxRate){
        if (StringUtils.isBlank(taxRate)){
            throw new EnhanceRuntimeException("税率为空");
        }
        String rate=taxRate.trim();
        if (rate.endsWith(PERCENT)){
            rate = rate.substring(0, rate.indexOf(PERCENT)).trim();
        }
        if (NumberUtil.isNumber(rate)){
            final BigDecimal bigDecimal = NumberUtil.toBigDecimal(rate);
            return Optional.of(bigDecimal).filter(x->x.compareTo(BigDecimal.ONE)<0)
                    .orElseGet(
                            ()->bigDecimal.movePointLeft(2)
                    );
        }

        throw new EnhanceRuntimeException("税率格式不正确:"+taxRate+",请确认是否为小数格式");
    }

    public static String convertUpc(String upc) {
        if (upc.endsWith("0")) {
            return "H" + upc.substring(0, upc.lastIndexOf("0"));
        }
        return upc;
    }
}
