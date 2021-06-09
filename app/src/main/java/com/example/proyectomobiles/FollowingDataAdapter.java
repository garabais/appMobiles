package com.example.proyectomobiles;

import android.content.Intent;
import android.util.Log;
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
    private String uid;

    public FollowingDataAdapter(ArrayList<JSONObject> fdata){
        this.fdata = fdata;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        Log.d("JSONcheck", fdata.get(position).toString());
        try {
            holder.username.setText(fdata.get(position).getString("followingName"));
            holder.elementname.setText(fdata.get(position).getString("name"));
            holder.score.setText(fdata.get(position).getString("score"));

            holder.elementname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), InfoElementActivity.class);

                    try {
                        i.putExtra("userID", uid);
                        i.putExtra("elementType", fdata.get(position).getString("type"));
                        i.putExtra("elementID", fdata.get(position).getInt("id"));
                        view.getContext().startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Intent i = new Intent(view.getContext(), UserActivity.class);
                        i.putExtra("userID", uid);
                        i.putExtra("otherUserID", fdata.get(position).getString("followingUid"));
                        view.getContext().startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            Log.d("fdata", fdata.get(position).getString("followingName"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return fdata.size(); }

    public class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView username, elementname, score;

        public FollowingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userFollowingRowName);
            elementname = itemView.findViewById(R.id.elementFollowingRowName);
            score = itemView.findViewById(R.id.elementScoreFollowingRow);
        }
    }

}

