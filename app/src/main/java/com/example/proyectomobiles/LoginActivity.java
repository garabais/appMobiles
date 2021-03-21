package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    private EditText email, contraseña;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailLogin);
        contraseña = findViewById(R.id.contraseñaLogin);


    }



    public void Login(View v){
        mAuth.signInWithEmailAndPassword(email.getText().toString(),contraseña.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Sesión iniciada!",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(v.getContext(),LandingPageActivity.class);
                            startActivity(i);
                            finish();
                        } else{
                            Toast.makeText(getApplicationContext(),"Log In fallido!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendToRegistro(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }


}