package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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