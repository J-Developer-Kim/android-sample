package com.example.androiddev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.androiddev.adapter.DateSelectViewPagerAdapter;
import com.example.androiddev.common.HoduCommon;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateSelectActivity extends AppCompatActivity {
    // ====================================================================================
    // Constant
    // ====================================================================================

    // ====================================================================================
    // Object
    // ====================================================================================
    private Calendar o_selectedDate;
    private DateSelectViewPagerAdapter o_pagerAdapter;

    // ====================================================================================
    // BindView
    // ====================================================================================
    @BindView(R.id.cl_backGround) ConstraintLayout cl_backGround;
    @BindView(R.id.cl_content)    ConstraintLayout cl_content;
    @BindView(R.id.cl_empty)      ConstraintLayout cl_empty;
    @BindView(R.id.vp_calendar)   ViewPager        vp_calendar;
    @BindView(R.id.btn_yyyy)      Button           btn_yyyy;
    @BindView(R.id.btn_month)     Button           btn_month;
    @BindView(R.id.btn_today)     Button           btn_today;

    // ====================================================================================
    // Activity LifeCycle
    // ====================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);

        ButterKnife.bind(this);

        if ( null == o_selectedDate ) {
            o_selectedDate = Calendar.getInstance();
        }

        initUI();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.sliding_down);
    }

    //=====================================================================================
    // 초기 설정
    //=====================================================================================
    private void initUI() {
        showMonthCalendarForViewPager();
        setTitleChange();
    }

    // ====================================================================================
    // Override
    // ====================================================================================
    /**
     * 클릭 이벤트 선언
     * @param po_view
     */
    @OnClick({
            R.id.cl_empty, R.id.btn_today
    })
    public void onClick(View po_view) {
        switch ( po_view.getId() ) {
            case R.id.cl_empty :
                finish();
                break;

            case R.id.btn_today :
                o_selectedDate = Calendar.getInstance();
                o_selectedDate.setTime(HoduCommon.GF_STRING_TO_DATE(HoduCommon.GF_GET_TODAY()));
                initUI();
                break;

            default:break;
        }
    }

    /**
     * 월 달력 보여주기
     */
    private void showMonthCalendarForViewPager() {
        vp_calendar.setAdapter(null);
        o_pagerAdapter = null;

        o_pagerAdapter = new DateSelectViewPagerAdapter(this);
        vp_calendar.setAdapter(o_pagerAdapter);

        Calendar lo_firstDayCal = (Calendar) o_selectedDate.clone();
        lo_firstDayCal.set(Calendar.DATE, 1);

        vp_calendar.setCurrentItem(HoduCommon.getPositionFromYearMonth(lo_firstDayCal));
        vp_calendar.addOnPageChangeListener(o_pageViewChangeListner);
    }

    /**
     * ViewPager Change Handler
     */
    private ViewPager.OnPageChangeListener o_pageViewChangeListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            o_selectedDate = HoduCommon.getCalObjFromPosition(i);
            setTitleChange();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 버튼 텍스트 변경
     */
    private void setTitleChange() {
        btn_yyyy.setText(o_selectedDate.get(Calendar.YEAR)+"년");
        btn_month.setText((o_selectedDate.get(Calendar.MONTH) + 1)+"월");
    }
}
