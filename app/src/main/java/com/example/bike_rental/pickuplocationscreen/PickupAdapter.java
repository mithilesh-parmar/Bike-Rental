package com.example.bike_rental.pickuplocationscreen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bike_rental.R;
import com.example.bike_rental.databinding.ListItemPickupBinding;

import java.util.ArrayList;
import java.util.List;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.PickupHolder> {


    private static final String TAG = "PickupAdapter";
    private List<String> items = new ArrayList<>(); // items for recycler view
    private Callback callback; // callback interface

    /**
     * indicates if the city is selected or not
     * if not then calls void onPickupLocationSelected(String pickupLocation) method of callback interface
     * else calls void onCitySelected(String city) method of callback interface
     */
    private boolean isCitySelected ;

    public PickupAdapter(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public PickupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListItemPickupBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.list_item_pickup,
                viewGroup,
                false
        );
        return new PickupHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupHolder pickupHolder, int i) {
        pickupHolder.bindView(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items == null? 0 : items.size() ;
    }

    // sets the boolean variable
    public void setCitySelected(boolean citySelected) {
        isCitySelected = citySelected;
    }

    // sets the items to be displayed in recyclerview
    public void setItems(List<String> items) {
        this.items.clear();
        this.items = items;
    }

    class PickupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ListItemPickupBinding binding ;

        public PickupHolder(@NonNull ListItemPickupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);
        }

        public void bindView(String location){
            binding.setLocation(location);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            if (!isCitySelected){ // if city is not selected yet
                isCitySelected = true;
                Log.d(TAG, "onClick: city selected "+items.get(getAdapterPosition()));
                callback.onCitySelected(items.get(getAdapterPosition())); // handled by activity
            }else { // if city is already selected then the selected item is a pickup location
                Log.d(TAG, "onClick: pickup location selected");
              callback.onPickupLocationSelected(items.get(getAdapterPosition())); // handled by activity
            }
        }
    }

    /**
     * callback for activity to handle
     */
    public interface Callback{
        void onCitySelected(String city);
        void onPickupLocationSelected(String pickupLocation);
    }
}
