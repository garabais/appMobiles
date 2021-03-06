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
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LandingPageActivity extends AppCompatActivity implements Handler.Callback {

    private static final int ADD_ELEMENT = 1;
    private static final int GET_USERNAME = 2;
    private static final int GET_FOLLOWING = 3;
    private static final int GET_RANDOM_FOLLOWING_NAME = 4;
    private static final int GET_RANDOM_MOVIE = 5;
    private static final int GET_RANDOM_GAME = 6;
    private static final int GET_RANDOM_SHOW = 7;
    private static final int CHANGE_USERNAME = 8;
    private FirebaseAuth mAuth;
    private EditText userTV;

    Handler handler;

    private String username;
    private String uid;
    private DatabaseReference mDatabase;

    private List<String> names, scores;

    private RecyclerView feedRecycler;
    private ArrayList<JSONObject> feedData;
    private FollowingDataAdapter feedAdapter;

    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        userTV = findViewById(R.id.user);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(this);
        names = new ArrayList<>();
        scores = new ArrayList<>();


        feedRecycler = findViewById(R.id.followingRecycler);
        feedData = new ArrayList<JSONObject>();
        feedAdapter = new FollowingDataAdapter(feedData);
        feedRecycler.setAdapter(feedAdapter);
        feedRecycler.setLayoutManager(new LinearLayoutManager(this));

        isAdmin = false;

    }

    public void loadFollowingData(){
        String followingURL = "https://dogetoing.herokuapp.com/users/" + uid + "/feed/";
        Request.get(this.handler, GET_RANDOM_MOVIE, followingURL + "movies").start();
        Request.get(this.handler, GET_RANDOM_GAME, followingURL + "games").start();
        Request.get(this.handler, GET_RANDOM_SHOW, followingURL + "shows").start();
        Log.d("printURL", followingURL + "movies");
    }

    public String followingRandomData(){
        //Random r = new Random();
        int random = (new Random().nextInt((3 - 1) + 1) + 1);

        if(random == 1){
            return "movies";
        }else if (random == 2){
            return "games";
        }else {
            return "shows";
        }
    }

    public void logout(View v){
        mAuth.signOut();
        Intent i = new Intent(this, StartActivity.class);
        startActivityForResult(i, ADD_ELEMENT);
    }

    public void searchElement(View v){
        Intent i = new Intent(this, MediaSearchActivity.class);
        i.putExtra("UID", uid);
        startActivity(i);
    }



    @Override
    protected void onStart() {
        super.onStart();

        // test to see if user is logged in
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
        {
            // do something if its logged in (or not!)
            Log.d("USER", "display name: " + user.getDisplayName());
            Log.d("USER", "email: " + user.getEmail());


           //userTV.setText(username);
           uid = user.getUid();
           feedAdapter.setUid(uid);

           String usernameURL = "https://dogetoing.herokuapp.com/users/" + uid;

           Request.get(LandingPageActivity.this.handler,GET_USERNAME,usernameURL).start();
           feedData.clear();
           loadFollowingData();

        }
    }

    public void searchUsers(View v) {
        Intent i = new Intent(this, UserSearchActivity.class);
        i.putExtra("UID", uid);

        startActivity(i);
    }

    public void personalColletion(View v) {
//        Intent i = new Intent(this, CollectionUser.class);
//        i.putExtra("UID", uid);
//
//        startActivity(i);
        Intent i = new Intent(this, MediaSearchActivity.class);
        i.putExtra("UID", uid);
        i.putExtra("USER", true);
        startActivity(i);
    }

    public void following(View v) {
        Intent i = new Intent(this, FollowerActivity.class);
        i.putExtra("UID", uid);

        startActivity(i);
    }

    public void adminPanel(View v) {
        if (isAdmin) {
            Intent i = new Intent(this, AdminPanelActivity.class);
            i.putExtra("UID", uid);

            startActivity(i);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        Log.d("defineR", r.data);
        Log.d("defineR", r.requestCode + "");
        Log.d("defineR", r.responseCode + "");
        if (r.requestCode==GET_USERNAME) {
            if(r.responseCode == HttpURLConnection.HTTP_OK){
                try {
                    JSONObject jsonUser = new JSONObject(r.data);
                    Log.wtf("NAME",r.data);
                    userTV.setText(jsonUser.getString("name"));
                    isAdmin = jsonUser.getBoolean("is_admin");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Error al obtener el username",Toast.LENGTH_SHORT).show();
            }

        }else if(r.requestCode == GET_RANDOM_GAME || r.requestCode == GET_RANDOM_MOVIE || r.requestCode == GET_RANDOM_SHOW){
            if(r.responseCode == HttpURLConnection.HTTP_OK){
//                Log.d("dataNAT", r.data);
                try {
                    JSONArray reviews = new JSONArray(r.data);
                    for(int i = 0; i < reviews.length(); i ++){
                        JSONObject o = reviews.getJSONObject(i);
                        if (r.requestCode == GET_RANDOM_MOVIE) {
                            o.put("type","movies");
                        } else if(r.requestCode == GET_RANDOM_GAME){
                            o.put("type","games");
                        } else {
                            o.put("type","shows");
                        }
                        feedData.add(o);
                    }
                    feedAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if(r.requestCode==CHANGE_USERNAME){
            //Toast.makeText(getApplicationContext(),"Se ha actualizado el username",Toast.LENGTH_SHORT).show();
            if(r.responseCode==HttpURLConnection.HTTP_OK){
                Toast.makeText(getApplicationContext(),"Se ha actualizado el username",Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public void changeUsername(View v) throws JSONException {
        JSONObject jsonName = new JSONObject();
        jsonName.put("name",userTV.getText());
        Request.put(handler,CHANGE_USERNAME,"https://dogetoing.herokuapp.com/users/" + uid,jsonName).start();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}