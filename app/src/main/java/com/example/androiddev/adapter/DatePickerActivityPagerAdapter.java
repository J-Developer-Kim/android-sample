package com.example.androiddev.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.androiddev.DatePickerFrag;
import com.example.androiddev.common.DatePickerListner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DatePickerActivityPagerAdapter extends FragmentStatePagerAdapter implements DatePickerListner {
    // ====================================================================================
    // Constant
    // ====================================================================================

    // ====================================================================================
    // Object
    // ====================================================================================
    protected HashMap<Integer, DatePickerFrag> o_fragmentMap;
    protected ArrayList<Long> arrL_listMonthByMillis = new ArrayList<>();
    protected int i_numOfMonth;
    protected int i_viewPagerWidth, i_viewPagerHeight;
    protected DatePickerListner o_listner;
    private Date o_start, o_end;
    private boolean b_isLunar = false;
    private boolean b_isAllDay = false;

    public DatePickerActivityPagerAdapter(FragmentManager fm, int pi_viewPagerWidth, int pi_viewPagerHeight, DatePickerListner po_listner) {
        super(fm);
        this.i_viewPagerWidth  = pi_viewPagerWidth;
        this.i_viewPagerHeight = pi_viewPagerHeight;
        this.o_listner         = po_listner;

        clearPrevFragments(fm);
        o_fragmentMap = new HashMap<Integer, DatePickerFrag>();
    }

    // ====================================================================================
    // Adapter LifeCycle
    // ====================================================================================
    @Override
    public Fragment getItem(int position) {
        DatePickerFrag frg = null;
        if ( o_fragmentMap.size() > 0 ) {
            frg = o_fragmentMap.get(position);
        }

        if ( frg == null ) {
            frg = DatePickerFrag.newInstance(i_viewPagerWidth, i_viewPagerHeight);
            frg.setO_listner(this);
            frg.setStartEndDate(o_start, o_end, b_isLunar, b_isAllDay);
            o_fragmentMap.put(position, frg);
        }
        else {
            frg.setStartEndDate(o_start, o_end, b_isLunar, b_isAllDay);
        }

        frg.setTimeByMillis(arrL_listMonthByMillis.get(position));
        return frg;
    }

    @Override
    public int getCount() { return arrL_listMonthByMillis.size(); }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    // ====================================================================================
    // Override
    // ====================================================================================
    /**
     * 달력 셀 Click 이벤트
     * @param timeMillis
     */
    @Override
    public void onDateClick(long timeMillis, int year, int month, int date) {
        o_listner.onDateClick(timeMillis, year, month, date);
    }

    // ====================================================================================
    // USER FUNCTION
    // ====================================================================================
    /**
     * 조회년도 기준으로 (앞,뒤)년도 12개월 씩 뽑아놓는다...
     * @param pi_numOfMonth
     */
    public void setNumOfMonth(int pi_numOfMonth, Calendar po_calendar) {
        this.i_numOfMonth = pi_numOfMonth;

        Calendar calendar = po_calendar;
        calendar.add(Calendar.MONTH, -pi_numOfMonth);
        calendar.set(Calendar.DATE, 1);

        for ( int i = 0; i < pi_numOfMonth * 2 + 1; i++ ) {
            arrL_listMonthByMillis.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
        }

        notifyDataSetChanged();
    }

    /**
     * Fragment 초기화
     * @param fm
     */
    private void clearPrevFragments(FragmentManager fm) {
        List<Fragment> listFragment = fm.getFragments();

        if ( listFragment != null ) {
            FragmentTransaction ft = fm.beginTransaction();

            for ( Fragment f : listFragment ) {
                if (f instanceof DatePickerFrag) {
                    ft.remove(f);
                }
            }

            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 다음달 추가
     */
    public void addNext() {
        long lastMonthMillis = arrL_listMonthByMillis.get(arrL_listMonthByMillis.size() - 1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastMonthMillis);
        for ( int i = 0; i < i_numOfMonth; i++ ) {
            calendar.add(Calendar.MONTH, 1);
            arrL_listMonthByMillis.add(calendar.getTimeInMillis());
        }

        notifyDataSetChanged();
    }

    /**
     * 이전달 추가
     */
    public void addPrev() {
        long lastMonthMillis = arrL_listMonthByMillis.get(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastMonthMillis);
        calendar.set(Calendar.DATE, 1);
        for ( int i = i_numOfMonth; i > 0; i-- ) {
            calendar.add(Calendar.MONTH, -1);
            arrL_listMonthByMillis.add(0, calendar.getTimeInMillis());
        }

        notifyDataSetChanged();
    }

    /**
     * 년,월 구하기
     * @param ps_format
     * @param pi_position
     * @return
     */
    public String getMonthDisplayed(String ps_format, int pi_position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(arrL_listMonthByMillis.get(pi_position));

        SimpleDateFormat sdf = new SimpleDateFormat(ps_format);
        Date date = new Date();
        date.setTime(arrL_listMonthByMillis.get(pi_position));

        return sdf.format(date);
    }

    /**
     * 월 구하기
     * @param pi_position
     * @return
     */
    public String getMonth(int pi_position) {
        return getMonthDisplayed("M월", pi_position);
    }

    /**
     * 월 구하기
     * @param pi_position
     * @return
     */
    public int getMonthNumber(int pi_position) {
        return Integer.parseInt(getMonthDisplayed("M", pi_position));
    }

    /**
     * 년도 구하기
     * @param pi_position
     * @return
     */
    public String getYear(int pi_position) {
        return getMonthDisplayed("YYYY년", pi_position);
    }

    /**
     * 년도 구하기
     * @param pi_position
     * @return
     */
    public int getYearNumber(int pi_position) {
        return Integer.parseInt(getMonthDisplayed("YYYY", pi_position));
    }

    /**
     * 시작날짜, 종료날짜 SETTER
     * @param o_start
     * @param o_end
     */
    public void setStartEndDate(Date o_start, Date o_end, boolean b_isLunar, boolean b_isAllDay) {
        this.o_start    = o_start;
        this.o_end      = o_end;
        this.b_isLunar  = b_isLunar;
        this.b_isAllDay = b_isAllDay;
    }

    /*
    private HashMap<Integer, DatePickerFrag>  o_frgMap;
    private ArrayList<Long>                   l_monthMillis = new ArrayList<>();
    private int                               i_viewPagerWidth;
    private int                               i_viewPagerHeight;
    protected DatePickerListner               o_listner;
    private Date                              o_start;
    private Date                              o_end;
    private int                               i_numOfMonth;

    public DatePickerActivityPagerAdapter(FragmentManager fm, int pi_viewPagerWidth, int pi_viewPagerHeight, DatePickerListner o_listner) {
        super(fm);
        this.i_viewPagerWidth  = pi_viewPagerWidth;
        this.i_viewPagerHeight = pi_viewPagerHeight;
        this.o_listner         = o_listner;

        clearPrevFragments(fm);
        o_frgMap = new HashMap<Integer, DatePickerFrag>();
    }

    private void clearPrevFragments(FragmentManager fm) {
        List<Fragment> listFragment = fm.getFragments();

        if ( listFragment != null ) {
            FragmentTransaction ft = fm.beginTransaction();

            for ( Fragment f : listFragment ) {
                if ( f instanceof DatePickerFrag ) {
                    ft.remove(f);
                }
            }

            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public Fragment getItem(int position) {
        DatePickerFrag frg = null;

        if ( o_frgMap.size() > 0 ) {
            frg = o_frgMap.get(position);
        }

        if ( frg == null ) {
            frg = DatePickerFrag.newInstance(i_viewPagerWidth, i_viewPagerHeight);
            //frg.setStartEndDate(o_start, o_end);
            //frg.setO_listner(this);
            o_frgMap.put(position, frg);
        }
        else {
            frg.setStartEndDate(o_start, o_end);
        }

        frg.setTimeByMillis(l_monthMillis.get(position));
        return frg;
    }

    @Override
    public int getCount() {
        return l_monthMillis.size();
    }

    public void setNumOfMonth(int i_numOfMonth) {
        this.i_numOfMonth = i_numOfMonth;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -i_numOfMonth);
        calendar.set(Calendar.DATE, 1);

        for ( int i = 0; i < i_numOfMonth * 2 + 1; i++ ) {
            l_monthMillis.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
        }

        notifyDataSetChanged();
    }

    public void addNext() {
        long lastMonthMillis = l_monthMillis.get(l_monthMillis.size() - 1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastMonthMillis);
        for ( int i = 0; i < i_numOfMonth; i++ ) {
            calendar.add(Calendar.MONTH, 1);
            l_monthMillis.add(calendar.getTimeInMillis());
        }

        notifyDataSetChanged();
    }

    public void addPrev() {
        long lastMonthMillis = l_monthMillis.get(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastMonthMillis);
        calendar.set(Calendar.DATE, 1);
        for ( int i = i_numOfMonth; i > 0; i-- ) {
            calendar.add(Calendar.MONTH, -1);
            l_monthMillis.add(0, calendar.getTimeInMillis());
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public String getMonth(int pi_position) {
        return getMonthDisplayed("M월", pi_position);
    }

    public String getYear(int pi_position) {
        return getMonthDisplayed("YYYY년", pi_position);
    }

    public String getMonthDisplayed(String ps_format, int pi_position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l_monthMillis.get(pi_position));

        SimpleDateFormat sdf = new SimpleDateFormat(ps_format);
        Date date = new Date();
        date.setTime(l_monthMillis.get(pi_position));

        return sdf.format(date);
    }

    public void setStartEndDate(Date o_start, Date o_end) {
        this.o_start = o_start;
        this.o_end   = o_end;
    }

    @Override
    public void onDateClick(long timeMillis) {
        o_listner.onDateClick(timeMillis);
    }
    */
}
