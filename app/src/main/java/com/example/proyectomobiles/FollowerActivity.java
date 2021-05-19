package com.example.proyectomobiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FollowerActivity extends AppCompatActivity implements Handler.Callback {


    private static final int GET_USERS = 1;

    private RecyclerView usersFound;
    private String userID;
    private ArrayList<UserData> users;
    private Handler h;
    private FollowerAdapter fAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        usersFound = findViewById(R.id.userFollowRecycler);

        Intent i = getIntent();
        userID = i.getStringExtra("UID");
        users = new ArrayList<>();

        fAdapter = new FollowerAdapter(users, userID);
        usersFound.setAdapter(fAdapter);
        usersFound.setLayoutManager(new LinearLayoutManager(this));

        h = new Handler(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = String.format("https://dogetoing.herokuapp.com/users/%s/follows", userID);

        Request.get(h, GET_USERS, url).start();
    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        Log.d("RESREQ", "handleMessage: " + r.requestCode);
        Log.d("RESREQ", "handleMessage: " + r.responseCode);
        Log.d("RESREQ", "handleMessage: " + r.data);

        if (r.requestCode == GET_USERS) {

            if (r.responseCode == HttpURLConnection.HTTP_OK) {

                try {
                    JSONArray d = new JSONArray(r.data);

                    users.clear();

                    for (int i = 0; i < d.length(); i++) {
                        JSONObject uJson = d.getJSONObject(i);
                        String uid = uJson.getString("followUid");
                        String name = uJson.getString("followName");
                        users.add(new UserData(name, uid));
                    }

                    fAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }
}
