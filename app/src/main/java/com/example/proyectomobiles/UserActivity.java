package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class UserActivity extends AppCompatActivity implements Handler.Callback {

    private TextView numberFollowers, usernameText;
    private RecyclerView rvVideojuegos, rvPeliculas, rvSeries;
    private String userID, otherUserID;
    private static final int GET_USERNAME = 2;
    private static final int GET_FOLLOWERS = 3;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        numberFollowers = findViewById(R.id.numberFollowers);
        usernameText = findViewById(R.id.userNameTxtFriend);
        rvVideojuegos = findViewById(R.id.rvVideojuegos);
        rvPeliculas = findViewById(R.id.rvPeliculas);
        rvSeries = findViewById(R.id.rvSeries);

        Intent i = getIntent();

        userID = i.getStringExtra("userID");
        otherUserID = i.getStringExtra("otherUserID");

    }

    @Override
    protected void onStart(){
        super.onStart();
        String UsernameURL = "https://dogetoing.herokuapp.com/users/" + otherUserID;
        Request.get(UserActivity.this.handler,GET_USERNAME,UsernameURL).start();

        String FollowersURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/followers";
        Request.get(UserActivity.this.handler,GET_FOLLOWERS,FollowersURL).start();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        if (r.responseCode == HttpURLConnection.HTTP_OK) {
            if(r.requestCode==GET_USERNAME){
                try {
                    JSONObject jsonUser = new JSONObject(r.data);
                    Log.wtf("NAME",r.data);
                    usernameText.setText(jsonUser.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (r.requestCode==GET_FOLLOWERS){
                try {
                    JSONArray jsonFollowers = new JSONArray(r.data);
                    Log.wtf("NAME",r.data);
                    numberFollowers.setText(String.valueOf(jsonFollowers.length()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        } else {
            Toast.makeText(getApplicationContext(),"Error al obtener el username",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}