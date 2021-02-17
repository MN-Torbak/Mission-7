package com.maxime.go4launch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maxime.go4launch.R;
import com.maxime.go4launch.model.Workmate;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    public WorkmateAdapter(List<Workmate> pWorkmates) {
        mWorkmates = pWorkmates;
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mRestaurant;

        WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar_workmate);
            mName = itemView.findViewById(R.id.name_workmate);
            mRestaurant = itemView.findViewById(R.id.restaurant_workmate);
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
        holder.mAvatar.setImageResource(R.drawable.ic_checkbox_green);
        holder.mName.setText(workmate.getName());
        holder.mRestaurant.setText(workmate.getRestaurant());
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
