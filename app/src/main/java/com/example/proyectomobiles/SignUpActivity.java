package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
        if(email.getText().toString().isEmpty() || user.getText().toString().isEmpty() || password.getText().toString().isEmpty()
        || passwordConfirmation.getText().toString().isEmpty()){
            Toast.makeText(this, "Favor de llenar todos los campos!",Toast.LENGTH_SHORT).show();
        } else if(!password.getText().toString().equals(passwordConfirmation.getText().toString())){
            Toast.makeText(this, "Las contraseñas no coinciden!", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // Sign in is successful
                                FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getText().toString()).build();

                                usr.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("USER", "User profile updated.");
                                                    Toast.makeText(getApplicationContext(),"Registro exitoso!",Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(v.getContext(),LandingPageActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    // TODO: failed to set the display name
                                                }
                                            }
                                        });

                            } else{
                                Toast.makeText(getApplicationContext(),"Registro fallido!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }

}

