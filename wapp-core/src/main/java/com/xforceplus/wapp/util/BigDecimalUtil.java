package com.xforceplus.wapp.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

  /** 判断是否整数 */
  public static Boolean isInteger(BigDecimal decimal){
    if (decimal == null){
      return false;
    }
    return new BigDecimal(decimal.intValue()).compareTo(decimal) == 0;
  }
}
