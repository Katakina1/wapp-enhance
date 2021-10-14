package com.xforceplus.wapp.modules.rednotification.mapstruct;

import com.xforceplus.wapp.sequence.IDSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Component
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


}
