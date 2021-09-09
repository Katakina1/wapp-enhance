package com.xforceplus.wapp.modules.einvoice.util;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Marvin
 */
public final class DateTimeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);

    private static final String TIME_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN_TIME = "yyyy-MM-dd HH:mm";
    private static final String DATEPATTERN_TIME = "yyyyMMdd";
    private static final String DATEPATTERN_TIME_MATH = "yyyyMM.dd";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(TIME_PATTERN);
    private static final SimpleDateFormat FORMATTER_TIME =  new SimpleDateFormat(TIME_PATTERN_TIME);
    private static final SimpleDateFormat FORMATTER_DATE =  new SimpleDateFormat(DATEPATTERN_TIME);
    private static final SimpleDateFormat FORMATTER_DATE_MATH = new SimpleDateFormat(DATEPATTERN_TIME_MATH);
    private static final String PARSE_ERROR = "Parse Date error";

    private DateTimeHelper() {
    }

    /**
     * 复制一个有效的时间
     *
     * @param srcDate 日期时间
     * @return clone的日期时间。如果为null，则返回null
     */
    public static Date obtainDateClone(final Date srcDate) {
        return srcDate == null ? null : new Date(srcDate.getTime());
    }

    /**
     * 把指定格式(YYYY-MM-DD)的字符串转换为时间
     *
     * @param dt 指定格式(YYYY-MM-DD)的字符串
     * @return 转换的时间格式。如果为空字符，则返回null
     */
    public static Date parseDateTime(final String dt) {
        Date result = null;
        try {
            result = Strings.isNullOrEmpty(dt) ? null : FORMATTER.parse(dt);
        } catch (Exception e) {
            LOGGER.warn(PARSE_ERROR, e);
        }
        return result;
    }

    /**
     * 把指定格式(YYYY-MM-DD)的字符串转换为时间
     *
     * @param dt 指定格式(YYYY-MM-DD HH;mm)的字符串
     * @return 转换的时间格式。如果为空字符，则返回null
     */
    public static Date parseDateTimeS(final String dt) {
        Date result = null;
        try {
            result = Strings.isNullOrEmpty(dt) ? null : FORMATTER_TIME.parse(dt);
        } catch (Exception e) {
            LOGGER.warn(PARSE_ERROR, e);
        }
        return result;
    }

    /**
     * 把指定格式(YYYYMMDD)的字符串转换为时间
     *
     * @param dt 指定格式(YYYYMMDD)的字符串
     * @return 转换的时间格式。如果为空字符，则返回null
     */
    public static Date parseDate(final String dt) {
        Date result = null;
        try {
            result = Strings.isNullOrEmpty(dt) ? null : FORMATTER_DATE.parse(dt);
        } catch (Exception e) {
            LOGGER.warn(PARSE_ERROR, e);
        }
        return result;
    }

    /**
     * 把指定格式(format)的字符串转换为时间
     *
     * @param dt     指定格式的字符串
     * @param format 指定格式(format)的字符串
     * @return 转换的时间格式。如果为空字符，则返回null
     */
    public static Date parseDateTime(final String dt, final String format) {
        SimpleDateFormat formatter =new SimpleDateFormat(format);
        try {
            return Strings.isNullOrEmpty(dt) ? null : formatter.parse(dt);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 格式化时间。返回格式为：YYYY-MM-DD
     *
     * @param source 需要格式的时间
     * @return 返回格式为：YYYY-MM-DD,如果待转换的时间为null，则返回""
     */
    public static String formatDate(final Date source) {

        return source == null ? "" : FORMATTER.format(source);
    }

    /**
     * 格式化时间。返回格式为：yyyy-MM-dd HH:mm
     *
     * @param source 需要格式的时间
     * @return 返回格式为：yyyy-MM-dd HH:mm,如果待转换的时间为null，则返回""
     */
    public static String formatDateTime(final Date source) {
        return source == null ? "" : FORMATTER_TIME.format(source);
    }

    /**
     * 格式化时间。返回格式为：YYYYMMDD
     *
     * @param dateSource 需要格式的时间
     * @return 返回格式为：YYYYMMDD,如果待转换的时间为null，则返回""
     */
    public static String formatDat(final Date dateSource) {
        return dateSource == null ? "" : FORMATTER_DATE.format(dateSource);
    }

    /**
     * 格式化时间为指定格式
     *
     * @param source  需要格式的时间
     * @param pattern 格式
     * @return 如果待转换的时间为null，则返回""， 否则返回指定格式时间字符串
     */
    public static String formatDate(final Date source, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return source == null ? "" : formatter.format(source);
    }

    /**
     * 根据格式化格式获取当前时间
     * @param pattern
     * @return
     */
    public static String formatNowDate(String pattern){
        SimpleDateFormat formatter =  new SimpleDateFormat(pattern);
        return formatter.format(new Date());
    }

    /**
     * 格式化时间。返回格式为：yyyyMM.dd
     *
     * @param source 需要格式的时间
     * @return 返回格式为：yyyyMM.dd,如果待转换的时间为null，则返回""
     */
    public static String formatDateMath(final Date source) {
        return source == null ? "" : FORMATTER_DATE_MATH.format(source);
    }




    /**
     * 某一个月第一天和最后一天
     *
     * @param date
     * @return
     */
    public static Map<String, String> getFirstAndLastDayMonth(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        Date theDate = calendar.getTime();

        //上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String dayFirst = df.format(gcLast.getTime());
        StringBuilder str = new StringBuilder().append(dayFirst);
        dayFirst = str.toString();

        //上个月最后一天
        calendar.add(Calendar.MONTH, 1);    //加一个月
        calendar.set(Calendar.DATE, 1);        //设置为该月第一天
        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
        String dayLast = df.format(calendar.getTime());
        StringBuilder endStr = new StringBuilder().append(dayLast);
        dayLast = endStr.toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", dayFirst);
        map.put("last", dayLast);
        return map;
    }

    /**
     *  计算两个日期之间相差天数
     * @param fDate 日期一
     * @param oDate 日期二
     * @return 相差天数
     */
    public static int daysOfTwo(Date fDate, Date oDate) {

        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int firstDate = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int secondDate = aCalendar.get(Calendar.DAY_OF_YEAR);
        return secondDate - firstDate;

    }

    /**
     * 日期比较-开始日期是否大于等于结束日期
     *
     * @param startDte 开始日期
     * @param endDate  结束日期
     * @return true 是 false 否
     */
    public static Boolean dateCompare(String startDte, String endDate) {
        final Date start = parseDateTime(startDte);
        final Date end = parseDateTime(endDate);

        return start.getTime() >= end.getTime();
    }

    public static Long countDay(String begin,String end){
        SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
        Date beginDate , endDate;
        long day = 0;
        try {
            beginDate= format.parse(begin);
            endDate=  format.parse(end);
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
        } catch (ParseException e) {
            LOGGER.warn(PARSE_ERROR, e);
        }
        return day;
    }

    public static String getMaxDate(String startDte, String endDate) {
        if(dateCompare( startDte,  endDate)){
            return startDte;
        }
        return endDate;
    }

    /**
     * 某年某月某日距今隔了多少个月
     *
     * @param source 日期
     * @return 月数
     */
    public static Double betweenMouth(Date source) {
        String date=formatDateMath(source);

        String nowDay=formatDateMath(new Date());

        final Double year = Double.valueOf(date.substring(0, 4));
        final Double mouth = Double.valueOf(date.substring(4, 9));

        final Double currentYear = Double.valueOf(nowDay.substring(0, 4));
        final Double currentMouth = Double.valueOf(nowDay.substring(4, 9));

        return Math.floor((currentYear - year) * 12 - mouth + currentMouth);
    }

}