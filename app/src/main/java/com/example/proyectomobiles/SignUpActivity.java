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
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private EditText email, user, password, passwordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.email);
        user = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordConfirmation = findViewById(R.id.passwordConfirmation);
        mAuth = FirebaseAuth.getInstance();
    }

    public void registrar(View v){
        if(email.getText().toString().equals("") || user.getText().toString().equals("") || password.getText().toString().equals("")
        || passwordConfirmation.getText().toString().equals("")){
            Toast.makeText(this, "Favor de llenar todos los campos!",Toast.LENGTH_SHORT).show();
        } else{
            String passwordString = password.getText().toString();
            String passwordConfirmationString = passwordConfirmation.getText().toString();
            if(passwordConfirmationString.equals(passwordString)){
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwordString)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Registro exitoso!",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(v.getContext(),LandingPageActivity.class);
                                    startActivity(i);
                                    finish();
                                } else{
                                    Toast.makeText(getApplicationContext(),"Registro fallido!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else{
                Toast.makeText(this, "Las contraseñas no coinciden!", Toast.LENGTH_SHORT).show();
            }
        }

    }

}