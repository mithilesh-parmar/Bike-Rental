package com.example.bike_rental.pickuplocationscreen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bike_rental.R;
import com.example.bike_rental.databinding.ActivityPickupLocationBinding;
import com.example.bike_rental.databinding.ListItemPickupBinding;

//TODO add pickuplocation for city currenlty the user is in
public class PickupLocationActivity extends AppCompatActivity {

    // pickuplocations
    String[] pickupLocations = {"Jecrc University","Poornima College","UDML college","Subodh College"};

    public static final String EXTRA_SELECTED_LOCATION = "PICKUP_LOCATION_SELECTED";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPickupLocationBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_pickup_location);

        setSupportActionBar(binding.toolbarPickupLocationActivity);

        binding.recyclerviewActivityPickupLocation.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewActivityPickupLocation.setAdapter(new PickupAdapter());
        binding.recyclerviewActivityPickupLocation.setHasFixedSize(true);


        binding.recyclerviewActivityPickupLocation.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview,menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * recycylerview and viewholder
     */
    class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.PickupHolder>{

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
            pickupHolder.bindView(pickupLocations[i]);
        }

        @Override
        public int getItemCount() {
            return pickupLocations.length;
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
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECTED_LOCATION,pickupLocations[getAdapterPosition()]);
                setResult(RESULT_OK,intent);
                finish();
            }
        }

    }
}
