package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class SignUpActivity extends AppCompatActivity implements Handler.Callback {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private EditText email, user, password, passwordConfirmation;
    private JSONObject data;
    Handler handler;

    private static final int ADD_USER = 1;
    private static final String ADD_USER_URL = "https://dogetoing.herokuapp.com/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.email);
        user = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordConfirmation = findViewById(R.id.passwordConfirmation);
        mAuth = FirebaseAuth.getInstance();
        handler = new Handler(this);
    }

    public void registrar(View v){

        v.setEnabled(false);
        if(email.getText().toString().isEmpty() || user.getText().toString().isEmpty() || password.getText().toString().isEmpty()
        || passwordConfirmation.getText().toString().isEmpty()){
            Toast.makeText(this, "Favor de llenar todos los campos!",Toast.LENGTH_SHORT).show();
        } else if(!password.getText().toString().equals(passwordConfirmation.getText().toString())){
            Toast.makeText(this, "Las contraseñas no coinciden!", Toast.LENGTH_SHORT).show();
        } else {
			v.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                // Sign in is successful
                                Log.d("XAVITEST", "handleMessage: " + "registro");
                                FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();

                                data = new JSONObject();
                                try {
                                    data.put("uid", usr.getUid());
                                    data.put("name", user.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Request.post(SignUpActivity.this.handler, ADD_USER, ADD_USER_URL, data).start();


                            } else{

                                Toast.makeText(getApplicationContext(),"Registro fallido!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;

        if (r.responseCode == HttpURLConnection.HTTP_CREATED) {
            Toast.makeText(getApplicationContext(),"Registro exitoso!",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this ,LandingPageActivity.class);
            startActivity(i);

        } else {
            Toast.makeText(getApplicationContext(),"Error en el registro, intentando de nuevo",Toast.LENGTH_SHORT).show();
            Request.post(SignUpActivity.this.handler, ADD_USER, ADD_USER_URL, data).start();
        }
        return true;
    }
}

