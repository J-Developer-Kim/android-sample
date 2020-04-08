package com.example.androiddev.common;

public class CommonEnum {
    public enum MenuCode {
          CHAT("CHAT")
        , IMAGE_PICKER("IMAGE_PICKER")
        , GOOGLE_MAP_SAMPLE("GOOGLE_MAP_SAMPLE")
        , CALENDAR_SELECTED("CALENDAR_SELECTED")
        , DATE_PICKER("DATE_PICKER");

        public String name;

        MenuCode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /*
    public enum CalendarType {
        _NORMAL(),
        _ALLDAY();

        CalendarType() {

        }

        public final static int NORMAL = 1;
        public final static int ALLDAY = 2;
    }
     */
}
