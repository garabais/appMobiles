package com.example.proyectomobiles;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ElementSearchAdapter extends RecyclerView.Adapter<ElementSearchAdapter.UserViewHolder> {

    private ArrayList<String> data;
    private String e_search;
    private View.OnClickListener listener;

    private static final int ADD_ELEMENT = 1;

    private static final String ADD_MOVIE_URL_TEMPLATE = "https://dogetoing.herokuapp.com/movies";
    private static final String ADD_GAME_URL_TEMPLATE = "https://dogetoing.herokuapp.com/games";
    private static final String ADD_SHOW_URL_TEMPLATE = "https://dogetoing.herokuapp.com/shows";
    private String addElementUrl;

    public ElementSearchAdapter(ArrayList<String> data, View.OnClickListener listener, String e_search) {
        this.data = data;
        this.e_search = e_search;
        addElementUrl = String.format(ADD_MOVIE_URL_TEMPLATE, e_search);
    }

    @NonNull
    @NotNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_score_row, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserViewHolder holder, int position) {
//        holder.name.setText(data.get(position).getName());

//        holder.fButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.fButton.setEnabled(false);
//
//                JSONObject d = new JSONObject();
//                try {
//                    d.put("followUid", data.get(position).getUid()); //checar
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                //Request.post(null, ADD_FOLLOW, addFollowUrl , d).start();
//
//                Toast.makeText(view.getContext(), String.format("Following %s", data.get(position).getName()), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public UserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.elementNameRow);
        }
    }
}
