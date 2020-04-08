package com.example.androiddev.dto;

import android.graphics.Color;

import java.util.Calendar;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DatePickerVO {
    private long    timeMillis;
    private int     color;
    private boolean is_start;
    private boolean is_end;
    private boolean is_interval;

    public DatePickerVO() {

    }

    public DatePickerVO(long timeMillis, int color, boolean is_start, boolean is_end) {
        this.timeMillis   = timeMillis;
        this.color        = color;
        this.is_start     = is_start;
        this.is_end       = is_end;
    }

    public void setIs_start(boolean is_start) {
        this.is_start = is_start;
    }

    public void setIs_end(boolean is_end) {
        this.is_end = is_end;
    }

    public void setIs_interval(boolean is_interval) {
        this.is_interval = is_interval;
    }
}
