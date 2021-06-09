package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class UserSearchActivity extends AppCompatActivity implements Handler.Callback {

    private static final int GET_USERS = 1;

    private EditText searchName;
    private Button searchBtn;
    private RecyclerView usersFound;
    private String userUid;
    private ArrayList<UserData> users;
    private Handler h;
    private UserSearchAdapter uAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        searchName = findViewById(R.id.userSearchName);
        searchBtn = findViewById(R.id.userSearchButton);
        usersFound = findViewById(R.id.userFollowRecycler);

        Intent i = getIntent();
        userUid = i.getStringExtra("UID");
        users = new ArrayList<>();

        uAdapter = new UserSearchAdapter(users, userUid);
        usersFound.setAdapter(uAdapter);
        usersFound.setLayoutManager(new LinearLayoutManager(this));

        h = new Handler(this);

    }

    public void searchUsers(View v ) {
        String name = searchName.getText().toString();
        Uri.Builder builder = new Uri.Builder();
        if (!name.isEmpty()) {

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com")
                    .appendPath("users")
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("nf", userUid);

            String url = builder.build().toString();

            Request.get(h, GET_USERS, url).start();

        } else {

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com")
                    .appendPath("users")
                    .appendQueryParameter("nf", userUid);

            String url = builder.build().toString();

            Request.get(h, GET_USERS, url).start();
            //Toast.makeText(this, "Please enter a name to search", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;

        if (r.requestCode == GET_USERS) {

            if (r.responseCode == HttpURLConnection.HTTP_OK) {

                try {
                    JSONArray d = new JSONArray(r.data);

                    users.clear();

                    for (int i = 0; i < d.length(); i++) {
                        JSONObject uJson = d.getJSONObject(i);
                        String uid = uJson.getString("uid");
                        String name = uJson.getString("name");

                        if (uid.equals(userUid)){
                            continue;
                        }
                        users.add(new UserData(name, uid));
                    }

                    uAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }
}

