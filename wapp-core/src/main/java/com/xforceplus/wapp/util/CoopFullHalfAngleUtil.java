package com.xforceplus.wapp.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 全半角转换工具类
 * @author wang_xianrui
 */
public class CoopFullHalfAngleUtil {

  /**
   * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
   * 半角!
   */
  static final char DBC_CHAR_START = 33;

  /**
   * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
   * 半角~
   */
  static final char DBC_CHAR_END = 126;

  /**
   * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
   * 全角！
   */
  static final char SBC_CHAR_START = 65281;

  /**
   * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
   * 全角～
   */
  static final char SBC_CHAR_END = 65374;

  /**
   * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
   * 全角半角转换间隔
   */
  static final int CONVERT_STEP = 65248;

  /**
   * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
   * 全角空格 12288
   */
  static final char SBC_SPACE = 12288;

  /**
   * 半角空格的值，在ASCII中为32(Decimal)
   * 半角空格
   */
  static final char DBC_SPACE = ' ';

  /**
   * <PRE>
   * 半角字符->全角字符转换
   * 只处理空格，!到˜之间的字符，忽略其他
   * </PRE>
   */
  public static String bj2qj(String src) {
    if (src == null) {
      return src;
    }
    StringBuilder buf = new StringBuilder(src.length());
    char[] ca = src.toCharArray();
    for (int i = 0; i < ca.length; i++) {
      /**
       * 如果是半角空格，直接用全角空格替代
       */
      if (ca[i] == DBC_SPACE) {
        buf.append(SBC_SPACE);
      /**
       * 字符是!到~之间的可见字符
       */
      } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) {
        buf.append((char) (ca[i] + CONVERT_STEP));
      /**
       * 不对空格以及ascii表中其他可见字符之外的字符做任何处理
       */
      } else {
        buf.append(ca[i]);
      }
    }
    return buf.toString();
  }

  /**
   * <PRE>
   * 全角字符->半角字符转换
   * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
   * </PRE>
   */
  public static String qj2bj(String src) {
    if (src == null) {
      return src;
    }
    StringBuilder buf = new StringBuilder(src.length());
    char[] ca = src.toCharArray();
    for (int i = 0; i < src.length(); i++) {
      /**
       * 如果位于全角！到全角～区间内
       */
      if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) {
        buf.append((char) (ca[i] - CONVERT_STEP));
      /**
       * 如果是全角空格
       */
      } else if (ca[i] == SBC_SPACE) {
        buf.append(DBC_SPACE);
      /**
       * 不处理全角空格，全角！到全角～区间外的字符
       */
      } else {
        buf.append(ca[i]);
      }
    }
    return buf.toString();
  }

  /**
   * 比较两个值是否相等，相等返回true,否则false,如有一方为空，则返回true
   * @param value1
   * @param value2
   * @return
   */
  public static boolean compare(String value1,String value2){
    if (StringUtils.isBlank(value1) || StringUtils.isBlank(value2) ){
      /**
       * 如果比较双方有一方为空，则不进行比较，默认比对一致
       */
      return true;
    }
    if ( qj2bj(value1).equals( qj2bj(value2) )){
      /**
       * 比对一致
       */
      return true;
    }
    return false;
  }

  /**
   * 判断前一个值是否包含后一个值
   * @param value1
   * @param value2
   * @return
   */
  public static boolean contains(String value1,String value2){
    return qj2bj(value1).contains(qj2bj(value2));
  }

}