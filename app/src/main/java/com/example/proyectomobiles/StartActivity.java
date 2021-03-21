package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent i = new Intent(this, LandingPageActivity.class);
            Toast.makeText(getApplicationContext(),"Inicio de sesión exitoso!",Toast.LENGTH_SHORT).show();
            startActivity(i);
        } else{

        }

    }

    public void openLogin(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }

    public void openRegister(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
}