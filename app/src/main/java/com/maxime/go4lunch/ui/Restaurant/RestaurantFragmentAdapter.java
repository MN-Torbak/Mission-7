package com.maxime.go4lunch.ui.Restaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Workmate;

import java.util.List;

public class RestaurantFragmentAdapter extends RecyclerView.Adapter<RestaurantFragmentAdapter.RestaurantActivityViewHolder> {

    public RestaurantFragmentAdapter(List<Workmate> pWorkmates) {
        mWorkmates = pWorkmates;
    }

    static class RestaurantActivityViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;

        RestaurantActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar_workmate);
            mName = itemView.findViewById(R.id.name_workmate);
        }
    }

    private List<Workmate> mWorkmates;

    @NonNull
    @Override
    public RestaurantFragmentAdapter.RestaurantActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_workmate, parent, false);
        return new RestaurantFragmentAdapter.RestaurantActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantFragmentAdapter.RestaurantActivityViewHolder holder, int position) {
        final Workmate workmate = mWorkmates.get(position);
        Glide.with(holder.mAvatar.getContext())
                .load(workmate.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mAvatar);
        holder.mName.setText(holder.mName.getContext().getString(R.string.joining, workmate.getName()));

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mWorkmates != null) {
            size = mWorkmates.size();
        }
        return size;
    }
}

