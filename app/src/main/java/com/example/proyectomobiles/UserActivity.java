package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements Handler.Callback {

    private TextView numberFollowers, usernameText;
    private RecyclerView rvVideojuegos, rvPeliculas, rvSeries;
    private MediaAdapter rvAdapterGames, rvAdapterMovies, rvAdapterShows;
    private String userID, otherUserID;
    private static final int GET_USERNAME = 2;
    private static final int GET_FOLLOWERS = 3;
    private static final int GET_MOVIES = 11;
    private static final int GET_VIDEOGAMES = 12;
    private static final int GET_SHOWS = 13;

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

        handler = new Handler(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        String UsernameURL = "https://dogetoing.herokuapp.com/users/" + otherUserID;
        Request.get(this.handler,GET_USERNAME,UsernameURL).start();

        String FollowersURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/following";
        Request.get(this.handler,GET_FOLLOWERS,FollowersURL).start();

        String MoviesURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/movies";
        Request.get(this.handler,GET_MOVIES,MoviesURL).start();
        String VideogamesURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/games";
        Request.get(this.handler,GET_VIDEOGAMES,VideogamesURL).start();
        String ShowsURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/shows";
        Request.get(this.handler,GET_SHOWS,ShowsURL).start();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        if (r.responseCode == HttpURLConnection.HTTP_OK) {
            if(r.requestCode==GET_USERNAME){
                try {
                    JSONObject jsonUser = new JSONObject(r.data);
                    Log.d("TESTNAME",r.data);
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
            } else if (r.requestCode==GET_MOVIES){
                try {
                    JSONArray jsonMovies = new JSONArray(r.data);
                    List<Media> listMovies = new ArrayList<Media>();
                    for(int i =0;i<jsonMovies.length();i++){
                        Media tmp = new Media(jsonMovies.getJSONObject(i).getInt("id"),jsonMovies.getJSONObject(i).getString("name"),jsonMovies.getJSONObject(i).getString("description"),jsonMovies.getJSONObject(i).getString("imageURL"),jsonMovies.getJSONObject(i).getString("releaseDate"),jsonMovies.getJSONObject(i).getDouble("score"));
                        listMovies.add(tmp);
                    }
                    rvAdapterMovies = new MediaAdapter(listMovies, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            
                        }
                    });
                    rvPeliculas.setAdapter(rvAdapterMovies);
                    rvPeliculas.setLayoutManager(new LinearLayoutManager(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (r.requestCode==GET_VIDEOGAMES){
                try {
                    JSONArray jsonGames = new JSONArray(r.data);
                    List<Media> listGames = new ArrayList<Media>();
                    for(int i =0;i<jsonGames.length();i++){
                        Media tmp = new Media(jsonGames.getJSONObject(i).getInt("id"),jsonGames.getJSONObject(i).getString("name"),jsonGames.getJSONObject(i).getString("description"),jsonGames.getJSONObject(i).getString("imageURL"),jsonGames.getJSONObject(i).getString("releaseDate"),jsonGames.getJSONObject(i).getDouble("score"));
                        listGames.add(tmp);
                    }
                    rvAdapterGames = new MediaAdapter(listGames, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    rvVideojuegos.setAdapter(rvAdapterMovies);
                    rvVideojuegos.setLayoutManager(new LinearLayoutManager(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (r.requestCode==GET_SHOWS){
                try {
                    JSONArray jsonShows = new JSONArray(r.data);
                    List<Media> listShows = new ArrayList<Media>();
                    for(int i =0;i<jsonShows.length();i++){
                        Media tmp = new Media(jsonShows.getJSONObject(i).getInt("id"),jsonShows.getJSONObject(i).getString("name"),jsonShows.getJSONObject(i).getString("description"),jsonShows.getJSONObject(i).getString("imageURL"),jsonShows.getJSONObject(i).getString("releaseDate"),jsonShows.getJSONObject(i).getDouble("score"));
                        listShows.add(tmp);
                    }
                    rvAdapterShows = new MediaAdapter(listShows, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    rvSeries.setAdapter(rvAdapterMovies);
                    rvSeries.setLayoutManager(new LinearLayoutManager(this));
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