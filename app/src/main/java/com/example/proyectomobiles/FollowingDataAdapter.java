package com.example.proyectomobiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowingDataAdapter extends RecyclerView.Adapter<FollowingDataAdapter.FollowingViewHolder> {

    private ArrayList<JSONObject> fdata;

    public FollowingDataAdapter(ArrayList<JSONObject> fdata){
        this.fdata = fdata;
    }

    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.following_data_row, parent, false);

        FollowingViewHolder fvh = new FollowingViewHolder(v);
        return  fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        try {
            holder.username.setText(fdata.get(position).getString("user"));
            holder.elementname.setText(fdata.get(position).getString("name"));
            holder.score.setText(fdata.get(position).getString("score"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return 0; }

    public class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView username, elementname, score;

        public FollowingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.elementFollowingRowName);
            elementname = itemView.findViewById(R.id.elementFollowingRowName);
            score = itemView.findViewById(R.id.elementScoreFollowingRow);
        }
    }

}

