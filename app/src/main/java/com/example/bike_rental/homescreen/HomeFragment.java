package com.example.bike_rental.homescreen;

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
import android.text.format.Time;
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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    private static final String TAG = "HomeFragment";
    private List<Bike> availableBikes = new ArrayList<>(); // available bikes
    private FragmentHomeBinding fragmentHomeBinding; // Binding for the layout inflated for the fragment
    private HomeFragmentViewModel viewModel; // view Model for this fragment
    private final static int PICKUP_LOCATION_REQUESTCODE = 2314; // requestcode
    private SharedPreferences.Editor editor; // editor instance for saving to prefd
    private static final String KEY_PICKUP_LOCATION_NAME = "Pickup location name "; // key for pref
    private String pickupLocation  = "Select pickup location";
    private BookingTime pickup, dropOff;
    private  DatePickerFragment fromDatePicker,tillDatePicker;
    private static final int FROM_DATE_REQUEST_CODE = 941;
    private static final int FROM_TIME_REQUEST_CODE = 142;
    private static final int TILL_DATE_REQUEST_CODE = 435;
    private static final int TILL_TIME_REQUEST_CODE = 441;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
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


        // if its a configuration change then extract the data
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PICKUP_LOCATION_NAME)){
            pickupLocation = savedInstanceState.getString(KEY_PICKUP_LOCATION_NAME); // get the pickup location
        }

        pickup = new BookingTime();
        dropOff = new BookingTime();

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

        // open the pickupactivity on click
        fragmentHomeBinding.userPickupText.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: userPickUpText clicked");
            Intent intent =new Intent(getActivity(),PickupLocationActivity.class);
            startActivityForResult(intent,PICKUP_LOCATION_REQUESTCODE);
        });

        //TODO show calendar view
        fragmentHomeBinding.fromCard.setOnClickListener(e->{
            fromDatePicker= DatePickerFragment.getInstance(-1,-1,"From");
            fromDatePicker.setTargetFragment(this,FROM_DATE_REQUEST_CODE);
            fromDatePicker.show(getFragmentManager(),"DatePickerFragment");
        });

        fragmentHomeBinding.tillCard.setOnClickListener(e->{
            tillDatePicker = DatePickerFragment.getInstance(-1,-1,"Till");
            tillDatePicker.setTargetFragment(this,TILL_DATE_REQUEST_CODE);
            tillDatePicker.show(getFragmentManager(),"DatePickerFragment");
        });

        // setup recycler view
        fragmentHomeBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());


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
            case PICKUP_LOCATION_REQUESTCODE:
                    // get the piockuplocation passed by the activity
                pickupLocation = data.getStringExtra(PickupLocationActivity.EXTRA_SELECTED_LOCATION);
                fragmentHomeBinding.userPickupText.setText(pickupLocation); // set the text
                savePref(pickupLocation); // save to shared pref
                break;
            case FROM_DATE_REQUEST_CODE:
                setDateFor(pickup,data,fragmentHomeBinding.fromDate);
                showTimePickerDialog("From",FROM_TIME_REQUEST_CODE);
                break;
            case TILL_DATE_REQUEST_CODE:
                setDateFor(dropOff,data,fragmentHomeBinding.tillDate);
                showTimePickerDialog("Till",TILL_TIME_REQUEST_CODE);
                break;
            case FROM_TIME_REQUEST_CODE:
                setTimeFor(pickup,data,fragmentHomeBinding.fromTime);
                break;
            case TILL_TIME_REQUEST_CODE:
                setTimeFor(dropOff,data,fragmentHomeBinding.tillTime);
                break;
        }
    }

    private void setDateFor(BookingTime booking, Intent data , TextView view){
        int dayOfMonth = data.getIntExtra(DatePickerFragment.EXTRA_DAY_OF_MONTH,-1);
        int month = data.getIntExtra(DatePickerFragment.EXTRA_MONTH,-1);
        booking.setDayOfMonth(dayOfMonth);
        booking.setMonth(month);
        updateDate(view,getMonthName(month) + ", " +String.valueOf(dayOfMonth));
    }

    private void setTimeFor(BookingTime booking , Intent data, TextView view){
        int hourOfDay = data.getIntExtra(TimePickerFragment.EXTRA_HOUR_OF_DAY,-1);
        int minute = data.getIntExtra(TimePickerFragment.EXTRA_MINUTE,-1);
        booking.setHourOfDay(hourOfDay);
        booking.setMinute(minute);
        updateTime(view, getFormattedTime(hourOfDay,minute));
    }

    private void updateTime(TextView view, String time){
        view.setText(time);
    }

    private void updateDate(TextView view,String date){
        view.setText(date);
    }

    private String getMonthName(int month){
        switch (month){
            case 1 : return "Jan";
            case 2 : return "Feb";
            case 3 : return "Mar";
            case 4 : return "Apr";
            case 5 : return "May";
            case 6 : return "Jun";
            case 7 : return "Jul";
            case 8 : return "Aug";
            case 9 : return "Sep";
            case 10 : return "Oct";
            case 11 : return "Nov";
            case 12 : return "Dec";
        }
        return "";
    }

    //TODO setup time and date formatter

    private String getFormattedTime(int hourOfDay, int minute){
        return ";";
    }

    private void showTimePickerDialog(String title,int requestCode){
        TimePickerFragment fragment = TimePickerFragment.getInstance(title);
        fragment.setTargetFragment(this,requestCode);
        fragment.show(getFragmentManager(),TimePickerFragment.TAG);
    }

    /**
     * used to save pickuplocation to sharedpref
     * @param pickupLocation
     */
    private void savePref(String pickupLocation){
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
