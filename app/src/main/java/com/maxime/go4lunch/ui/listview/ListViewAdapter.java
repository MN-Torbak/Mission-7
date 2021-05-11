package com.maxime.go4lunch.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.ui.mapview.MapViewFragment;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

    public ListViewAdapter(List<Restaurant> pRestaurant) {
        mRestaurant = pRestaurant;
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mAddress;
        TextView mSchedule;
        //TODO: ajouter le nombre de personnes ayant choisi ce restaurant et le nombre d'étoiles du restaurant

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar_listview);
            mName = itemView.findViewById(R.id.name_listview);
            mAddress = itemView.findViewById(R.id.address_listview);
            mSchedule = itemView.findViewById(R.id.schedule_listview);
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

    @Override
    public void onBindViewHolder(@NonNull final ListViewAdapter.ListViewHolder holder, int position) {
        final Restaurant restaurant = mRestaurant.get(position);
        Glide.with(holder.mAvatar.getContext())
                .load(restaurant.getUrlAvatar())
                .into(holder.mAvatar);
        holder.mName.setText(restaurant.getName());
        holder.mAddress.setText(restaurant.getAddress());
        holder.mSchedule.setText(restaurant.getSchedule());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                b.putParcelable("restaurant", restaurant);
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
