package com.example.androiddev.common;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DateCalendarDataSet {
    private Context  context;
    public  Calendar _calendar;
    public  int      position                = 0;
    public  int      week_count              = 0;  // 월에 해당하는 주수
    public  int      max_cell_idx            = 0;  // 최대 엑세스 가능 셀 idx ( 예 : 4주 * 7일)
    public  int      first_day_idx           = 0;  // 1일 인덱스
    public  int      last_day_idx            = 0;  // 마지막날 인덱스
    public  int      day_of_month            = 0;  // 해당월 마지막날짜
    public  int      day_of_month_first_cell = 0;  // 첫 셀의 날자
    public  int      day_of_month_last_cell  = 0;  // 마지막 셀의 날자
    public  int      _year                   = 0;
    public  int      _month                  = 0;   // -1 되어있음 month = calendar.get(Calendar.MONTH);
    public Date      _start                  = new java.util.Date();;
    public Date      _end                    = new java.util.Date();
    public long      _startTime;
    public long      _endTime;

    public final int MAX_MONTH_DAYS       = 42; // 6주 * 7일
    public boolean[] cell_info_this_month = new boolean[MAX_MONTH_DAYS];    // 날짜 셀의 현재 달 여부 ( 이전 ,이후 달 이면 그레이 컬러 적용하기 위해)
    public int[]     cell_info_day        = new int[MAX_MONTH_DAYS];        // 날짜 셀의 일짜

    private final int[] DAY_LIST_ARRAY = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};

    public DateCalendarDataSet() { }

    // 1. 달력 기초 정보 세팅
    public void changeData(Context context, int position) {
        this.context = context;
        this.position = position;

        setupMonthGridInfo(position);
    }

    // 달력 정보 세팅
    private void setupMonthGridInfo(int position){
        _calendar = (Calendar) HoduCalendarHelper.getCalObjFromPosition(position).clone();

        // 이번달의 1일 기준에서 첫째 요일로 맞춤 (시작요일 셋팅)
        _start.setTime((HoduCommon.GF_GET_FIRST_DAY_OF_WEEK(_calendar)).getTimeInMillis());

        // 이번달의 마지막 날에서 마지막 요일로 맞춤 (종료요일 셋팅)
        _end.setTime(HoduCommon.GF_GET_LAST_DAY_OF_WEEK(HoduCommon.GF_GET_MAX_DAY_OF_MONTH(_calendar)).getTimeInMillis()) ;
        _startTime = _start.getTime() / 1000 * 1000;
        _endTime = _end.getTime() / 1000 * 1000;

        _year = _calendar.get(Calendar.YEAR);
        _month = _calendar.get(Calendar.MONTH);

        week_count = _calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);  // 총 주수

        max_cell_idx = week_count*7;

        first_day_idx = _calendar.get(Calendar.DAY_OF_WEEK) - 1;  // 월 1일 요일
        day_of_month = _calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 월 마지막 날

        Calendar cal = Calendar.getInstance();
        cal.setTime(_start);
        int firstDay = day_of_month_first_cell = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(_end);
        //day_of_month_last_cell = cal.get(Calendar.DAY_OF_MONTH);

        // 해당월 이전
        for ( int idx = 0; idx < first_day_idx; idx++ ) {
            cell_info_day[idx] = firstDay++;
        }

        System.arraycopy(DAY_LIST_ARRAY,0,cell_info_day,first_day_idx,day_of_month);  // 1~31 까지 넣기

        int tail = 7 * week_count - (first_day_idx+day_of_month);

        if ( tail > 0 ) System.arraycopy(DAY_LIST_ARRAY,0,cell_info_day,first_day_idx+day_of_month,tail );  // 1~31 까지 넣기

        Arrays.fill(cell_info_this_month, false);
        Arrays.fill(cell_info_this_month, first_day_idx,first_day_idx+day_of_month,true);   // 해당월은 true
    }
}
