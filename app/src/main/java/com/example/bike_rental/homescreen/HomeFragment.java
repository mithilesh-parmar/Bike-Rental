package com.example.bike_rental.homescreen;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bike_rental.utils.DatePickerFragment;
import com.example.bike_rental.models.Bike;
import com.example.bike_rental.R;
import com.example.bike_rental.databinding.FragmentHomeBinding;
import com.example.bike_rental.databinding.ListItemBikeBinding;
import com.example.bike_rental.pickuplocationscreen.PickupLocationActivity;
import com.example.bike_rental.utils.Pickerlayoutmanager;
import com.example.bike_rental.utils.BookingTime;
import com.example.bike_rental.utils.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//TODO migrate all the location code from mainactivity and add a geocoder to get city of the user
public class HomeFragment  extends Fragment  {

    /**
     * static method to get a fragment of this class
     * @return
     */
    public static HomeFragment createInstance(){ return new HomeFragment(); }

    // constants for requestcodes and keys for outstate
    private static final String TAG = "HomeFragment";
    private static final int REQUEST_CODE_FROM_DATE = 941;
    private static final int REQUEST_CODE_FROM_TIME = 142;
    private static final int REQUEST_CODE_TILL_DATE = 435;
    private static final int REQUEST_CODE_TILL_TIME = 441;
    private final static int REQUEST_CODE_PICKUP_LOCATION = 314;
    private static final String KEY_PICKUP_LOCATION_NAME = TAG + "Pickup location name ";
    private static final String KEY_PICKUP_DETAILS = TAG + "KEY_PICKUP_DETAILS";
    private static final String KEY_DROPOFF_DETAILS = TAG + "KET_DROPOFF_DETAILS";

    // object declaration
    private String pickupLocation  = "Select pickup location";
    private List<Bike> availableBikes = new ArrayList<>(); // available bikes
    private FragmentHomeBinding fragmentHomeBinding; // Binding for the layout inflated for the fragment
    private HomeFragmentViewModel viewModel; // view Model for this fragment
    private SharedPreferences.Editor editor; // editor instance for saving to pref
    private SharedPreferences preferences;
    private BookingTime pickup, dropOff;



    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentHomeBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_home,
                container,
                false
        );

        // get a reference to viewmodels  for the fragment
        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);


        // get a reference to binding
        this.fragmentHomeBinding = binding;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pickup = new BookingTime();
        dropOff = new BookingTime();
        dropOff.setTimeToSixHoursPlus();


            // if its a configuration change then extract the data

        if (preferences.contains(KEY_PICKUP_LOCATION_NAME)){
            pickupLocation = preferences.getString(KEY_PICKUP_LOCATION_NAME,"Select pickup location");
        }

        if (savedInstanceState != null ){
            Log.d(TAG, "onViewCreated: configuration change occured");
            if (savedInstanceState.containsKey(KEY_PICKUP_LOCATION_NAME)){
                pickupLocation = savedInstanceState.getString(KEY_PICKUP_LOCATION_NAME); // get the pickup location
            }
            if (savedInstanceState.containsKey(KEY_DROPOFF_DETAILS)){
                dropOff = (BookingTime) savedInstanceState.getSerializable(KEY_DROPOFF_DETAILS);
            }
            if (savedInstanceState.containsKey(KEY_PICKUP_DETAILS)){
                pickup = (BookingTime) savedInstanceState.getSerializable(KEY_PICKUP_DETAILS);
            }
    }


            if (!viewModel.getAvailableBikes().hasObservers()){
                // has no observers
                //TODO change the implementation to get avaialble bikes for a particular location
                viewModel.getAvailableBikes().observe(this, bikes -> {
                    // request available bikes for selected pickup location
                    this.availableBikes = bikes;
                    //TODO investigate if this is best practise or should I get a reference to adapter during first initialization
                    fragmentHomeBinding.recyclerviewFragmentMain.getAdapter().notifyDataSetChanged(); // notify the adapter
                });
            }

            // set text for pickup location textview
            fragmentHomeBinding.userPickupText.setText(pickupLocation);

            // set time for pickup time textview
            updateTime(fragmentHomeBinding.fromTime, pickup.getHourOfDay(),pickup.getMinute());

            // set time for dropoff time textview
            updateTime(fragmentHomeBinding.tillTime,dropOff.getHourOfDay(),dropOff.getMinute());

            // set date for pickup date textview
            updateDate(fragmentHomeBinding.fromDate,pickup.getDayOfMonth(),pickup.getMonth());

            // set date for dropoff date textview
            updateDate(fragmentHomeBinding.tillDate,dropOff.getDayOfMonth(),dropOff.getMonth());


        // open the pickupactivity on click
            fragmentHomeBinding.userPickupText.setOnClickListener(e->{
                Log.d(TAG, "onViewCreated: userPickUpText clicked");
                Intent intent =new Intent(getActivity(),PickupLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PICKUP_LOCATION);
            });


            //TODO set the min allowed and max allowed date to today and 3 days ahead for future booking
            fragmentHomeBinding.fromCard.setOnClickListener(e-> {
                showDatePickerDialog("From", REQUEST_CODE_FROM_DATE, -1,-1);
            });

            // TODO set the max allowed date to 3 plus the pickup date
            // TODO set the min allowed date to the pickup date
            // TODO disable this view until pickup is not set
            fragmentHomeBinding.tillCard.setOnClickListener(e->{
                showDatePickerDialog("Till", REQUEST_CODE_TILL_DATE,-1,-1);
            });

            // setup recycler view
            fragmentHomeBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());


            // TODO adjust the layoutmanager to scale down all the elements except the first one
            Pickerlayoutmanager pickerLayoutManager = new Pickerlayoutmanager(getActivity(), Pickerlayoutmanager.HORIZONTAL, false);
            pickerLayoutManager.setChangeAlpha(true);
            pickerLayoutManager.setScaleDownBy(0.4f);
            pickerLayoutManager.setScaleDownDistance(1.0f);


            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(fragmentHomeBinding.recyclerviewFragmentMain);

            fragmentHomeBinding.recyclerviewFragmentMain.setLayoutManager(
                    pickerLayoutManager
            );



            fragmentHomeBinding.userPickupText.setText(pickupLocation); // set it to textview

    }

    /**
     * called after the user selects the pickup location from PickupLocation activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case REQUEST_CODE_PICKUP_LOCATION:
                    // get the pickuplocation passed by the activity
                pickupLocation = data.getStringExtra(PickupLocationActivity.EXTRA_SELECTED_LOCATION);
                fragmentHomeBinding.userPickupText.setText(pickupLocation); // set the text
                saveLocationPref(pickupLocation); // save to shared pref
                break;
            case REQUEST_CODE_FROM_DATE:
                setDateFor(pickup,data,fragmentHomeBinding.fromDate);
                showTimePickerDialog("From", REQUEST_CODE_FROM_TIME);
                break;
            case REQUEST_CODE_TILL_DATE:
                setDateFor(dropOff,data,fragmentHomeBinding.tillDate);
                showTimePickerDialog("Till", REQUEST_CODE_TILL_TIME);
                break;
            case REQUEST_CODE_FROM_TIME:
                setTimeFor(pickup,data,fragmentHomeBinding.fromTime);
                break;
            case REQUEST_CODE_TILL_TIME:
                setTimeFor(dropOff,data,fragmentHomeBinding.tillTime);
                break;
        }
    }

    /**
     * called after user selects date and is passed to activityresult method
     * set date selected by user in textview provided
     * @param booking time object
     * @param data intent passed to activityresult
     * @param view textview to set date to
     */
    private void setDateFor(BookingTime booking, Intent data , TextView view){
        int dayOfMonth = data.getIntExtra(DatePickerFragment.EXTRA_DAY_OF_MONTH,-1);
        int month = data.getIntExtra(DatePickerFragment.EXTRA_MONTH,-1);
        booking.setDayOfMonth(dayOfMonth);
        booking.setMonth(month);
        updateDate(view, dayOfMonth,month);
    }


    /**
     * called after user selects time from dialog and is passed to activityresult method
     * set time selected by user in textview provided
     * @param booking time object
     * @param data intent passed to activityrexult method
     * @param view textview to set text to
     */
    private void setTimeFor(BookingTime booking , Intent data, TextView view){
        int hourOfDay = data.getIntExtra(TimePickerFragment.EXTRA_HOUR_OF_DAY,-1);
        int minute = data.getIntExtra(TimePickerFragment.EXTRA_MINUTE,-1);
        booking.setHourOfDay(hourOfDay);
        booking.setMinute(minute);
        updateTime(view, hourOfDay,minute);
    }

    /**
     * set time to textview provided
     * @param view textview
     * @param
     */
    @SuppressLint("SimpleDateFormat")
    private void updateTime(TextView view, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        view.setText(new SimpleDateFormat("hh a").format(calendar.getTime()));
    }

    /**
     * set date to textview provided
     * @param view textvoew
     * @param
     */
    @SuppressLint("SimpleDateFormat")
    private void updateDate(TextView view, int dayOfMonth, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,month);
        view.setText(new SimpleDateFormat("MMM dd").format(calendar.getTime()));
    }


    /**
     * show time picker dialog
     * @param title title for dialog
     * @param requestCode used in activity result
     */
    private void showTimePickerDialog(String title,int requestCode){
        if ( getFragmentManager().findFragmentByTag(TimePickerFragment.TAG) == null){
            TimePickerFragment fragment = TimePickerFragment.getInstance(title);
            fragment.setTargetFragment(this,requestCode);
            fragment.show(getFragmentManager(),TimePickerFragment.TAG);
        }else {
            Log.d(TAG, "showTimePickerDialog: Dialog exists in fragment manager");
        }
    }

    /**
     *
     * @param title used to pass this to time picker dialog
     * @param requestCode used in activity result
     */
    private void showDatePickerDialog(String title,int requestCode,int minAllowedDate, int maxAllowedDate){
       if (getFragmentManager().findFragmentByTag(DatePickerFragment.TAG) == null){
           DatePickerFragment datePickerFragment =
                   DatePickerFragment.getInstance(minAllowedDate,maxAllowedDate,title);
           datePickerFragment.setTargetFragment(this,requestCode);
           datePickerFragment.show(getFragmentManager(),DatePickerFragment.TAG);
       }else {
           Log.d(TAG, "showDatePickerDialog: Dialog exists in fragment manager");
       }
    }

    /**
     * used to save pickuplocation to sharedpref
     * @param pickupLocation
     */
    private void saveLocationPref(String pickupLocation){
        editor.putString(KEY_PICKUP_LOCATION_NAME,pickupLocation);
        editor.apply();
    }


    /**
     * lifecycle call
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_PICKUP_LOCATION_NAME,pickupLocation);
        outState.putSerializable(KEY_PICKUP_DETAILS,pickup);
        outState.putSerializable(KEY_DROPOFF_DETAILS,dropOff);
        super.onSaveInstanceState(outState);
    }


    /**
     * Recyclerview adapter for bikes available in region
     */

    class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeHolder>{

        private static final String TAG = "BikeAdapter";

        public BikeAdapter() {

        }

        @NonNull
        @Override
        public BikeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ListItemBikeBinding bikeBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.getContext()),
                    R.layout.list_item_bike,
                    viewGroup,
                    false
            );
            return new BikeHolder(bikeBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull BikeHolder bikeHolder, int i) {
            bikeHolder.bindView(availableBikes.get(i));
        }

        @Override
        public int getItemCount() {
            return availableBikes == null ? 0 : availableBikes.size();
        }



        /**
         * Holder for bikes
         */
        class BikeHolder extends RecyclerView.ViewHolder{
            // Binding of the List Item used by recycler view
            ListItemBikeBinding bikeBinding;

            public BikeHolder(ListItemBikeBinding bikeBinding){
                super(bikeBinding.getRoot());
                this.bikeBinding = bikeBinding;
                //TODO impelement selection logic and highlight the selected accordingly

            }

            /**
             *
             * @param b - bike object that should be binded to the recycler view
             */

            public void bindView(Bike b){
                bikeBinding.setBike(b);
                bikeBinding.executePendingBindings();
            }
        }
    }


}
