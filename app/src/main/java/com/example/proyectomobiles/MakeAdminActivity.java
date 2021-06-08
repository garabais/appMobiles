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
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MakeAdminActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private final static int GET_USERS = 0;
    private final static int MAKE_ADMIN = 1;

    private String uid;
    private EditText searchName;
    private Handler h;

    private ArrayList<String> usernames;
    private ArrayList<UserData> users;

    private SingleElementAdapter adapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_admin);

        Intent i  = getIntent();
        uid = i.getStringExtra("UID");
        searchName = findViewById(R.id.adminSearchName);
        rv = findViewById(R.id.adminFollowRecycler);
        h = new Handler(this);

        usernames = new ArrayList<>();
        users = new ArrayList<>();
        adapter = new SingleElementAdapter(usernames, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void searchUsers(View v) {

        String name = searchName.getText().toString();
        if (!name.isEmpty()) {
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com").appendPath("users")
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("admin", "false");

            String url = builder.build().toString();

            Request.get(h, GET_USERS, url).start();

        } else {
            Toast.makeText(this, "Please enter a name to search", Toast.LENGTH_SHORT).show();
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
                    usernames.clear();

                    for (int i = 0; i < d.length(); i++) {
                        JSONObject uJson = d.getJSONObject(i);
                        String id = uJson.getString("uid");
                        String name = uJson.getString("name");
                        Log.d("RESPHAN", "handleMessage: pre " + uid + " " + name);
                        if (uid.equals(id)){
                            continue;
                        }
                        Log.d("RESPHAN", "handleMessage: post" + id + " " + name);
                        users.add(new UserData(name, id));
                        usernames.add(name);
                    }
                    Log.d("RESPHAN", "handleMessage: notify" + usernames );
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int pos = rv.getChildLayoutPosition(view);

        try {
            JSONObject data = new JSONObject();
            data.put("uid", users.get(pos).getUid());

            Request.post(h, MAKE_ADMIN, "https://dogetoing.herokuapp.com/admin", data).start();

            Toast.makeText(this, String.format("%s is now an admin", usernames.get(pos)), Toast.LENGTH_SHORT).show();
            usernames.remove(pos);
            users.remove(pos);
            adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}