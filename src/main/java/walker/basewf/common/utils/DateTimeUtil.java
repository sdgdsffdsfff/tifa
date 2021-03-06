package walker.basewf.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 提供各种关于日期、时间的常用方法。
 *
 * @author HuQingmiao
 */
public class DateTimeUtil {

    private static Logger log = LoggerFactory.getLogger(DateTimeUtil.class);

    public static final String PRETTY_DT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String COMPACT_DT_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 将给定的日期时间字符串按照指定的格式进行解析。
     * <p/>
     * Parses text from the given string to produce a java.util.Date.
     *
     * @param datetimeStr the data/time string which format is given by the second
     *                    parameter.
     * @param format      the format of data/time string, such as: "yyyy-MM-dd
     *                    HH:mm:ss.SSS"
     */
    public static Date parse(String datetimeStr, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date d = df.parse(datetimeStr);
            return d;

        } catch (ParseException e) {
            log.error("", e);
            return null;
        }
    }

    /**
     * 把给定的日期时间类解析成指定格式的字符串。
     * <p/>
     * Formats a Date to produce a date/time string, which format
     * like:'yyyy-MM-dd HH:mm:ss.SSS'
     *
     * @param date   java.util.Date object, that need to be formatted.
     * @param format the format of the data/time string, such as: "yyyy-MM-dd
     *               HH:mm:ss.SSS"
     */
    public static String format(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 取得相对于当前时间指定间隔天数的时间。参数n为负数，则返回当前时间之前第n天的时间； n等于0， 则返回当前时间； n为正数，
     * 则返回当前时间之后的第n天的时间。
     *
     * @param n 相对当前时间的间隔天数
     */
    public static Date getOneDay(int n) {
        long milliseconds = System.currentTimeMillis() + (long) n * 24 * 60 * 60 * 1000;
        return new Date(milliseconds);
    }

    /**
     * 取得相对于指定时间的间隔天数的时间。参数n为负数，则返回当前时间之前第n天的时间； n等于0， 则返回当前时间； n为正数，
     * 则返回当前时间之后的第n天的时间。
     *
     * @param n 相对当前时间的间隔天数
     */
    public static Date getOneDay(Date raletiveDate, int n) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(raletiveDate);

        int day = calendar.get(Calendar.DAY_OF_MONTH) + n;
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return new Date(calendar.getTime().getTime());
    }


    /**
     * 将java.util.date类型的日期转为java.sql.date, 以适应实体类的日期类型
     *
     * @param date java.util.Date
     * @return java.sql.Date
     */
    public static java.sql.Date toSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 将java.util.date类型的时间转为java.sql.Time, 以适应实体类的时间类型
     *
     * @param date java.util.date
     * @return java.sql.date
     */
    public static java.sql.Time toSqlTime(Date date) {
        return new java.sql.Time(date.getTime());
    }

    /**
     * 将java.util.date类型的日期时间转为java.sql.Timestamp, 以适应实体类的日期时间类型
     *
     * @param date java.util.date
     * @return java.sql.date
     */
    public static java.sql.Timestamp toSqlTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * 取java.sql.Timestamp类型的当前时间
     *
     * @return java.sql.Timestamp类型的当前时间
     */
    public static java.sql.Timestamp currentTime() {
        return new java.sql.Timestamp(new Date().getTime());
    }


    public static void main(String[] args) {

        System.out.println(getOneDay(new Date(), 1).toString());
    }
}
