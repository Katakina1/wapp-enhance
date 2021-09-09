package com.xforceplus.wapp.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 公共工具类
 *
 * @author Colin.hu
 * @date 4/23/2018
 */
public class CommonUtil {

    private final static Logger LOGGER = getLogger(CommonUtil.class);

    private static String[] fpdmlist = {"144031539110", "131001570151", "133011501118", "111001571071"};


    /**
     * 根据发票代码获取发票类型
     *
     * @param fpdm 发票代码
     * @return 发票类型
     */

    public static String getFplx(String fpdm) {
        LOGGER.info("从发票代码中获取发票类型,参数为：{}", fpdm);

        String fplx = "";
        if (fpdm.trim().length() == 12) {
            String fplxflag = fpdm.substring(7, 8);
            for (int i = 0; i < fpdmlist.length; i++) {
                if (fpdm.equals(fpdmlist[i])) {
                    fplx = "10";
                    break;
                }
            }
            if ("0".equals(fpdm.substring(0, 1)) && "11".equals(fpdm.substring(10, 12))) {
                fplx = "10";
            }
            if ("0".equals(fpdm.substring(0, 1)) && "12".equals(fpdm.substring(10, 12))) {
                fplx = "14";
            }
            if ("0".equals(fpdm.substring(0, 1)) && ("06".equals(fpdm.substring(10, 12)) || "07".equals(fpdm.substring(10, 12)))) {
                //判断是否为卷式发票  第1位为0且第11-12位为06或07
                fplx = "11";
            }
            if ("2".equals(fplxflag) && !"0".equals(fpdm.substring(0, 1))) {
                fplx = "03";
            }
            if ("0".equals(fpdm.substring(0, 1)) && ("04".equals(fpdm.substring(10, 12)) || "05".equals(fpdm.substring(10, 12)))) {
                fplx = "04";
            }
        } else if (fpdm.trim().length() == 10) {
            String fplxflag = fpdm.substring(7, 8);
            if ("1".equals(fplxflag) || "5".equals(fplxflag)) {
                fplx = "01";
            } else if ("6".equals(fplxflag) || "3".equals(fplxflag)) {
                fplx = "04";
            } else if ("7".equals(fplxflag) || "2".equals(fplxflag)) {
                fplx = "02";
            }
        }
        LOGGER.info("发票类型为：{}", fplx);
        return fplx;
    }

    /**
     * 金额保留2位小数,不足补0
     *
     * @param value
     * @return
     */
    public static String formatMoney(Double value) {
        if (value == null) {
            return "";
        }
        Boolean isMinus = false;
        if (value < 0.0) {
            value = -value;
            isMinus = true;
        }
        String valueStr = String.valueOf(value);
        int index = valueStr.indexOf(".");
        if (index < 0) {
            //整数
            valueStr += ".00";
        }
        if (valueStr.length() == index + 2) {
            //1位小数
            valueStr += "0";
        }
        index = valueStr.indexOf('.');
        while (index - 3 > 0) {
            valueStr = valueStr.substring(0, index - 3) + "," + valueStr.substring(index - 3);
            index = index - 3;
        }
        return isMinus ? "-" + valueStr : valueStr;
    }

    /**
     * 时间格式校验
     *
     * @param str       要校验的时间字符串
     * @param formatter 时间格式
     * @param regex     时间格式正则 可为空
     * @return 成功true 失败false
     */
    public static Boolean isValidDate(String str, String formatter, String regex) {
        Boolean flag = Boolean.FALSE;
        if (!StringUtils.isEmpty(regex)) {
            final Pattern pattern = Pattern.compile(regex);
            final Matcher m = pattern.matcher(str);
            final Boolean dateFlag = m.matches();
            if (!dateFlag) {
                return Boolean.FALSE;
            }
        }
        final DateFormat dateFormat = new SimpleDateFormat(formatter);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(str);
            flag = Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.info("时间格式错误:{}", e);
        }
        return flag;
    }

    /**
     * 数字字符串格式校验
     *
     * @param str   字符串
     * @param regex 正则
     * @return 成功true 失败false
     */
    public static Boolean isValidNum(String str, String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher m = pattern.matcher(str);
        return m.matches();
    }

    public static Boolean isNumber(String str){
        String reStr = str.replace(",", "");
        String reg = "^[0-9]+(.[0-9]+)?$";
        return reStr.matches(reg);
    }
}
