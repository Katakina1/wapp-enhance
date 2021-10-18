package com.xforceplus.wapp.modules.rednotification.mapstruct;

import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Component
@Slf4j
public class RedNotificationFactory {
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    IDSequence iDSequence;

    public String dateToString(Date date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return format.format(date);
    }

    public String bigDecimalToString(BigDecimal bigDecimal){
        if(Objects.isNull(bigDecimal)){
            return "";
        }
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    public  Long nextId(){
        return iDSequence.nextId();
    }


    /**
     * 处理税率% 问题
     */
    public static   BigDecimal handleTaxRate(String taxRate){
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

}
