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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private TextView numberFollowers, usernameText;
    private Button seguirButton;
    private RecyclerView rvVideojuegos, rvPeliculas, rvSeries;
    private MediaAdapter rvAdapterGames, rvAdapterMovies, rvAdapterShows;
    private String userID, otherUserID;
    private static final int GET_USERNAME = 2;
    private static final int GET_FOLLOWERS = 3;
    private static final int GET_MOVIES = 11;
    private static final int GET_VIDEOGAMES = 12;
    private static final int GET_SHOWS = 13;
    private static final int ADD_FOLLOW = 1;
    private static final int IS_FOLLOWING = 23;

    private static final String ADD_FOLLOW_URL_TEMPLATE = "https://dogetoing.herokuapp.com/users/%s/follows";
    private String addFollowUrl;

    private MediaAdapter aGames, aMovies, aShows;
    private ArrayList<Media> lGames, lMovies, lShows;


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
        seguirButton = findViewById(R.id.botonSeguir);

        Intent i = getIntent();

        userID = i.getStringExtra("userID");
        otherUserID = i.getStringExtra("otherUserID");

        addFollowUrl = String.format(ADD_FOLLOW_URL_TEMPLATE, userID);

        handler = new Handler(this);

        lGames = new ArrayList<>();
        lMovies = new ArrayList<>();
        lShows = new ArrayList<>();

        aGames = new MediaAdapter(lGames, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = rvVideojuegos.getChildLayoutPosition(view);

                Intent i = new Intent(UserActivity.this, InfoElementActivity.class);
                i.putExtra("userID", userID);
                i.putExtra("elementType", "games");
                i.putExtra("elementID", lGames.get(pos).getId());

                startActivity(i);
            }
        });

        aMovies = new MediaAdapter(lMovies, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = rvPeliculas.getChildLayoutPosition(view);

                Intent i = new Intent(UserActivity.this, InfoElementActivity.class);
                i.putExtra("userID", userID);
                i.putExtra("elementType", "movies");
                i.putExtra("elementID", lMovies.get(pos).getId());

                startActivity(i);
            }
        });

        aShows = new MediaAdapter(lShows, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = rvSeries.getChildLayoutPosition(view);

                Intent i = new Intent(UserActivity.this, InfoElementActivity.class);
                i.putExtra("userID", userID);
                i.putExtra("elementType", "shows");
                i.putExtra("elementID", lShows.get(pos).getId());

                startActivity(i);
            }
        });

        rvVideojuegos.setAdapter(aGames);
        rvVideojuegos.setLayoutManager(new LinearLayoutManager(this));
        rvPeliculas.setAdapter(aMovies);
        rvPeliculas.setLayoutManager(new LinearLayoutManager(this));
        rvSeries.setAdapter(aShows);
        rvSeries.setLayoutManager(new LinearLayoutManager(this));
        //seguirButton.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String UsernameURL = "https://dogetoing.herokuapp.com/users/" + otherUserID;
        Request.get(this.handler, GET_USERNAME, UsernameURL).start();

        String FollowersURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/followers";
        Request.get(this.handler, GET_FOLLOWERS, FollowersURL).start();

        String isFollowing = "https://dogetoing.herokuapp.com/users/" + userID + "/follows/" + otherUserID;
        Log.d("RESPONCETEST", "onStart: " + isFollowing);
        Request.get(this.handler, IS_FOLLOWING, isFollowing).start();

        String MoviesURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/movies";
        Request.get(this.handler, GET_MOVIES, MoviesURL).start();
        String VideogamesURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/games";
        Request.get(this.handler, GET_VIDEOGAMES, VideogamesURL).start();
        String ShowsURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/shows";
        Request.get(this.handler, GET_SHOWS, ShowsURL).start();

        lGames.clear();
        lMovies.clear();
        lShows.clear();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {

        RequestResponse r = (RequestResponse) message.obj;
        Log.d("RESPONCETEST", "handleMessage: " + r.requestCode);
        Log.d("RESPONCETEST", "handleMessage: " + r.responseCode);
        Log.d("RESPONCETEST", "handleMessage: " + r.data);
        if (r.responseCode == HttpURLConnection.HTTP_OK) {
            if (r.requestCode == GET_USERNAME) {
                try {
                    JSONObject jsonUser = new JSONObject(r.data);
                    Log.d("TESTNAME", r.data);
                    usernameText.setText(jsonUser.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (r.requestCode == GET_FOLLOWERS) {
                try {
                    JSONArray jsonFollowers = new JSONArray(r.data);
                    Log.wtf("NAME", r.data);
                    numberFollowers.setText(String.valueOf(jsonFollowers.length()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (r.requestCode == GET_MOVIES) {
                try {
                    JSONArray jsonMovies = new JSONArray(r.data);

                    for (int i = 0; i < jsonMovies.length(); i++) {
                        Media tmp = new Media(jsonMovies.getJSONObject(i).getInt("id"), jsonMovies.getJSONObject(i).getString("name"), jsonMovies.getJSONObject(i).getString("description"), jsonMovies.getJSONObject(i).getString("imageURL"), jsonMovies.getJSONObject(i).getString("releaseDate"), jsonMovies.getJSONObject(i).getDouble("score"));
                        lMovies.add(tmp);
                    }

                    aMovies.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (r.requestCode == GET_VIDEOGAMES) {
                try {
                    JSONArray jsonGames = new JSONArray(r.data);

                    for (int i = 0; i < jsonGames.length(); i++) {
                        Media tmp = new Media(jsonGames.getJSONObject(i).getInt("id"), jsonGames.getJSONObject(i).getString("name"), jsonGames.getJSONObject(i).getString("description"), jsonGames.getJSONObject(i).getString("imageURL"), jsonGames.getJSONObject(i).getString("releaseDate"), jsonGames.getJSONObject(i).getDouble("score"));
                        lGames.add(tmp);
                    }

                    aGames.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (r.requestCode == GET_SHOWS) {
                try {
                    JSONArray jsonShows = new JSONArray(r.data);

                    for (int i = 0; i < jsonShows.length(); i++) {
                        Media tmp = new Media(jsonShows.getJSONObject(i).getInt("id"), jsonShows.getJSONObject(i).getString("name"), jsonShows.getJSONObject(i).getString("description"), jsonShows.getJSONObject(i).getString("imageURL"), jsonShows.getJSONObject(i).getString("releaseDate"), jsonShows.getJSONObject(i).getDouble("score"));
                        lShows.add(tmp);
                    }

                    aShows.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if(r.requestCode == IS_FOLLOWING) {
                Log.d("PRUEBATESTRESP", "handleMessage: ");
                seguirButton.setVisibility(View.GONE);
            } else if(r.requestCode == ADD_FOLLOW) {
                Toast.makeText(this, String.format("Following %s", usernameText.getText().toString()), Toast.LENGTH_SHORT).show();
                String FollowersURL = "https://dogetoing.herokuapp.com/users/" + otherUserID + "/followers";
                Request.get(this.handler, GET_FOLLOWERS, FollowersURL).start();
                seguirButton.setVisibility(View.GONE);
            }

        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void follow(View v) {
        JSONObject d = new JSONObject();
        try {
            d.put("followUid", otherUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request.post(handler, ADD_FOLLOW, addFollowUrl, d).start();


        //numberFollowers.setText(String.valueOf(Integer.parseInt(numberFollowers.getText().toString()) + 1));
    }

    @Override
    public void onClick(View view) {

    }
}
