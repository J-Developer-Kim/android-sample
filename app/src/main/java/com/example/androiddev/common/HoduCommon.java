package com.example.androiddev.common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HoduCommon {
    private static final int CI_CALENDAR_MIN_YEAR = 1000; // 0000 ~ MAX_INTEGER 까지

    /**
     * 연월로 달력 페이지 포지션 계산하기
     * @param lo_firstDayCal
     * @return
     */
    public static int getPositionFromYearMonth(Calendar lo_firstDayCal) {
        int year = lo_firstDayCal.get(Calendar.YEAR);
        if (year < CI_CALENDAR_MIN_YEAR)
            return -1;
        int month = lo_firstDayCal.get(Calendar.MONTH);
        int position = (year - CI_CALENDAR_MIN_YEAR) * 12 + month;
        return position;
    }

    /**
     * position 으로 달력 가져오기
     * @param position
     * @return
     */
    public static Calendar getCalObjFromPosition(int position) {
        if (position < 0)
            return null;
        int year = CI_CALENDAR_MIN_YEAR + position / 12;
        int month = position % 12;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        return cal;
    }

    /**
     * 기준날짜에서 시작 요일 (일요일) 로 바꾼다.
     * Calendar.set(Calendar.DAY_OF_WEEK, n); 하는 것은 오라클에서 권장사항이 아니다.
     * @param po_cal
     * @return
     */
    public static Calendar GF_GET_FIRST_DAY_OF_WEEK(Calendar po_cal){
        return GF_ADD_DAY_OF_CALENDAR(po_cal, (((po_cal.get(Calendar.DAY_OF_WEEK)) - 1) * -1));
    }

    /**
     * 기준날짜에서 마지막 요일 (토요일) 로 바꾼다.
     * Calendar.set(Calendar.DAY_OF_WEEK, n); 하는 것은 오라클에서 권장사항이 아니다.
     * @param po_cal
     * @return
     */
    public static Calendar GF_GET_LAST_DAY_OF_WEEK(Calendar po_cal) {
        return GF_ADD_DAY_OF_CALENDAR(po_cal, (7 - po_cal.get(Calendar.DAY_OF_WEEK)));
    }

    /**
     * 기준날짜에서 이번달 최대일자로 바꾼다.
     * Calendar.set(Calendar.DAY_OF_WEEK, n); 하는 것은 오라클에서 권장사항이 아니다.
     * @param po_cal
     * @return
     */
    public static Calendar GF_GET_MAX_DAY_OF_MONTH(Calendar po_cal){
        Calendar lo_cal = (Calendar) po_cal.clone();
        lo_cal.set(lo_cal.get(Calendar.YEAR), lo_cal.get(Calendar.MONTH), lo_cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return lo_cal;
    }

    /**
     * 시간 계산
     * @param ps_dateTime - 시간
     * @param pi_term - 간격
     * @param pc_gb - 구분자
     * @return
     */
    public static  String GF_GET_CALC_TIME(String ps_dateTime, int pi_term, char pc_gb) {
        SimpleDateFormat lo_sdf = new SimpleDateFormat("HHmm");
        try{
            Date lo_date = lo_sdf.parse(ps_dateTime);
            Calendar lo_cal = Calendar.getInstance();
            lo_cal.setTime(lo_date);

            switch (pc_gb) {
                case 'H':
                    lo_cal.add(Calendar.HOUR_OF_DAY, pi_term);
                    break;
                case 'm':
                    lo_cal.add(Calendar.MINUTE, pi_term);
                    break;
            }

            return String.format("%02d%02d",
                    lo_cal.get(Calendar.HOUR_OF_DAY), lo_cal.get(Calendar.MINUTE));
        }
        catch (Exception e){
            return ps_dateTime;
        }
    }

    /**
     * 캘린더 객체에 pi_days 가감하기
     * @param po_cal
     * @param pi_days
     * @return
     */
    public static Calendar GF_ADD_DAY_OF_CALENDAR(Calendar po_cal, int pi_days) {
        Calendar lo_cal = (Calendar) po_cal.clone();
        lo_cal.add(Calendar.DATE, pi_days);
        return lo_cal;
    }

    /**
     * 문자열을 Date 객체로
     * @param ps_date
     * @return
     */
    public static Date GF_STRING_TO_DATE(String ps_date) {
        return GF_STRING_TO_CALENDAR(ps_date).getTime();
    }

    /**
     * 문자열을 캘린더 객체로
     * @param ps_date
     * @return
     */
    public static Calendar GF_STRING_TO_CALENDAR(String ps_date) {

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(ps_date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(ps_date.substring(4, 6)) -1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ps_date.substring(6, 8)));

        if (ps_date.length() > 8) {
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ps_date.substring(8, 10)));
            cal.set(Calendar.MINUTE, Integer.parseInt(ps_date.substring(10, 12)));

            if (ps_date.length() > 12) {
                cal.set(Calendar.SECOND, Integer.parseInt(ps_date.substring(12, 14)));

                if (ps_date.length() == 16) {
                    cal.set(Calendar.MILLISECOND, Integer.parseInt(ps_date.substring(14, 16)));
                } else {
                    cal.set(Calendar.MILLISECOND, 0);
                }
            } else {
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal;
    }

    /**
     * 오늘 일자 구하기
     * @return
     */
    public static String GF_GET_TODAY() {

        return GF_GET_TODAY(8, "");
    }

    /**
     * 오늘 일자 구하기
     * @param pi_len
     * @return
     */
    public static String GF_GET_TODAY(int pi_len) {

        return GF_GET_TODAY(pi_len, "");
    }

    /**
     * 오늘 일자 구하기
     * @param pi_len
     * @param ps_delimiter
     * @return
     */
    public static String GF_GET_TODAY(int pi_len, String ps_delimiter) {

        Calendar lo_today = Calendar.getInstance(); // 날짜 불러오는 함수

        if(pi_len == 6){
            return GF_CALENDAR_TO_STRING(lo_today, ps_delimiter).substring(0, 6 + (ps_delimiter.length()));
        }
        else{
            return GF_CALENDAR_TO_STRING(lo_today, ps_delimiter);
        }
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_date - Date - Calendar Object
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Date po_date) {

        Calendar lo_cal = Calendar.getInstance();
        lo_cal.setTime(po_date);

        return GF_CALENDAR_TO_STRING(lo_cal);
    }
    /**
     * 캘린더 객체를 문자열로
     * @param po_date - Date - Calendar Object
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Date po_date, boolean pb_showTime) {

        Calendar lo_cal = Calendar.getInstance();
        lo_cal.setTime(po_date);

        return GF_CALENDAR_TO_STRING(lo_cal, pb_showTime);
    }

    /**
     * 캘린더 객체를 문자열로
     * @param pl_mils - Long - Calendar Object
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Long pl_mils) {

        Calendar lo_cal = Calendar.getInstance();
        lo_cal.setTimeInMillis(pl_mils);

        return GF_CALENDAR_TO_STRING(lo_cal);
    }

    /**
     * 캘린더 객체를 문자열로
     * @param pl_mils - Long - Calendar Object
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Long pl_mils, String ps_delimeter) {

        Calendar lo_cal = Calendar.getInstance();
        lo_cal.setTimeInMillis(pl_mils);

        return GF_CALENDAR_TO_STRING(lo_cal, ps_delimeter);
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_cal - Calendar Object
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Calendar po_cal) {

        return GF_CALENDAR_TO_STRING(po_cal, "");
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_cal - Calendar Object
     * @param pb_showTime - 시간 보여주기
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Calendar po_cal, boolean pb_showTime) {

        return GF_CALENDAR_TO_STRING(po_cal, "", pb_showTime);
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_cal - Calendar Object
     * @param pi_date - 더할 날짜
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Calendar po_cal, int pi_date) {

        po_cal.add(Calendar.DATE, pi_date);
        return GF_CALENDAR_TO_STRING(po_cal, "");
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_cal - Calendar Object
     * @param ps_delimeter - 구분 문자
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Calendar po_cal, String ps_delimeter) {

        return GF_CALENDAR_TO_STRING(po_cal, ps_delimeter, false);
    }

    /**
     * 캘린더 객체를 문자열로
     * @param po_cal - Calendar Object
     * @param ps_delimeter - 구분 문자
     * @param pb_showTime - 시간 보여주기
     * @return
     */
    public static String GF_CALENDAR_TO_STRING(Calendar po_cal, String ps_delimeter, boolean pb_showTime) {

        StringBuilder sb = new StringBuilder();

        sb.append("yyyy");

        if (null != ps_delimeter) {
            sb.append(ps_delimeter);
        }
        sb.append("MM");

        if (null != ps_delimeter) {
            sb.append(ps_delimeter);
        }
        sb.append("dd");

        if (pb_showTime) {
            sb.append(" HH:mm");
        }

        Date lo_date = new Date();
        lo_date.setTime(po_cal.getTimeInMillis());

        SimpleDateFormat lo_sdf = new SimpleDateFormat(sb.toString());

        return lo_sdf.format(lo_date);
    }

    /**
     * 데이트 객체를 문자열로
     * @param po_date
     * @return
     */
    public static String GF_DATE_TO_STRING(Date po_date){
        return GF_DATE_TO_STRING(po_date, false);
    }

    /**
     * 데이트 객체를 문자열로
     * @param po_date
     * @param pb_showTime - 시간 보여주기
     * @return
     */
    public static String GF_DATE_TO_STRING(Date po_date, boolean pb_showTime){

        Calendar cal = Calendar.getInstance();

        cal.setTime(po_date);

        return GF_CALENDAR_TO_STRING(cal, pb_showTime);
    }

    /**
     * 데이트 객체를 문자열로
     * @param po_cal
     * @param ps_format - simpleformat
     * @return
     */
    public static String GF_DATE_TO_STRING(Calendar po_cal, String ps_format){

        SimpleDateFormat lo_sdf = new SimpleDateFormat(ps_format);

        return lo_sdf.format(new Date(po_cal.getTimeInMillis()));
    }

    /**
     * 데이트 객체를 문자열로
     * @param po_date
     * @param ps_delimeter - 포멧
     * @param pb_showTime - 시간 보여주기
     * @return
     */
    public static String GF_DATE_TO_STRING(Date po_date, String ps_delimeter, boolean pb_showTime){

        Calendar cal = Calendar.getInstance();

        cal.setTime(po_date);

        return GF_CALENDAR_TO_STRING(cal, ps_delimeter, pb_showTime);
    }

    public static String GF_DATE_TO_STRING(Date po_date, String ps_format) {
        Date lo_currentDate = po_date;

        SimpleDateFormat lo_sdf = new SimpleDateFormat(ps_format);

        return lo_sdf.format(po_date);
    }

    /**
     * Date TO Calendar
     * @param po_date
     * @return
     */
    public static Calendar GF_DATE_TO_CALENDAR(Date po_date) {
        Calendar lo_calendar = Calendar.getInstance();
        lo_calendar.setTime(po_date);
        return lo_calendar;
    }

    /**
     * D-Day 계산
     * @return
     */
    public static long GF_CALCURATE_DDAY(Date po_fromDate, Date po_toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //한국기준 날짜

        long difference = 0L;

        try {
            difference = (sdf.parse(sdf.format(po_fromDate)).getTime() - sdf.parse(sdf.format(po_toDate)).getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return difference/ (24*60*60*1000);
    }
}
