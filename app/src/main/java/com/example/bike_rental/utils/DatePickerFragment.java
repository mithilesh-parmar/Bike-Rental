package com.example.bike_rental.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "DatePickerFragment";
    private final static String KEY_MIN_DATE_ALLOWED = TAG+"MIN_DATE_ALLOWED";
    private final static String KEY_MAX_DATE_ALLOWED = TAG+"MAX_DATE_ALLOWED";
    private final static String KEY_TITLE =TAG+ ".KEY_TITLE";
    public static final String EXTRA_YEAR = TAG + "EXTRA_YEAR";
    public static final String EXTRA_MONTH = TAG + "EXTRA_MONTH";
    public static final String EXTRA_DAY_OF_MONTH = TAG + "EXTRA_DAY_OF_MONTH";


    /**
     * create the instance of this fragment
     * @param minAllowedDate min date that can be selected
     * @param maxAllowedDate max date that can be selected
     * @param title title for dialog
     * @return
     */
    public static DatePickerFragment getInstance(int minAllowedDate, int maxAllowedDate,String title){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MIN_DATE_ALLOWED,minAllowedDate);
        bundle.putInt(KEY_MAX_DATE_ALLOWED,maxAllowedDate);
        bundle.putString(KEY_TITLE,title);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        int minAllowedDate = -1; // default value
        int maxAllowedDate = -1; // default value


        // get the values from bundle if it was a configuration change
        if (bundle != null){
            Log.d(TAG, "onViewCreated: "+maxAllowedDate);
            maxAllowedDate = bundle.getInt(KEY_MAX_DATE_ALLOWED);
            minAllowedDate = bundle.getInt(KEY_MIN_DATE_ALLOWED);
        }

        // setup the calendar instance with year , month , day
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd  = new DatePickerDialog(getActivity(), this, year, month, day);


        if (maxAllowedDate != -1 && minAllowedDate != -1){
//             if max allowed date and min allowed date are provided
            Log.d(TAG, "onCreateDialog: "+maxAllowedDate);
            calendar.add(Calendar.DATE, maxAllowedDate);
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            Log.d(TAG, "onCreateDialog: "+minAllowedDate);
            calendar.add(Calendar.DATE, minAllowedDate);
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }

        return dpd;
    }


    /**
     * on date changed
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (view.isShown()){
            Log.d(TAG, "onDateSet: year "+year+" month "+month+" dayofmonth "+dayOfMonth);
            sendResult(Activity.RESULT_OK,year,month,dayOfMonth);
        }
    }

    /**
     * send selected date to calling fragment using intent
     * @param resultCode
     * @param year
     * @param month
     * @param dayOfMonth
     */
    private void sendResult(int resultCode,int year, int month, int dayOfMonth){

        if (getTargetFragment() == null)return;

        Intent intent  = new Intent();
        intent.putExtra(EXTRA_YEAR,year);
        intent.putExtra(EXTRA_MONTH,month);
        intent.putExtra(EXTRA_DAY_OF_MONTH,dayOfMonth);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

}
