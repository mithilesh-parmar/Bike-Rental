package com.example.bike_rental.utils;


import android.annotation.SuppressLint;
import android.util.Log;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingTime implements Serializable {

    private static final String TAG = "BookingTime";
    private int dayOfMonth;
    private int month;
    private int hourOfDay;
    private int minute;
    private final long millisInDay = (1000 * 60 * 60 * 24);

    @SuppressLint("SimpleDateFormat")
    public BookingTime() {
        // set initial date and time to current system time and date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * sets the time to 6 hours from current time
     */
    public void setTimeToSixHoursPlus(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,6);
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);

    }
}
