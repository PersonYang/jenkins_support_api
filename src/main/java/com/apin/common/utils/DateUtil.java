package com.apin.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @version 1.0, 2013/04/16
 */
public class DateUtil {

    private static final String[] DATE_FORMATS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd"};

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd
     */
    public static final int DEFAULT = 0;
    public static final int YM = 1;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd
     */
    public static final int YMR_SLASH = 11;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMdd
     */
    public static final int NO_SLASH = 2;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMM
     */
    public static final int YM_NO_SLASH = 3;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm:ss
     */
    public static final int DATE_TIME = 4;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMddHHmmss
     */
    public static final int DATE_TIME_NO_SLASH = 5;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm
     */
    public static final int DATE_HM = 6;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm:ss
     */
    public static final int TIME = 7;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm
     */
    public static final int HM = 8;

    /**
     * 变量：日期格式化类型 - 格式:HHmmss
     */
    public static final int LONG_TIME = 9;
    /**
     * 变量：日期格式化类型 - 格式:HHmm
     */

    public static final int SHORT_TIME = 10;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH:mm:ss
     */
    public static final int DATE_TIME_LINE = 12;

    public static String dateToStr(Date date, int type) {
        switch (type) {
            case DEFAULT:
                return dateToStr(date);
            case YM:
                return dateToStr(date, "yyyy/MM");
            case NO_SLASH:
                return dateToStr(date, "yyyyMMdd");
            case YMR_SLASH:
                return dateToStr(date, "yyyy-MM-dd");
            case YM_NO_SLASH:
                return dateToStr(date, "yyyyMM");
            case DATE_TIME:
                return dateToStr(date, "yyyy/MM/dd HH:mm:ss");
            case DATE_TIME_NO_SLASH:
                return dateToStr(date, "yyyyMMddHHmmss");
            case DATE_HM:
                return dateToStr(date, "yyyy/MM/dd HH:mm");
            case TIME:
                return dateToStr(date, "HH:mm:ss");
            case HM:
                return dateToStr(date, "HH:mm");
            case LONG_TIME:
                return dateToStr(date, "HHmmss");
            case SHORT_TIME:
                return dateToStr(date, "HHmm");
            case DATE_TIME_LINE:
                return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
            default:
                throw new IllegalArgumentException("Type undefined : " + type);
        }
    }

    public static String dateToStr(Date date, String pattern) {
        if (date == null || date.equals(""))
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, "yyyy/MM/dd");
    }

    /**
     * 在不确定日期格式的情况下，依次解析，如成功就返回
     *
     * @param date 日期字符�?
     * @return
     */
    public static Date toDate(String date) {

        SimpleDateFormat dateFormat = null;

        for (String format : DATE_FORMATS) {
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat(format);
            } else {
                dateFormat.applyPattern(format);
            }

            try {
                return dateFormat.parse(date);
            } catch (Exception ex) {
                continue;
            }
        }

        return null;
    }

    /**
     * 格式化日�?
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static boolean compareDate(String start, String end) throws ParseException {

        Date date = new Date();
        int pos = end.indexOf("+");
        Date endDate = date;
        if (pos != -1) {
            Calendar cal = Calendar.getInstance();
            String time = end.substring(0, pos).trim();
            int days = Integer.valueOf(end.substring(pos + 1).trim());
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            end = time;
            endDate = cal.getTime();
        }

        String now = formatDate(date, DATE_FORMATS[0]);
        String beginTime = formatDate(date, DATE_FORMATS[1]) + " " + start;
        String endTime = formatDate(endDate, DATE_FORMATS[1]) + " " + end;
        try {
            Date d1 = df.parse(now);
            Date d2 = df.parse(endTime);
            Date d3 = df.parse(beginTime);
            return ((d2.getTime() - d1.getTime()) > 0l) && ((d1.getTime() - d3.getTime()) > 0l);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    public static boolean compareDate(String start, String end, String nowdate) throws ParseException {

        Date date = new Date(nowdate);
        int pos = end.indexOf("+");
        Date endDate = date;
        if (pos != -1) {
            Calendar cal = Calendar.getInstance();
            String time = end.substring(0, pos).trim();
            int days = Integer.valueOf(end.substring(pos + 1).trim());
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            end = time;
            endDate = cal.getTime();
        }

        String now = formatDate(date, DATE_FORMATS[0]);
        String beginTime = formatDate(date, DATE_FORMATS[1]) + " " + start;
        String endTime = formatDate(endDate, DATE_FORMATS[1]) + " " + end;
        try {
            Date d1 = df.parse(now);
            Date d2 = df.parse(endTime);
            Date d3 = df.parse(beginTime);
            return ((d2.getTime() - d1.getTime()) > 0l) && ((d1.getTime() - d3.getTime()) > 0l);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }


    /**
     * 得到前几天的日期
     *
     * @param num 前几天
     * @return
     */
    public static String preDate(String num) {
        int n = Integer.valueOf(num);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -n);
        return formatDate(calendar.getTime(), FORMAT_YYYY_MM_DD);
    }

    
    public static Date toFullDate(String date) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");;
    			try {
					return dateFormat.parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
    }
    public static Date toFormatDate(String date,String type) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(type);;
    	try {
    		return dateFormat.parse(date);
    	} catch (ParseException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return null;
    }

    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() <= dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 
     * 
     * @param date传入的时间
     * @param num 天数
     * @param type true 增加 false 减少
     * @return
     */
    public static Date changeDay(Date date,int num,boolean type){
    	if(date ==null ){
    		return new Date();
    	}
    	if(type){
    		return new Date(date.getTime() + num * 24 * 60 * 60 * 1000);
    	}else{
    		return  new Date(date.getTime() - num * 24 * 60 * 60 * 1000);
    	}
    }
    
    
    
    
    
    
    /**
     * 
     * 
     * 返回两个时间段间隔的小时 分钟map
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @param format 时间格式 例如: yyyy-MM-dd, HH:mm等
     * @param str  返回 类型  例:h,m,hm
     * @return  例如:12h23m
     */
    public static String redurnStayTime(String startTime, String endTime,  String format, String str){
    	Map<String, Long> resultMap =dateDiff(startTime, endTime, format, str);
    	StringBuffer sb = new StringBuffer();
    	if(resultMap.containsKey("h")){
    		if(resultMap.get("h")<0){
    			if(resultMap.containsKey("m")){
    				sb.append(24+resultMap.get("h")).append("h");
    			}else{
    				sb.append(24+resultMap.get("h")).append("h");
    			}
    		}else{
    			sb.append(resultMap.get("h")).append("h");
    		}
    	}if(resultMap.containsKey("m")){
    		if(resultMap.get("m") != 0){
    			if(resultMap.get("m")<0){
        			sb.append(-resultMap.get("m")).append("m");
        		}else{
        			sb.append(resultMap.get("m")).append("m");
        		}
    		}
    	}
		return sb.toString();
    }
    
    public static int retrurnBetween(String startTime, String endTime,  String format, String str){
    	Map<String, Long> resultMap =dateDiff(startTime, endTime, format, str);
    	StringBuffer sb = new StringBuffer();
    	int hour=0;
    	if(resultMap.containsKey("h")){
    		if(resultMap.get("h")<0){
    			if(resultMap.containsKey("m")){
    				sb.append(24+resultMap.get("h")).append("h");
    				hour=(int) (24+resultMap.get("h"));
    			}else{
    				sb.append(24+resultMap.get("h")).append("h");
    			}
    		}else{
    			hour=(int)(resultMap.get("h")+0);
    		}
    	}
		return hour;
    }
    
    /**
     * 返回两个时间段间隔的小时 分钟map
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @param format 时间格式 例如: yyyy-MM-dd, HH:mm等
     * @param str  返回 类型  例:h,m,hm
     * @return
     */
    public static Map<String, Long> dateDiff(String startTime, String endTime,  String format, String str) {   
        // 按照传入的格式生成一个simpledateformate对象    
        SimpleDateFormat sd = new SimpleDateFormat(format);    
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数    
        long nh = 1000 * 60 * 60;// 一小时的毫秒数    
        long nm = 1000 * 60;// 一分钟的毫秒数    
        long ns = 1000;// 一秒钟的毫秒数    
        long diff;    
        long day = 0;    
        long hour = 0;    
        long min = 0;    
        long sec = 0;    
        Map<String, Long> resultMap = new HashMap<>();
        // 获得两个时间的毫秒时间差异    
        try { 
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();    
            day = diff / nd;// 计算差多少天    
            hour = diff % nd / nh + day * 24;// 计算差多少小时    
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟    
            sec = diff % nd % nh % nm / ns;// 计算差多少秒    
            // 输出结果    
            System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"   
                    + (min - day * 24 * 60) + "分钟" + sec + "秒。");    
            System.out.println("hour=" + hour + ",min=" + min);    
            if (str.equalsIgnoreCase("h")) {    
            	resultMap.put("h", hour);
            } else if("hm".endsWith(str)){
            	if(min == 0){
            		resultMap.put("h", hour);
            	}
            	resultMap.put("h", hour);
            	resultMap.put("m", min);
            }else {    
            	resultMap.put("m", min); 
            }    
        } catch (Exception e) {    
            e.printStackTrace();    
        }
		return resultMap;    
    } 
    
    
    /**
     * 
     * @param sourceDateTime 原时间
     * @param departTimeZoneId 原时区 时区ID
     * @param destTimeZoneId  目的时区 时区ID
     * @return Date 原时间的目的时区时间
     */
    public static Date string2TimeZone(String sourceDateTime,String departTimeZoneId,String destTimeZoneId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		int diffTime = getDiffTimeZoneRawOffset(departTimeZoneId, destTimeZoneId)-1*1000*60*60;
		try {
			Date d = sdf.parse(sourceDateTime);
			long sourceTime = d.getTime();
			long destLongTime = sourceTime - diffTime;
			return new Date(destLongTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getDiffTimeZoneRawOffset(String departTimeZoneId, String destTimeZoneId) {
		return TimeZone.getTimeZone(departTimeZoneId).getRawOffset()-TimeZone.getTimeZone(destTimeZoneId).getRawOffset();
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
