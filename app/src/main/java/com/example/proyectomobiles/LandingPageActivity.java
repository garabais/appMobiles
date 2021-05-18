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
    private static final int GET_RANDOM_REVIEW = 5;
    private FirebaseAuth mAuth;
    private TextView userTV;

    Handler handler;

    private String username;
    private String uid;
    private DatabaseReference mDatabase;

    private List<String> names, scores;

    private RecyclerView feedRecycler;
    private ArrayList<JSONObject> feedData;
    private FollowingDataAdapter feedAdapter;

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

    }

    public void loadFollowingData(){
        String followingURL = "https://dogetoing.herokuapp.com/users/" + uid + "/feed/";
        Request.get(this.handler, GET_RANDOM_REVIEW, followingURL + "movies").start();
        Request.get(this.handler, GET_RANDOM_REVIEW, followingURL + "games").start();
        Request.get(this.handler, GET_RANDOM_REVIEW, followingURL + "shows").start();
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

    public void calificarElemento(View v){
        Intent i = new Intent(this, ElementNewActivity.class);
        i.putExtra("UID", uid);
        startActivityForResult(i, ADD_ELEMENT);
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

           String usernameURL = "https://dogetoing.herokuapp.com/users/" + uid;

           Request.get(LandingPageActivity.this.handler,GET_USERNAME,usernameURL).start();
           loadFollowingData();

        }
    }

    public void searchUsers(View v) {
        Intent i = new Intent(this, UserSearchActivity.class);
        i.putExtra("UID", uid);

        startActivity(i);
    }

    public void personalColletion(View v) {
        Intent i = new Intent(this, CollectionUser.class);
        i.putExtra("UID", uid);

        startActivity(i);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Error al obtener el username",Toast.LENGTH_SHORT).show();
            }

        }else if(r.requestCode == GET_RANDOM_REVIEW){
            if(r.responseCode == HttpURLConnection.HTTP_OK){
//                Log.d("dataNAT", r.data);
                try {
                    JSONArray reviews = new JSONArray(r.data);
                    for(int i = 0; i < reviews.length(); i ++){
                        feedData.add(reviews.getJSONObject(i));
                    }
                    feedAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}