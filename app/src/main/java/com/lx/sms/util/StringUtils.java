package com.lx.sms.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {

    public static boolean isNotEmptyBatch(String... strs) {
        for (String str : strs) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param carNum
     * @return 合法：true 不合法：false
     * @description:验证车牌号
     */

    public static boolean validateCarNum(String carNum) {

        boolean result = false;

        String[] provence = new String[]{"京", "津", "冀", "晋", "辽", "吉", "黑", "沪", "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂", "琼", "渝",

                "川", "黔", "滇", "藏", "陕", "甘", "青", "宁", "新", "港", "澳", "蒙"};

        String reg = "[\u4e00-\u9fa5]{1}[A-Z]{1}[a-zA-Z_0-9]{5}";

        boolean firstChar = false;

        if (carNum.length() > 0) {

            firstChar = Arrays.asList(provence).contains(carNum.substring(0, 1));

        }

        try {

            Pattern p = Pattern.compile(reg);

            Matcher m = p.matcher(carNum);

            if (m.matches() && firstChar) {

                result = true;

            } else {

                result = false;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }


    /**
     * 验证国内身份证号码：15或18位，由数字组成，不能以0开头
     */
    public static boolean checkIdCard(String idNr) {
        String reg = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";

        return startCheck(reg, idNr);
    }

    public static boolean startCheck(String reg, String string) {
        boolean tem = false;

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(string);

        tem = matcher.matches();
        return tem;
    }

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static Pattern IMG_URL = Pattern
            .compile(".*?(gif|jpeg|png|jpg|bmp)");

    private final static Pattern URL = Pattern
            .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    private final static Pattern PHONENUM = Pattern.compile("1(3[0-9]|5[012356789]|7[0678]|8[0-9]|4[57])\\d{8}");

    private final static Pattern PASSWORD = Pattern.compile("((?=.*\\d)(?=.*[a-zA-Z]).{6,16})");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
//            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormat3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormat3.get());
    }

    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateString(Date date) {
        return dateFormat3.get().format(date);
    }

    public static String getDateString(String sdate) {
        return dateFormat3.get().format(toDate(sdate));
    }


    /**
     * 智能格式化
     */
    public static String friendly_time3(String sdate) {
        String res = "";
        if (isEmpty(sdate))
            return "";

        Date date = StringUtils.toDate(sdate);
        if (date == null)
            return sdate;

        SimpleDateFormat format = dateFormater2.get();

        if (isToday(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "上午 hh:mm" : "下午 hh:mm");
            res = format.format(date);
        } else if (isYesterday(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "昨天 上午 hh:mm" : "昨天 下午 hh:mm");
            res = format.format(date);
        } else if (isCurrentYear(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "MM-dd 上午 hh:mm" : "MM-dd 下午 hh:mm");
            res = format.format(date);
        } else {
            format.applyPattern(isMorning(date.getTime()) ? "yyyy-MM-dd 上午 hh:mm" : "yyyy-MM-dd 下午 hh:mm");
            res = format.format(date);
        }
        return res;
    }

    /**
     * @return 判断一个时间是不是上午
     */
    public static boolean isMorning(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int hour = time.hour;
        return (hour >= 0) && (hour < 12);
    }


    /**
     * @return 判断一个时间是不是今天
     */
    public static boolean isToday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    /**
     * @return 判断一个时间是不是昨天
     */
    public static boolean isYesterday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (time.monthDay - thenMonthDay == 1);
    }

    /**
     * @return 判断一个时间是不是明天
     */
    public static boolean isTomorrow(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay - time.monthDay == 1);
    }

    /**
     * @return 判断一个时间是不是今年
     */
    public static boolean isCurrentYear(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year);
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 是否是相同的一天
     *
     * @param sDate1 sDate1
     * @param sDate2 sDate2
     * @return
     */
    public static boolean isSameDay(String sDate1, String sDate2) {
        if (TextUtils.isEmpty(sDate1) || TextUtils.isEmpty(sDate2)) {
            return false;
        }
        boolean b = false;
        Date date1 = toDate(sDate1);
        Date date2 = toDate(sDate2);
        if (date1 != null && date2 != null) {
            String d1 = dateFormater2.get().format(date1);
            String d2 = dateFormater2.get().format(date2);
            if (d1.equals(d2)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime().getDay());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater.get().format(cal.getTime());
        return curDate;
    }

    /***
     * 计算两个时间差，返回的是的毫秒ms
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long calDateDifferent(String date1, String date2) {

        long diff = 0;

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = toDate(date1);
            d2 = toDate(date2);

            // 毫秒ms
            diff = d2.getTime() - d1.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diff;
    }

    /**
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        String time = null;
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes "
                + seconds + " seconds ";
//        return days > 0 ? days + "天" : "" + (hours > 0 ? hours + "小时" : "") + (minutes > 0 ? minutes + "分钟" : "");
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断手机号是不是一个合法的手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(String phoneNum) {
        if (phoneNum == null || phoneNum.trim().length() == 0) {
            return false;
        }
        return PHONENUM.matcher(phoneNum).matches();
    }

    /**
     * 判断密码是不是一个合法的密码
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        if (password == null || password.trim().length() == 0)
            return false;
        return PASSWORD.matcher(password).matches();
    }

    /**
     * 判断一个url是否为图片url
     *
     * @param url
     * @return
     */
    public static boolean isImgUrl(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return IMG_URL.matcher(url).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static int getCurrHour() {
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        return mHour;
    }


    public static String isAmOrPm() {
        return getCurrHour() < 12 ? "0" : "3";
    }


    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line + "<br>");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     */
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int leng = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > leng) {
            start = leng;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > leng) {
            end = leng;
        }
        return str.substring(start, end);
    }

    /**
     * 获取当前时间为每年第几周
     *
     * @return
     */
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /**
     * 获取当前时间为每年第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    public static int[] getCurrentDate() {
        int[] dateBundle = new int[3];
        String[] temp = getDataTime("yyyy-MM-dd").split("-");

        for (int i = 0; i < 3; i++) {
            try {
                dateBundle[i] = Integer.parseInt(temp[i]);
            } catch (Exception e) {
                dateBundle[i] = 0;
            }
        }
        return dateBundle;
    }

    /**
     * 返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }


    /**
     * 把数据转换成类似于1千+，2.1万+,1百万+
     *
     * @param num
     * @return
     */
    public static String longToStringAlert(long num) {
        String result = "";
        if (num < 10000) {
            result = String.valueOf(num);
        } else if (num < 1000000) {
            long a1 = num / 10000;
            if (num % 10000 == 0) {
                result = a1 + "万+";
            } else {
                long a2 = num % 10000;
                result = a1 + "." + String.valueOf(a2).substring(0, 1) + "万+";
            }
        } else {
            result = (num / 1000000) + "百万+";
        }

        return result;
    }

    /**
     * 从url中获得文件名
     *
     * @param url 文件路径
     * @return 文件名
     */
    public static String getFileName(String url) {
        if (url != null && url.length() > 0) {
            int index = url.lastIndexOf('/');
            if (index > 0) {
                return url.substring(index + 1);
            }
        }
        return "";
    }

}
