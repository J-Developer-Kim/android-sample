package com.example.androiddev;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androiddev.adapter.DatePickerActivityPagerAdapter;
import com.example.androiddev.common.CommonEnum;
import com.example.androiddev.common.Constant;
import com.example.androiddev.common.DatePickerListner;
import com.example.androiddev.common.HoduCommon;
import com.zyyoona7.wheel.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.ButterKnife;

public class DatePickerActivity extends AppCompatActivity implements DatePickerListner {
    // ====================================================================================
    // Constant
    // ====================================================================================
    private static final int    COUNT_PAGE          = 12;
    private static final String INVITE_DATE_FORMAT  = "yyyy.MM.dd EEEE";
    private static final String INVITE_TIME_FORMAT  = "a hh:mm";
    private static final String CALENDAR_START_DATE = "START";
    private static final String CALENDAR_END_DATE   = "END";

    // ====================================================================================
    // Object
    // ====================================================================================
    private int                            i_viewPagerHeight;                                               // Viewpager Width
    private int                            i_viewPagerWidth;                                                // Viewpager Height
    private DatePickerActivityPagerAdapter o_adapter;                                                       // Viewpager Adapter
    private Date                           o_start;                                                         // 시작날짜
    private Date                           o_end;                                                           // 종료날짜
    private Calendar                       o_displayCalendar;                                               // Viewpager 캘린더에 보여지고있는 년월
    private String                         s_calendar = CALENDAR_START_DATE;                                // 시작날짜 : START , 종료날짜 : END (Focus 가있는게 뭐인지)
    private ArrayList<String>              arrS_ampm = new ArrayList<>();                                   // AM, PM
    private String[]                       arrS_hh = {"1","2","3","4","5","6","7","8","9","10","11","12"};  // 휠뷰 시간
    private String[]                       arrS_mi = {"00","10","20","30","40","50"};                       // 휠뷰 분
    private ArrayList<String>              arrS_yy = new ArrayList<>();                                     // 휠뷰 년도
    private String[]                       arrS_mm = {"1","2","3","4","5","6","7","8","9","10","11","12"};  // 휠뷰 월
    public  boolean                        b_wheelViewListner = false;                                      // 휠뷰 리스너 반영여부(시간)
    public  boolean                        b_yearMonthWheelViewListner = false;                             // 휠뷰 리스너 반영여부(년,월)
    private boolean                        b_isLunar = false;                                               // 음력 선택여부
    private boolean                        b_isAllDay = false;                                              // 종일 선택여부
    private boolean                        b_isCalYearMonthSelected = false;                                // 달력 년도,월 변경모드 일때 true
    private boolean                        b_isViewPagerListner = false;

    // ====================================================================================
    // BindView
    // ====================================================================================
    @BindView(R.id.cl_backGround)          ConstraintLayout cl_backGround;
    @BindView(R.id.cl_content)             ConstraintLayout cl_content;
    @BindView(R.id.cl_empty)               ConstraintLayout cl_empty;
    // 캘린더 백그라운드
    @BindView(R.id.cl_calendar)            ConstraintLayout cl_calendar;
    // 년도
    @BindView(R.id.btn_yyyy)               Button           btn_yyyy;
    // 월
    @BindView(R.id.btn_month)              Button           btn_month;
    // 오늘
    @BindView(R.id.btn_today)              Button           btn_today;
    // 시작일자 시간
    @BindView(R.id.tv_startYmd)            TextView         tv_startYmd;
    @BindView(R.id.tv_startTime)           TextView         tv_startTime;
    // 종료일자 시간
    @BindView(R.id.tv_endYmd)              TextView         tv_endYmd;
    @BindView(R.id.tv_endTime)             TextView         tv_endTime;
    // 종일
    @BindView(R.id.tv_allDay)              TextView         tv_allDay;
    @BindView(R.id.view_lunarAlldayCenter) View             view_lunarAlldayCenter;
    // 음력
    @BindView(R.id.tv_lunar)               TextView         tv_lunar;
    // 캘린더 요일,달력
    @BindView(R.id.vp_calendar)            ViewPager        vp_calendar;
    // 휠뷰
    @BindView(R.id.ll_bottomWheelView)     LinearLayout     ll_bottomWheelView;
    @BindView(R.id.wheel_ampm)             WheelView        wheel_ampm;
    @BindView(R.id.wheel_hour)             WheelView        wheel_hour;
    @BindView(R.id.wheel_minute)           WheelView        wheel_minute;
    // 년월 선택하는 휠뷰 백그라운드
    @BindView(R.id.cl_yearMonthWheelView)  ConstraintLayout cl_yearMonthWheelView;
    @BindView(R.id.tv_bottomClose)         TextView         tv_bottomClose;
    @BindView(R.id.tv_bottomSucc)          TextView         tv_bottomSucc;
    @BindView(R.id.wheel_yyyy)             WheelView        wheel_yyyy;
    @BindView(R.id.wheel_month)            WheelView        wheel_month;

    // ====================================================================================
    // Activity LifeCycle
    // ====================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);

        ButterKnife.bind(this);

        // 호출하는 화면에서 무조건 start,end 는 파라미터로 넘겨줘야함..
        // 현재는 샘플로 Date 세팅
        // 시작 : 현재시간 , 종료 : 현재시간 기준 + 1시간
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        o_displayCalendar = calendar;

        o_start = new Date(calendar.getTimeInMillis());

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

        o_end = new Date(calendar.getTimeInMillis());

        // 캘린더 보일지 날짜선택 휠뷰 보일지
        // displayContentChange();

        // 휠뷰 세팅
        // makeWheelView();

        // ViewPager 시작
        vp_calendar.post(() -> {
            i_viewPagerHeight = vp_calendar.getHeight();
            i_viewPagerWidth  = vp_calendar.getWidth();

            // 처음 로딩될때 화면 로딩이 빠른거처럼 보이도록 ㅠㅠ;;;
            new Thread(() -> runOnUiThread(() -> setCalendar())).start();
        });

        // display();

        // 타이틀 변경
        // setTextTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        if ( null != o_adapter ) {
            o_adapter = null;
        }

        vp_calendar.setAdapter(null);

        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.sliding_down);
    }

    // ====================================================================================
    // Override
    // ====================================================================================
    /**
     * 클릭 이벤트 선언
     * @param po_view
     */
    @OnClick({
        R.id.cl_empty,
        R.id.btn_today,
        R.id.ll_start,
        R.id.ll_end,
        R.id.tv_lunar,
        R.id.tv_allDay,
        R.id.btn_month,
        R.id.btn_yyyy,
        R.id.tv_bottomClose,
        R.id.tv_bottomSucc
    })
    public void onClick(View po_view) {
        switch ( po_view.getId() ) {
            // --------------------------------------------------------
            // 공백란
            // --------------------------------------------------------
            case R.id.cl_empty :
                finish();
                break;
            // --------------------------------------------------------
            // 오늘
            // --------------------------------------------------------
            case R.id.btn_today :
                o_displayCalendar = Calendar.getInstance();
                setCalendar();
                break;
            // --------------------------------------------------------
            // 시작일
            // --------------------------------------------------------
            case R.id.ll_start :
                s_calendar = CALENDAR_START_DATE;
                display();
                break;
            // --------------------------------------------------------
            // 종료일
            // --------------------------------------------------------
            case R.id.ll_end :
                s_calendar = CALENDAR_END_DATE;
                display();
                break;
            // --------------------------------------------------------
            // 음력
            // --------------------------------------------------------
            case R.id.tv_lunar :
                b_isLunar = !b_isLunar;
                display();
                break;
            // --------------------------------------------------------
            // 종일
            // --------------------------------------------------------
            case R.id.tv_allDay :
                b_isAllDay = !b_isAllDay;
                display();
                break;
            // --------------------------------------------------------
            // 년도,월
            // --------------------------------------------------------
            case R.id.btn_yyyy :
            case R.id.btn_month :
                b_isCalYearMonthSelected = !b_isCalYearMonthSelected;
                display();
                break;
            // --------------------------------------------------------
            // 년도,월 선택모드 취소
            // --------------------------------------------------------
            case R.id.tv_bottomClose :
                b_isCalYearMonthSelected = false;
                display();
                break;
            // --------------------------------------------------------
            // 년도,월 선택모드 저장
            // --------------------------------------------------------
            case R.id.tv_bottomSucc :
                b_isCalYearMonthSelected = false;
                setDisplaycalendarSetting(Integer.parseInt(arrS_yy.get(wheel_yyyy.getSelectedItemPosition())), Integer.parseInt(arrS_mm[wheel_month.getSelectedItemPosition()]));
                setCalendar();
                break;

            default:break;
        }
    }

    /**
     * 달력 셀 Click 이벤트
     * @param timeMillis
     */
    @Override
    public void onDateClick(long timeMillis, int year, int month, int date) {
        // 날짜변경 일반
        if ( b_isLunar == false ) {
            Calendar lo_target = s_calendar.equals(CALENDAR_START_DATE) ? HoduCommon.GF_DATE_TO_CALENDAR(o_start) : HoduCommon.GF_DATE_TO_CALENDAR(o_end);

            Date lo_date = new Date(timeMillis);
            long i_diffDay = s_calendar.equals(CALENDAR_START_DATE) ? HoduCommon.GF_CALCURATE_DDAY(o_end, lo_date) : HoduCommon.GF_CALCURATE_DDAY(lo_date, o_start);

            if ( i_diffDay < 0 ) {
                lo_target = HoduCommon.GF_DATE_TO_CALENDAR(o_start);
                lo_target.set(Calendar.YEAR, year);
                lo_target.set(Calendar.MONTH, month - 1);
                lo_target.set(Calendar.DATE, date);

                o_start = new Date(lo_target.getTimeInMillis());

                lo_target.set(Calendar.HOUR_OF_DAY, lo_target.get(Calendar.HOUR_OF_DAY) + 1);
                o_end = new Date(lo_target.getTimeInMillis());

                s_calendar = CALENDAR_START_DATE;
            }
            else {
                lo_target.set(Calendar.YEAR, year);
                lo_target.set(Calendar.MONTH, month - 1);
                lo_target.set(Calendar.DATE, date);

                if ( s_calendar.equals(CALENDAR_START_DATE) ) {
                    o_start = new Date(lo_target.getTimeInMillis());
                }
                else {
                    o_end = new Date(lo_target.getTimeInMillis());
                }
            }

            setTextTitle();
            o_adapter.setStartEndDate(o_start, o_end, b_isLunar, b_isAllDay);
            o_adapter.notifyDataSetChanged();
        }
    }

    // ====================================================================================
    // USER FUNCTION
    // ====================================================================================
    /**
     * 경우에따라 화면 컨트롤
     */
    private void display() {
        Log.e("KHJ", "display 호출");

        // 달력 변경모드 일때
        if ( b_isCalYearMonthSelected ) {
            cl_calendar.setVisibility(View.GONE);
            cl_yearMonthWheelView.setVisibility(View.VISIBLE);
            btn_today.setVisibility(View.INVISIBLE);

            btn_yyyy.setTextColor(getColor(R.color.color_app_theme));
            btn_month.setTextColor(getColor(R.color.color_app_theme));
        }
        else {
            cl_yearMonthWheelView.setVisibility(View.GONE);
            cl_calendar.setVisibility(View.VISIBLE);
            btn_today.setVisibility(View.VISIBLE);

            btn_yyyy.setTextColor(getColor(R.color.color_black));
            btn_month.setTextColor(getColor(R.color.color_black));
        }

        setTextTitle();

        setAllDayDisplay();
        setLunarDisplay();
        ll_bottomWheelView.setVisibility(b_isAllDay ? View.GONE : View.VISIBLE);

        // 휠뷰
        makeYearMonthWheelView();
        makeWheelView();
    }

    /**
     * 종일 변경
     */
    protected void setAllDayDisplay() {
        tv_allDay.setTextColor(b_isAllDay ? getColor(R.color.color_white) : getColor(R.color.color_black));
        tv_allDay.setBackgroundColor(b_isAllDay ? getColor(R.color.color_app_theme) : getColor(R.color.white));
        view_lunarAlldayCenter.setBackgroundColor((b_isLunar && b_isAllDay) ? getColor(R.color.white) : getColor(R.color.color_line_gray));
    }

    /**
     * 음력 변경
     */
    protected void setLunarDisplay() {
        tv_lunar.setTextColor(b_isLunar ? getColor(R.color.color_white) : getColor(R.color.color_black));
        tv_lunar.setBackgroundColor(b_isLunar ? getColor(R.color.color_app_theme) : getColor(R.color.white));
        view_lunarAlldayCenter.setBackgroundColor((b_isLunar && b_isAllDay) ? getColor(R.color.white) : getColor(R.color.color_line_gray));
    }

    /**
     * 시작날짜시간, 종료날짜시간 변경
     */
    protected void setTextTitle() {
        tv_startYmd.setTextColor(getColor(R.color.black));
        tv_startTime.setTextColor(getColor(R.color.black));
        tv_endYmd.setTextColor(getColor(R.color.black));
        tv_endTime.setTextColor(getColor(R.color.black));

        if ( s_calendar.equals(CALENDAR_START_DATE) ) {
            tv_startYmd.setTextColor(getColor(R.color.color_app_theme));
            tv_startTime.setTextColor(getColor(R.color.color_app_theme));
        }
        else {
            tv_endYmd.setTextColor(getColor(R.color.color_app_theme));
            tv_endTime.setTextColor(getColor(R.color.color_app_theme));
        }

        if ( b_isAllDay ) {
            tv_startYmd.setText(HoduCommon.GF_DATE_TO_STRING(o_start, "yyyy.MM.dd"));
            tv_startTime.setText(HoduCommon.GF_DATE_TO_STRING(o_start, "EEEE"));
            tv_endYmd.setText(HoduCommon.GF_DATE_TO_STRING(o_end, "yyyy.MM.dd"));
            tv_endTime.setText(HoduCommon.GF_DATE_TO_STRING(o_end, "EEEE"));
        }
        else {
            tv_startYmd.setText(HoduCommon.GF_DATE_TO_STRING(o_start, INVITE_DATE_FORMAT));
            tv_startTime.setText(HoduCommon.GF_DATE_TO_STRING(o_start, INVITE_TIME_FORMAT));
            tv_endYmd.setText(HoduCommon.GF_DATE_TO_STRING(o_end, INVITE_DATE_FORMAT));
            tv_endTime.setText(HoduCommon.GF_DATE_TO_STRING(o_end, INVITE_TIME_FORMAT));
        }
    }

    /**
     * 달력 만들기
     */
    private void setCalendar() {
        b_isViewPagerListner = false;

        if ( o_adapter != null ) {
            o_adapter = null;
        }

        vp_calendar.setAdapter(null);

        o_adapter = new DatePickerActivityPagerAdapter(getSupportFragmentManager(), i_viewPagerWidth, i_viewPagerHeight, this);
        vp_calendar.setAdapter(o_adapter);

        o_adapter.setStartEndDate(o_start, o_end, b_isLunar, b_isAllDay);
        o_adapter.setNumOfMonth(COUNT_PAGE, o_displayCalendar);

        vp_calendar.setOffscreenPageLimit(1);
        vp_calendar.setCurrentItem(COUNT_PAGE);

        if ( null != vp_calendar.getOnFocusChangeListener() ) {
            vp_calendar.addOnPageChangeListener(null);
        }

        vp_calendar.addOnPageChangeListener(o_viewPagerListner);

        new Handler().postDelayed(() -> {
            setYearMonthTitle(Constant.DATE_PICKER_COUNT);
            display();
        }, 100);

        new Handler().postDelayed(() -> {
            b_isViewPagerListner = true;
        }, 500);
    }

    private ViewPager.OnPageChangeListener o_viewPagerListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if ( !b_isViewPagerListner ) {
                return;
            }

            setYearMonthTitle(position);

            if ( position == 0 ) {
                o_adapter.addPrev();
                vp_calendar.setCurrentItem(COUNT_PAGE, false);
            }
            else {
                if (position == o_adapter.getCount() - 1) {
                    o_adapter.addNext();
                    vp_calendar.setCurrentItem(o_adapter.getCount() - (COUNT_PAGE + 1), false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 년도, 월 타이틀 변경
     * @param pi_position
     */
    protected void setYearMonthTitle(int pi_position) {
        int li_yy = o_adapter.getYearNumber(pi_position);
        int li_mm = o_adapter.getMonthNumber(pi_position);

        btn_yyyy.setText(li_yy+"년");
        btn_month.setText(li_mm+"월");

        setDisplaycalendarSetting(li_yy, li_mm);
    }

    /**
     * 보여지고있는 달력의 년도,날짜를 변경
     * @param pi_year
     * @param pi_month
     */
    protected void setDisplaycalendarSetting(int pi_year, int pi_month) {
        Calendar calendar = o_displayCalendar;
        calendar.set(Calendar.YEAR, pi_year);
        calendar.set(Calendar.MONTH, pi_month-1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        o_displayCalendar = Calendar.getInstance();
        o_displayCalendar.setTimeInMillis(calendar.getTimeInMillis());
    }

    /**
     * DATE TIME 변경
     * @param pi_calendar
     * @param pi_value
     */
    protected void setTimeChange(int pi_calendar, int pi_value) {
        Calendar lo_target = s_calendar.equals(CALENDAR_START_DATE) ? HoduCommon.GF_DATE_TO_CALENDAR(o_start) : HoduCommon.GF_DATE_TO_CALENDAR(o_end);

        if ( pi_calendar == Calendar.HOUR_OF_DAY ) {
            if ( pi_value == 12 && lo_target.get(Calendar.AM_PM) == 0 ) {
                lo_target.set(Calendar.HOUR_OF_DAY, 0);
            }
            else if ( pi_value == 12 && lo_target.get(Calendar.AM_PM) == 1 ) {
                lo_target.set(Calendar.HOUR_OF_DAY, 12);
            }
            else {
                lo_target.set(Calendar.HOUR, pi_value);
            }
        }
        else {
            lo_target.set(pi_calendar, pi_value);
        }

        if ( s_calendar.equals(CALENDAR_START_DATE) ) {
            o_start = new Date(lo_target.getTimeInMillis());
        }
        else {
            o_end = new Date(lo_target.getTimeInMillis());
        }

        setTextTitle();
    }

    /**
     * 오전 오후 Setting
     */
    protected void makeWheelView() {
        b_wheelViewListner = false;

        Calendar lo_target = s_calendar.equals(CALENDAR_START_DATE) ? HoduCommon.GF_DATE_TO_CALENDAR(o_start) : HoduCommon.GF_DATE_TO_CALENDAR(o_end);

        // ------------------------------------------------------------
        // APM 만들기
        // ------------------------------------------------------------
        arrS_ampm.clear();

        Calendar lo_cal = Calendar.getInstance();
        lo_cal.set(Calendar.HOUR_OF_DAY, 0);

        for ( int i = 0; i < 2; i++) {
            arrS_ampm.add(new SimpleDateFormat("a").format(lo_cal.getTime()));
            lo_cal.set(Calendar.HOUR_OF_DAY, 12);
        }

        wheel_ampm.setCyclic(false);
        wheel_ampm.setData(arrS_ampm);
        wheel_ampm.setSelectedItemPosition(lo_target.get(Calendar.AM_PM));

        wheel_ampm.setOnItemSelectedListener((wheelView, data, position) -> {
            if ( arrS_ampm.size() <= position ) {
                return;
            }

            if ( b_wheelViewListner ) {
                wheel_ampm.setSelectedItemPosition(position);
                setTimeChange(Calendar.AM_PM, position);
            }
        });

        // ------------------------------------------------------------
        // 시간 만들기
        // ------------------------------------------------------------
        wheel_hour.setData(Arrays.asList(arrS_hh));
        wheel_hour.setCyclic(true);

        int li_hourPosition = 0;

        if ( lo_target.get(Calendar.HOUR) == 0 ) {
            li_hourPosition = 11;
        }
        else {
            li_hourPosition = lo_target.get(Calendar.HOUR)-1;
        }

        wheel_hour.setSelectedItemPosition(li_hourPosition);

        wheel_hour.setOnItemSelectedListener((wheelView, data, position) -> {
            if ( arrS_hh.length <= position ) {
                return;
            }

            if ( b_wheelViewListner ) {
                wheel_hour.setSelectedItemPosition(position);
                setTimeChange(Calendar.HOUR_OF_DAY, Integer.parseInt(arrS_hh[position]));
            }
        });

        // ------------------------------------------------------------
        // 분 만들기
        // ------------------------------------------------------------
        wheel_minute.setData(Arrays.asList(arrS_mi));
        wheel_minute.setCyclic(true);
        wheel_minute.setSelectedItemPosition(lo_target.get(Calendar.MINUTE) / 10);

        wheel_minute.setOnItemSelectedListener((wheelView, data, position) -> {
            if ( arrS_mi.length <= position ) {
                return;
            }

            if ( b_wheelViewListner ) {
                wheel_minute.setSelectedItemPosition(position);
                setTimeChange(Calendar.MINUTE, Integer.parseInt(arrS_mi[position]));
            }
        });

        b_wheelViewListner = true;

        // 0.5 초 후에 실행
        new Handler().postDelayed(() -> b_wheelViewListner = true, 500);
    }

    /**
     * 년,월 휠뷰 Setting
     */
    protected void makeYearMonthWheelView() {
        b_yearMonthWheelViewListner = false;

        // ------------------------------------------------------------
        // 년도 만들기
        // ------------------------------------------------------------
        arrS_yy.clear();

        Calendar lo_target = o_displayCalendar;

        for ( int i = 1900; i <= 2050; i++ ) {
            arrS_yy.add(Integer.toString(i));
        }

        wheel_yyyy.setCyclic(true);
        wheel_yyyy.setData(arrS_yy);
        wheel_yyyy.setSelectedItemPosition(lo_target.get(Calendar.YEAR)-1900);

        wheel_yyyy.setOnItemSelectedListener((wheelView, data, position) -> {
            if ( arrS_yy.size() <= position ) {
                return;
            }

            if ( b_yearMonthWheelViewListner ) {
                wheel_hour.setSelectedItemPosition(position);
            }
        });

        // ------------------------------------------------------------
        // 월 만들기
        // ------------------------------------------------------------
        wheel_month.setCyclic(true);
        wheel_month.setData(Arrays.asList(arrS_mm));
        wheel_month.setSelectedItemPosition(lo_target.get(Calendar.MONTH));

        wheel_month.setOnItemSelectedListener((wheelView, data, position) -> {
            if ( arrS_mm.length <= position ) {
                return;
            }

            if ( b_yearMonthWheelViewListner ) {
                wheel_month.setSelectedItemPosition(position);
            }
        });

        b_yearMonthWheelViewListner = true;

        // 0.5 초 후에 실행
        new Handler().postDelayed(() -> b_yearMonthWheelViewListner = true, 500);
    }



    /**
     * start, end 오늘날짜 1시간 기준으로 변경
    private void dateInit() {
        Calendar lo_cal = Calendar.getInstance();

        String ls_hour = "";
        String ls_min  = "";
        String ls_frTm = "";

        // 캘린더 객체로부터 시간 구하기
        if ( String.valueOf(lo_cal.get(Calendar.HOUR_OF_DAY)).length() == 1 ){
            ls_hour = "0" + String.valueOf(lo_cal.get(Calendar.HOUR_OF_DAY));
        }
        else {
            ls_hour = String.valueOf(lo_cal.get(Calendar.HOUR_OF_DAY));
        }

        // 캘린더 객체로부터 분 구하기
        if ( String.valueOf(lo_cal.get(Calendar.MINUTE)).length() == 1 ) {
            ls_min = "0" + String.valueOf(lo_cal.get(Calendar.MINUTE));
        }
        else {
            ls_min = String.valueOf(lo_cal.get(Calendar.MINUTE));
        }

        // 현재 시간이 정시라면 그대로 사용한다.
        if ( "00".equals(ls_min) ) {
            ls_frTm = ls_hour + ls_min;
        }
        // 정시가 아니라면 가장 가까운 미래의 정시로 보여준다. ( 23시인 경우라면 그시간 그대로 간다.)
        else {
            if ( ls_hour.equals("23") ) {
                ls_frTm = ls_hour + "00";
            }
            else {
                ls_frTm = HoduCommon.GF_GET_CALC_TIME(ls_hour + "00", 1, 'H');
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        o_start = HoduCommon.GF_STRING_TO_DATE(sdf.format(lo_cal.getTime()) + ls_frTm + "00");

        // 시작시간이 23시 이후라면 종료시간은 23시 50분
        if ( ls_frTm.substring(0, 2).equals("23") ) {
            o_end = HoduCommon.GF_STRING_TO_DATE(sdf.format(lo_cal.getTime()) + "2350" + "00");
        }
        else {
            o_end = HoduCommon.GF_STRING_TO_DATE(sdf.format(lo_cal.getTime()) + HoduCommon.GF_GET_CALC_TIME(ls_frTm, 1, 'H') + "00");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(o_start);
        o_displayCalendar = cal;
    }
    */
}
