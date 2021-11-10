package com.xforceplus.wapp.util;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

/**
 * 类描述：
 *
 * @ClassName CodeGenerator
 * @Description TODO
 * @Author ZZW
 * @Date 2021/11/8 14:24
 */
public class CodeGenerator {
    public static String generateCode(TXfDeductionBusinessTypeEnum tx) {
        String code = DateUtils.format(new Date(), "yyyyMMdd");
        String random = RandomStringUtils.randomAlphanumeric(4);
        code = tx.getPrefix().concat(code).concat(random);
        return code;
    }

}
