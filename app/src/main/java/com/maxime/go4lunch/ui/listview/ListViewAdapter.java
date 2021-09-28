package com.maxime.go4lunch.ui.listview;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;

import java.util.Collections;
import java.util.List;

import static com.maxime.go4lunch.ui.listview.ListViewFragment.mLocation;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

    public ListViewAdapter(List<Restaurant> pRestaurant) {
        mRestaurant = pRestaurant;
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mAddress;
        TextView mSchedule;
        TextView mNumberWorkmate;
        TextView mRange;
        ImageView mOneStar;
        ImageView mTwoStar;
        ImageView mThreeStar;
        ImageView mOneStarEmpty;
        ImageView mTwoStarEmpty;
        ImageView mThreeStarEmpty;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar_listview);
            mName = itemView.findViewById(R.id.name_listview);
            mAddress = itemView.findViewById(R.id.address_listview);
            mSchedule = itemView.findViewById(R.id.schedule_listview);
            mNumberWorkmate = itemView.findViewById(R.id.number_of_workmate_here_listview);
            mRange = itemView.findViewById(R.id.range_listview);
            mOneStar = itemView.findViewById(R.id.one_star_listview);
            mTwoStar = itemView.findViewById(R.id.two_star_listview);
            mThreeStar = itemView.findViewById(R.id.three_star_listview);
            mOneStarEmpty = itemView.findViewById(R.id.one_star_empty);
            mTwoStarEmpty = itemView.findViewById(R.id.two_star_empty);
            mThreeStarEmpty = itemView.findViewById(R.id.three_star_empty);

        }
    }

    private List<Restaurant> mRestaurant;

    @NonNull
    @Override
    public ListViewAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_listview, parent, false);
        return new ListViewHolder(v);
    }

    void updateFilter(ListViewFragment.SortMethod sortingRestaurants, Location location) {
        switch (sortingRestaurants) {
            case PROXI:
                Collections.sort(mRestaurant, new Restaurant.RestaurantProxiComparator(location));
                break;
            case STARS:
                Collections.sort(mRestaurant, new Restaurant.RestaurantStarsComparator());
                break;
            case NUMBEROFWORKMATES:
                Collections.sort(mRestaurant, new Restaurant.RestaurantNumberOfWorkmatesComparator());
                break;
            case NONE:
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewAdapter.ListViewHolder holder, int position) {
        final Restaurant restaurant = mRestaurant.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(10));
        Glide.with(holder.mAvatar.getContext())
                .load(restaurant.getUrlAvatar())
                .apply(requestOptions)
                .into(holder.mAvatar);
        holder.mName.setText(restaurant.getName());
        holder.mAddress.setText(restaurant.getAddress());
        holder.mSchedule.setText(restaurant.getSchedule());
        holder.mNumberWorkmate.setText("("+ restaurant.getWorkmatesEatingHere().size()+")");

        float[] results = new float[1];
        android.location.Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), restaurant.getLatlng().latitude, restaurant.getLatlng().longitude, results);
        int distanceInMeters = (int) results[0];
        holder.mRange.setText((distanceInMeters)+"m");

        float star = restaurant.getStar();


        if (star >= 2.5 ) {
            holder.mOneStar.setVisibility(View.VISIBLE);
            holder.mTwoStar.setVisibility(View.VISIBLE);
            holder.mThreeStar.setVisibility(View.VISIBLE);
            holder.mOneStarEmpty.setVisibility(View.INVISIBLE);
            holder.mTwoStarEmpty.setVisibility(View.INVISIBLE);
            holder.mThreeStarEmpty.setVisibility(View.INVISIBLE);
        } else if (star >= 1.5) {
            holder.mOneStar.setVisibility(View.VISIBLE);
            holder.mTwoStar.setVisibility(View.VISIBLE);
            holder.mThreeStar.setVisibility(View.INVISIBLE);
            holder.mOneStarEmpty.setVisibility(View.INVISIBLE);
            holder.mTwoStarEmpty.setVisibility(View.INVISIBLE);
            holder.mThreeStarEmpty.setVisibility(View.VISIBLE);
        } else if (star >= 0.5) {
            holder.mOneStar.setVisibility(View.VISIBLE);
            holder.mTwoStar.setVisibility(View.INVISIBLE);
            holder.mThreeStar.setVisibility(View.INVISIBLE);
            holder.mOneStarEmpty.setVisibility(View.INVISIBLE);
            holder.mTwoStarEmpty.setVisibility(View.VISIBLE);
            holder.mThreeStarEmpty.setVisibility(View.VISIBLE);
        } else {
            holder.mOneStar.setVisibility(View.INVISIBLE);
            holder.mTwoStar.setVisibility(View.INVISIBLE);
            holder.mThreeStar.setVisibility(View.INVISIBLE);
            holder.mOneStarEmpty.setVisibility(View.VISIBLE);
            holder.mTwoStarEmpty.setVisibility(View.VISIBLE);
            holder.mThreeStarEmpty.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                b.putString("restaurant", restaurant.getId());
                Navigation.findNavController(view).navigate(R.id.action_navigation_list_view_to_restaurantDetailsFragment, b);

            }
        });
    }


    @Override
    public int getItemCount() {
        int size = 0;
        if (mRestaurant != null) {
            size = mRestaurant.size();
        }
        return size;
    }
}
