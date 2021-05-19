package com.example.proyectomobiles;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {

    private List<UserData> data;
    private String uid;
    private String otherUid;
    private int unfollowIndex;

    private static final int DEL_FOLLOW = 1;

    private static final String UNFOLLOW_URL_TEMPLATE = "https://dogetoing.herokuapp.com/users/%s/follows/%s";
    private String unFollowUrl;

    public FollowerAdapter(List<UserData> data, String uid) {
        this.data = data;
        this.uid = uid;
    }

    public void setUnfollowIndex(int unfollowIndex) {
        this.unfollowIndex = unfollowIndex;
    }

    @NonNull
    @NotNull
    @Override
    public FollowerAdapter.FollowerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_following_row, parent, false);
        return new FollowerAdapter.FollowerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowerAdapter.FollowerViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());

        holder.uButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject d = new JSONObject();
                try {
                    d.put("followUid", data.get(position).getUid());
                    otherUid = data.get(position).getUid();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                unFollowUrl = String.format(UNFOLLOW_URL_TEMPLATE, uid, otherUid);

                Request.delete(null, 0, unFollowUrl ).start();

                Toast.makeText(view.getContext(), String.format("Unfollowing %s", data.get(position).getName()), Toast.LENGTH_SHORT).show();

                data.remove(position);

                notifyDataSetChanged();
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), UserActivity.class);
                i.putExtra("userID", uid);
                i.putExtra("otherUserID", data.get(position).getUid() );

                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button uButton;

        public FollowerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.followingName);
            uButton = itemView.findViewById(R.id.unfollowButton);
        }
    }
}
