package com.example.androiddev.common;

import java.util.Calendar;

public class HoduCalendarHelper {
    private static final int CI_CALENDAR_MIN_YEAR = 1000; // 0000 ~ MAX_INTEGER 까지

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
}
