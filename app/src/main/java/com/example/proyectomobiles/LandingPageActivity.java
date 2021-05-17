package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


public class LandingPageActivity extends AppCompatActivity implements Handler.Callback {

    private static final int ADD_ELEMENT = 1;
    private static final int GET_USERNAME = 2;
    private FirebaseAuth mAuth;
    private TextView userTV;

    Handler handler;

    private String username;
    private String uid;

    private RecyclerView elements;
    private ElementListAdapter rvAdapter;
    private Spinner spinner;
    private String currCategory;
    private DatabaseReference mDatabase;

    private List<String> names, scores;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        userTV = findViewById(R.id.user);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        elements = findViewById(R.id.elementsList);
        spinner = findViewById(R.id.spinnerCategoria);
        handler = new Handler(this);
        currCategory = "";

        String[] categorias = {"Pelicula", "Juego", "Serie"};

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                LandingPageActivity.this.updateCategory((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        names = new ArrayList<>();
        scores = new ArrayList<>();

        rvAdapter = new ElementListAdapter(names, scores);
        elements.setAdapter(rvAdapter);
        elements.setLayoutManager(new LinearLayoutManager(this));

        for (int i = 0; i< 15; i++){
            names.add(0, "a" + i);
            scores.add(0,"" + i);
            //rvAdapter.notifyItemInserted(0);

        }

        rvAdapter.notifyDataSetChanged();

    }

    public void logout(View v){
        mAuth.signOut();
        Intent i = new Intent(this, StartActivity.class);
        startActivityForResult(i, ADD_ELEMENT);
    }

    public void registrarElemento(View v){
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

           if (currCategory.isEmpty()){
               this.updateCategory("Pelicula");
           }

        }


    }

    public void updateCategory(String cat){
        if (cat.equals("Pelicula") || cat.equals("Serie") || cat.equals("Juego")){
            String c = cat.toLowerCase();

            if (currCategory.equals(c)){
                return;
            }

            currCategory = c;

            this.updateLists();

        }  else {
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("refreshList", "onActivityResult: " + requestCode + " " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_ELEMENT) {
            //Log.d("refreshList", "onActivityResult: add");
            if (currCategory.toLowerCase().equals(data.getStringExtra(ElementNewActivity.E_CAT).toLowerCase())){
                names.add(0,  data.getStringExtra(ElementNewActivity.E_NAME).toUpperCase());
                scores.add(0,"" + data.getStringExtra(ElementNewActivity.E_SCORE).toUpperCase());
                rvAdapter.notifyItemInserted(0);
            }
            //this.updateLists();
        }
    }

    private void updateLists(){
        scores.clear();
        names.clear();
        rvAdapter.notifyDataSetChanged();

        DatabaseReference db = mDatabase.child("users").child(uid).child(currCategory);
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    for (DataSnapshot child: task.getResult().getChildren()) {
                        Log.d("firebase", String.valueOf(child.getValue()));
                        Log.d("firebase", String.valueOf(child.getKey()));
                        scores.add(0, String.valueOf(child.child("score").getValue()));
                        names.add(0, String.valueOf(child.getKey()).toUpperCase());
                        rvAdapter.notifyItemInserted(0);
                    }


                }
            }
        });
    }

    public void searchUsers(View v) {
        Intent i = new Intent(this, UserSearchActivity.class);
        i.putExtra("UID", uid);

        startActivity(i);
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        if (r.responseCode == HttpURLConnection.HTTP_OK) {
            if(r.requestCode==GET_USERNAME){
                try {
                    JSONObject jsonUser = new JSONObject(r.data);
                    Log.wtf("NAME",r.data);
                    userTV.setText(jsonUser.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{

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