package com.example.androiddev.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.androiddev.R;
import com.example.androiddev.common.Constant;
import com.example.androiddev.common.DateCalendarDataSet;

import java.util.HashMap;
import java.util.Map;

public class DateMonthView extends View {
    public DateCalendarDataSet dateCalendarDataSet = new DateCalendarDataSet();

    public TextPaint _paint_day = new TextPaint();
    public TextPaint _paint_day_satuday = new TextPaint();
    public TextPaint _paint_day_sunday = new TextPaint();
    public TextPaint _paint_day_ex_month = new TextPaint();

    Map<String, Object> o_centerTextWidthMap = new HashMap<>();

    private int canvasWidth;
    private int canvasHeight;
    public int day_height=0;
    public int divider_height=0;
    int cell_width = 0;
    int cell_height = 0;

    public DateMonthView(Context context) {
        super(context);
        initUI();
    }

    public DateMonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public DateMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    public DateMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI();
    }

    protected void initUI() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setPaint();

        calcDayCellSize(canvas);

        drawMonth(canvas);  // 주 그리기
    }

    // 셀 사이즈, 셀 안에 이벤트 들어갈 수 있는 최대갯수 계산
    protected void calcDayCellSize(Canvas canvas) {
        int week_count = dateCalendarDataSet.week_count;
        int width =  canvas.getWidth();
        int height =  canvas.getHeight();
        cell_width = width / 7;
        cell_height = height / week_count;
    }

    // 초기에 한번 , 설정이 변경되었을때만
    protected void setPaint() {
        Context context = getContext();

        Constant.SP_DATE_FONT_SIZE = 11;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        day_height     = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, Constant.SP_DATE_FONT_SIZE, displayMetrics)+0.5);
        divider_height = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics)+0.5);

        // 날짜
        _paint_day.setAntiAlias(true);
        _paint_day.setTextSize(day_height);
        _paint_day.setColor(Color.BLACK);
        _paint_day.setTextAlign(Paint.Align.CENTER);

        // 날짜 토요일
        _paint_day_satuday.set(_paint_day);
        _paint_day_satuday.setColor(Color.BLACK);

        // 날짜 일요일
        _paint_day_sunday.set(_paint_day);
        _paint_day_sunday.setColor(Color.RED);

        // 날짜 해당월 이 아닌 날짜
        _paint_day_ex_month.set(_paint_day);
        _paint_day_ex_month.setColor(getContext().getColor(R.color.ex_month_text_color));
    }

    protected void drawMonth(Canvas canvas) {
        int week_count = dateCalendarDataSet.week_count;



        for ( int i = 0; i < week_count; i++ ) {
            //drawWeek(canvas, i,i+1 == week_count);
        }
    }

    protected void drawWeek(Canvas canvas, int week_row,boolean isLast) {
        Rect rect_row = new Rect(0, cell_height * week_row, canvasWidth, (cell_height * week_row) + cell_height); // 주 단위로 큰 Rect

        if ( isLast ) rect_row.bottom = canvasHeight;

        Rect rect_cell = new Rect(0, rect_row.top, cell_width, rect_row.bottom);

        for ( int cell_idx = 0; cell_idx < 7; cell_idx++ ) {
            canvas.save();
            canvas.clipRect(rect_cell);
            drawCellBasic(canvas, week_row, cell_idx);
            canvas.restore();
            rect_cell.offset(cell_width ,0);
        }
    }

    // 셀 기본 정보 그리기
    // 날짜 , 음력
    private void drawCellBasic(Canvas canvas,int week_row, int cell_idx) {
        Rect rect_cell = canvas.getClipBounds();

        int idx = week_row*7+cell_idx;

        String day = Integer.toString(dateCalendarDataSet.cell_info_day[idx]);

        StringBuilder lo_textWidthKeyBuilder = new StringBuilder();
        lo_textWidthKeyBuilder.append(week_row).append("_").append(day);
        final String text_width_map_key = lo_textWidthKeyBuilder.toString();

        Rect r = new Rect(rect_cell);

        if ( !dateCalendarDataSet.cell_info_this_month[idx] ) {
            drawTextCenter(canvas, _paint_day_ex_month, day, r, text_width_map_key);
        }
        else if ( cell_idx == 0 ) {
            drawTextCenter(canvas, _paint_day_sunday, day, r, text_width_map_key);
        }
        else if ( cell_idx == 6 ) {
            drawTextCenter(canvas, _paint_day_satuday, day, r, text_width_map_key);
        }
        else {
            drawTextCenter(canvas, _paint_day, day, r, text_width_map_key);
        }
    }

    /**
     * 텍스트 그리기 (${week_row}_${text}를 key로 text_width를 value로 하는 Map에 데이터를 넣어준다
     * @param canvas
     * @param paint
     * @param text
     * @param rect
     * @param text_width_map_key
     */
    private void drawTextCenter(Canvas canvas, Paint paint, String text, Rect rect, String text_width_map_key) {
        RectF bounds = new RectF(rect);

        bounds.right = paint.measureText(text, 0, text.length());
        bounds.bottom = paint.descent() - paint.ascent();
        bounds.left += (rect.width() - bounds.right) / 2.0f;
        bounds.top += (rect.height() - bounds.bottom) / 2.0f;

        // drawTextRight에 사용할 key, value를 넣는다
        if( text_width_map_key != null ) { o_centerTextWidthMap.put(text_width_map_key, bounds.right); }

        canvas.drawText(text, bounds.left, bounds.top - paint.ascent(), paint);
    }

    public void changeData(int position) {
        dateCalendarDataSet.changeData(getContext(), position);
    }
}
