package com.maxime.go4lunch.ui.workmates;

import android.graphics.Typeface;
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
import com.bumptech.glide.request.RequestOptions;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Workmate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    public WorkmateAdapter(List<Workmate> pWorkmates) {
        mWorkmates = pWorkmates;
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;

        WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar_workmate);
            mName = itemView.findViewById(R.id.name_workmate);
        }
    }

    private List<Workmate> mWorkmates;

    @NonNull
    @Override
    public WorkmateAdapter.WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_workmate, parent, false);
        return new WorkmateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkmateAdapter.WorkmateViewHolder holder, int position) {
        final Workmate workmate = mWorkmates.get(position);
        Glide.with(holder.mAvatar.getContext())
                .load(workmate.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mAvatar);
        if (workmate.getRestaurant().equals("aucun")) {
            holder.mName.setTypeface(null, Typeface.ITALIC);
            holder.mName.setText(holder.mName.getContext().getString(R.string.not_chosen, workmate.getName()));
        } else if (!getReadableDate().equals(workmate.getRestaurant_date_choice())){
            holder.mName.setTypeface(null, Typeface.ITALIC);
            holder.mName.setText(holder.mName.getContext().getString(R.string.not_chosen, workmate.getName()));
        } else {
            holder.mName.setText(holder.mName.getContext().getString(R.string.chosen, workmate.getName(), workmate.getRestaurant()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                b.putString("restaurant", workmate.getRestaurantID());
                Navigation.findNavController(view).navigate(R.id.action_navigation_workmates_to_restaurantDetailsFragment, b);

            }
        });
    }

    private String getReadableDate() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        return formatter.format(now);
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
