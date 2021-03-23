package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.view.View;


public class LandingPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userTV;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        userTV = findViewById(R.id.user);
        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void logout(View v){
        mAuth.signOut();
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
    }

    public void registrarElemento(View v){
        Intent i = new Intent(this, ElementNew.class);
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

           username = user.getDisplayName();
           userTV.setText(username);
        }


    }
}