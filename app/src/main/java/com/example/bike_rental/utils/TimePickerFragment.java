package com.example.bike_rental.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "TimePickerFragment";
    private static final String KEY_TITLE = TAG + ".KEY_TITLE";
    public static final String EXTRA_HOUR_OF_DAY =TAG + ".EXTRA_HOUR_OF_DAY";
    public static final String EXTRA_MINUTE =TAG + ".EXTRA_MINUTE";


    /**
     *
     * @param title title for dialog
     * @return
     */
    public static TimePickerFragment getInstance(String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title="";

        // if arguments are provided  then get the title values
        if (args != null){
            title =  args.getString(KEY_TITLE);
        }

        // setup the calendar instance
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hourOfDay, minute, false);
        dialog.setTitle(title);

        return dialog;
    }

    /**
     * on time set send the result to calling fragment
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (view.isShown() ) {
            Log.d(TAG, "onTimeSet: "+hourOfDay+minute);
            sendResult(Activity.RESULT_OK,hourOfDay,minute);
        }

    }

    /**
     * send the selected time to calling fragment
     * @param resultCode
     * @param hourOfDay
     * @param minute
     */
    private void sendResult(int resultCode,int hourOfDay, int minute){
        if (getTargetFragment() == null)return;

        Intent intent  = new Intent();
        intent.putExtra(EXTRA_HOUR_OF_DAY,hourOfDay);
        intent.putExtra(EXTRA_MINUTE,minute);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


}
