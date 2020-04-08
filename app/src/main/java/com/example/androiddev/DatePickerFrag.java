package com.example.androiddev;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.androiddev.common.DatePickerListner;
import com.example.androiddev.common.HoduCommon;
import com.example.androiddev.dto.DatePickerVO;
import com.example.androiddev.views.GinusCircleDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DatePickerFrag extends Fragment implements DatePickerListner {
    //==============================================================================================
    // NESTED CLASS Define
    //==============================================================================================

    //==============================================================================================
    // Constant Define
    //==============================================================================================
    private static final String INVITE_DATE_FORMAT_YYYY_MM_DD = "yyyyMMdd";

    //==============================================================================================
    // Instance Layout xml Variable Define With ButterKnife
    //==============================================================================================
    @BindView(R.id.ll_content) LinearLayout ll_content;

    //==============================================================================================
    // Instance Variable Define
    //==============================================================================================
    protected DatePickerListner       o_listner;
    protected Context                 o_context;
    protected ViewGroup               vg_main = null;
    protected int                     i_viewPagerWidth;
    protected int                     i_viewPagerHeight;
    protected Unbinder                o_unbinder = null;
    protected long                    l_monthMillis = 0;
    protected int                     i_weekCount = 0;
    protected int                     i_cellCount = 0;
    protected int                     i_cellHeight = 0;
    protected int                     i_cellWidth  = 0;
    protected Calendar                calendar;
    protected ArrayList<DatePickerVO> arrO_dates = new ArrayList<>();
    protected int                     i_textSize = 11;
    private   Date                    o_start;
    private   Date                    o_end;
    private   String                  s_startYmd;
    private   String                  s_endYmd;
    private   boolean                 b_isInterval;     // 시작날자, 종료날자 에 회색 백그라운드를 넣어야할때 true 로 바뀜... 더 좋은방법있음 찾아야함..
    private   long                    i_diffDay = 0;    // 시작날자, 종료날자 차이
    private   boolean                 b_isLunar = false;
    private   boolean                 b_isAllDay = false;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    //==============================================================================================
    // INSTANCE
    //==============================================================================================
    public static DatePickerFrag newInstance(int pi_viewPagerWidth, int pi_viewPagerHeight) {
        DatePickerFrag frg = new DatePickerFrag();
        Bundle bundle = new Bundle();
        bundle.putInt("width" , pi_viewPagerWidth);
        bundle.putInt("height", pi_viewPagerHeight);
        frg.setArguments(bundle);
        return frg;
    }

    public void setO_listner(DatePickerListner po_listner) {
        this.o_listner = po_listner;
    }

    public void setStartEndDate(Date o_start, Date o_end, boolean b_isLunar, boolean b_isAllDay) {
        this.b_isLunar      = b_isLunar;
        this.b_isAllDay     = b_isAllDay;
        this.o_start        = new Date(setTimeZero(o_start).getTimeInMillis());
        this.o_end          = new Date(setTimeZero(o_end).getTimeInMillis());
        this.s_startYmd     = HoduCommon.GF_DATE_TO_STRING(this.o_start, INVITE_DATE_FORMAT_YYYY_MM_DD);
        this.s_endYmd       = HoduCommon.GF_DATE_TO_STRING(this.o_end, INVITE_DATE_FORMAT_YYYY_MM_DD);
        this.i_diffDay      = HoduCommon.GF_CALCURATE_DDAY(this.o_start, this.o_end);
    }

    //==============================================================================================
    // Fragment LifeCycle
    //==============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i_viewPagerWidth  = getArguments().getInt("width");
        i_viewPagerHeight = getArguments().getInt("height");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        o_context = container.getContext();
        vg_main = (ViewGroup) inflater.inflate(R.layout.fragment_date_picker_item, container, false);

        o_unbinder = ButterKnife.bind(this, vg_main);

        //DisplayMetrics displayMetrics = o_context.getResources().getDisplayMetrics();
        //i_textSize = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, displayMetrics)+0.5);

        initData();

        return vg_main;
    }

    @Override
    public void onAttach(Context po_context) {
        o_context = po_context;
        super.onAttach(po_context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        o_unbinder.unbind();
    }

    //==============================================================================================
    // 초기 설정
    //==============================================================================================
    private void initUI() {
        ll_content.removeAllViews();

        int li_subStartIdx = 0;

        for ( int i = 0; i < i_weekCount; i++ ) {
            LinearLayout.LayoutParams lo_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, i_cellHeight);
            LinearLayout              lo_linear = new LinearLayout(o_context);

            lo_linear.setOrientation(LinearLayout.HORIZONTAL);
            lo_linear.setBackgroundColor(o_context.getColor(R.color.white));
            lo_linear.setLayoutParams(lo_params);
            lo_linear.setId(i);
            ll_content.addView(lo_linear);

            li_subStartIdx = (i*7);

            for ( int j = li_subStartIdx; j < (li_subStartIdx+7); j++ ) {
                if ( li_subStartIdx >= (i_cellCount-1) ) {
                    break;
                }

                makeSubLayout(lo_linear, j);
            }
        }
    }

    private void initData() {
        b_isInterval = false;
        arrO_dates.clear();

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l_monthMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        i_weekCount = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);  // 총 주수
        i_cellCount = 7 * i_weekCount; // 총 날짜수
        i_cellHeight = i_viewPagerHeight / i_weekCount;
        i_cellWidth = i_viewPagerWidth / 7;

        // 1일의 요일
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        ///< 1일이 일요일이 아닐 경우 이전월의 날짜를 넣기 위한 개수 구하기.
        int preOfDay = dayOfWeek-1;

        //이달의 마지막 날
        int maxDateOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(l_monthMillis);
        calendar1.set(Calendar.DATE, maxDateOfMonth);

        ///< 마지막 날이 토요일이 아닐경우 다음월의 날짜를 넣기 위한 개수 구하기
        int nextOfDay = 7 - calendar1.get(Calendar.DAY_OF_WEEK);

        // 이전
        for ( int i = 0; i < preOfDay; i++ ) {
            if ( i == 0 ) calendar.add(Calendar.DATE,-(preOfDay-i));
            else calendar.add(Calendar.DATE,1);

            arrO_dates.add(makeDatePickerVO(false));
        }

        if ( preOfDay > 0 ) {
            calendar.add(Calendar.DATE,1);
        }

        // 현재
        for ( int i = 0; i < maxDateOfMonth; i++ ) {
            arrO_dates.add(makeDatePickerVO(true));
            calendar.add(Calendar.DATE, 1);
        }

        // 미래
        for ( int i = 0; i < nextOfDay; i++ ) {
            arrO_dates.add(makeDatePickerVO(false));
            calendar.add(Calendar.DATE, 1);
        }

        initUI();
    }

    //==============================================================================================
    //  기본 Listener Override 재 정의
    //==============================================================================================
    @Override
    public void onDateClick(long timeMillis, int year, int month, int date) {
        o_listner.onDateClick(timeMillis, year, month, date);
    }

    //==============================================================================================
    // 추가적인 Override 재 정의 - parent 구현 외적인 것만 처리
    //==============================================================================================

    //==============================================================================================
    // User Functions Start
    //==============================================================================================
    /**
     * 날짜
     * @param timeByMillis
     */
    public void setTimeByMillis(long timeByMillis) {
        this.l_monthMillis = timeByMillis;
    }

    /**
     * 데이터 만들기
     * @param is_month : 현재달은 true
     * @return
     */
    private DatePickerVO makeDatePickerVO(boolean is_month) {
        DatePickerVO lo_data = new DatePickerVO();
        lo_data.setTimeMillis(calendar.getTimeInMillis());

        if ( calendar.get(Calendar.DAY_OF_WEEK) == 1 ) {
            lo_data.setColor(is_month ? o_context.getColor(R.color.color_red) : o_context.getColor(R.color.holiday_ex_month_color));
        }
        else if ( calendar.get(Calendar.DAY_OF_WEEK) == 7 ) {
            lo_data.setColor(is_month ? o_context.getColor(R.color.black) : o_context.getColor(R.color.ex_month_text_color));
        }
        else {
            lo_data.setColor(is_month ? o_context.getColor(R.color.black) : o_context.getColor(R.color.ex_month_text_color));
        }

        // 일반 날짜변경
        if ( this.b_isLunar == false ) {
            lo_data.setIs_start(s_startYmd.equals(HoduCommon.GF_DATE_TO_STRING(calendar, INVITE_DATE_FORMAT_YYYY_MM_DD)));
            lo_data.setIs_end(s_endYmd.equals(HoduCommon.GF_DATE_TO_STRING(calendar, INVITE_DATE_FORMAT_YYYY_MM_DD)));

            // 시작날짜,종료날짜 가 다르면
            if ( i_diffDay != 0 ) {
                b_isInterval = true;

                if ( !(lo_data.is_start() || lo_data.is_end()) ) {
                    lo_data.setIs_interval(getIsInterval(lo_data.getTimeMillis()));
                }
            }
        }

        return lo_data;
    }

    /**
     * getYYYYMMDD
     * @param ll_millis
     * @return
     */
    private String getYYYYMMDD(long ll_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        date.setTime(ll_millis);

        return sdf.format(date);
    }

    private Calendar getDateToCalendar(Date po_date) {
        Calendar lo_calendar = Calendar.getInstance();
        lo_calendar.setTime(po_date);
        return lo_calendar;
    }

    /**
     * START DATE ~ END DATE 차이
     * @param ll_millis
     * @return
     */
    private boolean getIsInterval(long ll_millis) {
        if ( !(null != o_start && null != o_end) ) {
            return false;
        }

        Calendar lo_startCal = Calendar.getInstance();
        lo_startCal.setTime(o_start);

        Calendar lo_endCal = Calendar.getInstance();
        lo_endCal.setTime(o_end);

        return ll_millis > lo_startCal.getTimeInMillis() && ll_millis < lo_endCal.getTimeInMillis();
    }

    /**
     * SubView 만들기
     * @param po_layout
     * @param pi_index
     */
    private void makeSubLayout(LinearLayout po_layout, int pi_index) {
        DatePickerVO lo_data = arrO_dates.get(pi_index);

        Calendar lo_subviewCal = Calendar.getInstance();
        lo_subviewCal.setTimeInMillis(lo_data.getTimeMillis());

        ConstraintLayout.LayoutParams lo_cellParam = new ConstraintLayout.LayoutParams(i_cellWidth, ConstraintLayout.LayoutParams.MATCH_PARENT);
        ConstraintLayout lo_cell = new ConstraintLayout(o_context);
        lo_cell.setLayoutParams(lo_cellParam);
        lo_cell.setId(View.generateViewId());

        // 클릭 이벤트
        lo_cell.setOnClickListener(view -> {
            Calendar lo_clickDate = Calendar.getInstance();
            lo_clickDate.setTimeInMillis(lo_data.getTimeMillis());

            onDateClick(lo_data.getTimeMillis(), lo_clickDate.get(Calendar.YEAR), (lo_clickDate.get(Calendar.MONTH) + 1), lo_clickDate.get(Calendar.DATE));
        });

        // 일반 날짜변경
        if ( this.b_isLunar == false ) {
            // 시작일자 회색 백그라운드
            if ( lo_data.is_start() && b_isInterval ) {
                makeStartEndBackGround(lo_cell, "S");
            }

            // 종료일자 회색 백그라운드
            if ( lo_data.is_end() && b_isInterval ) {
                makeStartEndBackGround(lo_cell, "E");
            }

            // 이외 회색 백그라운드
            if ( lo_data.is_interval() ) {
                makeStartEndInterval(lo_cell);
            }

            // 시작,종료 Circle 만들기
            if ( lo_data.is_start() || lo_data.is_end() ) {
                lo_data.setColor(o_context.getColor(R.color.white));
                makeCirCle(lo_cell);
            }
        }

        // 텍스트 뷰 만들기
        makeTextView(lo_cell, lo_data, lo_subviewCal, (lo_data.is_start() || lo_data.is_end()));

        // 부모 Linear 에 넣음
        po_layout.addView(lo_cell);
    }

    /**
     * 백그라운드 Gray 만들기
     * @param po_cell
     */
    private void makeStartEndBackGround(ConstraintLayout po_cell, String ps_type) {
        int li_height = (int) Math.ceil(i_cellHeight / 1.5);

        View lo_view = new View(o_context);
        lo_view.setLayoutParams(new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, li_height));
        lo_view.setBackgroundColor(o_context.getColor(R.color.color_gray));
        lo_view.setId(View.generateViewId());

        po_cell.addView(lo_view);

        ConstraintSet lo_set = new ConstraintSet();
        lo_set.clone(po_cell);

        lo_set.connect(lo_view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, ps_type.equals("S") ? (i_cellWidth / 2) : 0);
        lo_set.connect(lo_view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, ps_type.equals("E") ? (i_cellWidth / 2) : 0);
        lo_set.connect(lo_view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        lo_set.connect(lo_view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        lo_set.applyTo(po_cell);
    }

    /**
     * 백그라운드 Gray 만들기
     * @param po_cell
     */
    private void makeStartEndInterval(ConstraintLayout po_cell) {
        int li_height = (int) Math.ceil(i_cellHeight / 1.5);

        View lo_view = new View(o_context);
        lo_view.setLayoutParams(new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, li_height));
        lo_view.setBackgroundColor(o_context.getColor(R.color.color_gray));
        lo_view.setId(View.generateViewId());

        po_cell.addView(lo_view);

        ConstraintSet lo_set = new ConstraintSet();
        lo_set.clone(po_cell);

        lo_set.connect(lo_view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        lo_set.connect(lo_view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        lo_set.connect(lo_view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        lo_set.connect(lo_view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        lo_set.applyTo(po_cell);
    }

    /**
     * 텍스트뷰 만들기
     * @param po_cell
     * @param po_data
     * @param po_subviewCal
     */
    private void makeTextView(ConstraintLayout po_cell, DatePickerVO po_data, Calendar po_subviewCal, boolean is_bold) {
        TextView lo_textView = new TextView(o_context);
        lo_textView.setText(po_subviewCal.get(Calendar.DATE) + "");
        lo_textView.setTextSize(i_textSize);
        lo_textView.setTextColor(po_data.getColor());
        lo_textView.setLayoutParams(new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lo_textView.setId(View.generateViewId());
        lo_textView.setGravity(Gravity.CENTER);
        lo_textView.setTypeface(null, is_bold ? Typeface.BOLD : Typeface.NORMAL);
        po_cell.addView(lo_textView);

        ConstraintSet lo_set = new ConstraintSet();
        lo_set.clone(po_cell);

        lo_set.connect(lo_textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        lo_set.connect(lo_textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        lo_set.connect(lo_textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        lo_set.connect(lo_textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        lo_set.applyTo(po_cell);
    }

    /**
     * Circle 만들기
     * @param po_cell
     */
    private void makeCirCle(ConstraintLayout po_cell) {
        int li_height = (int) Math.ceil(i_cellHeight / 1.6);

        ImageView lo_imageView = new ImageView(o_context);
        lo_imageView.setBackground(new GinusCircleDrawable(o_context.getColor(R.color.color_app_theme)));
        lo_imageView.setLayoutParams(new ConstraintLayout.LayoutParams(li_height, li_height));
        lo_imageView.setId(View.generateViewId());

        po_cell.addView(lo_imageView);

        ConstraintSet lo_set = new ConstraintSet();
        lo_set.clone(po_cell);

        lo_set.connect(lo_imageView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        lo_set.connect(lo_imageView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        lo_set.connect(lo_imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        lo_set.connect(lo_imageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        lo_set.applyTo(po_cell);
    }

    /**
     * 00 : 00 : 00 으로 초기화
     * @param po_date
     * @return
     */
    private Calendar setTimeZero(Date po_date) {
        Calendar lo_calendar = HoduCommon.GF_DATE_TO_CALENDAR(po_date);
        lo_calendar.set(Calendar.HOUR_OF_DAY, 0);
        lo_calendar.set(Calendar.MINUTE, 0);
        lo_calendar.set(Calendar.SECOND, 0);
        lo_calendar.set(Calendar.MILLISECOND, 0);

        return lo_calendar;
    }
}
