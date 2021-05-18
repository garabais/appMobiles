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

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {

    private List<UserData> data;
    private String uid;

    private static final int ADD_FOLLOW = 1;

    private static final String ADD_FOLLOW_URL_TEMPLATE = "https://dogetoing.herokuapp.com/users/%s/follows";
    private String addFollowUrl;

    public UserSearchAdapter(List<UserData> data, String uid) {
        this.data = data;
        this.uid = uid;
        addFollowUrl = String.format(ADD_FOLLOW_URL_TEMPLATE, uid);
    }

    @NonNull
    @NotNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_follow_row, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), UserActivity.class);
                i.putExtra("userID", uid);
                i.putExtra("otherUserID", data.get(position).getUid());

                view.getContext().startActivity(i);
            }
        });

        holder.fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fButton.setEnabled(false);

                JSONObject d = new JSONObject();
                try {
                    d.put("followUid", data.get(position).getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Request.post(null, ADD_FOLLOW, addFollowUrl , d).start();

                Toast.makeText(view.getContext(), String.format("Following %s", data.get(position).getName()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button fButton;

        public UserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userFollowRowName);
            fButton = itemView.findViewById(R.id.userFollowRowButton);
        }
    }
}
