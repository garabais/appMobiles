package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ElementNewActivity extends AppCompatActivity implements Handler.Callback {

    public static String E_NAME = "E_NAME", E_SCORE = "E_SCORE", E_CAT = "E_CAT";
    private String uid;
    private static final int GET_ELEMENTS = 1, REGISTER_ELEMENT = 2;
    private Handler h;
    private int selectedScore;
    private String category;

    private EditText nombreElemento;
    private String nombreE;
    private Spinner spinner;
    private Integer[] valores = {0,1,2,3,4,5,6,7,8,9,10};
    public RadioGroup opciones;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_new);
        nombreElemento = findViewById(R.id.nombreElemento);
        spinner = findViewById(R.id.spinner);
        opciones = findViewById(R.id.opcionesElemento);

        //ElementSearchAdapter elementSearchAdapter = new ElementSearchAdapter(elements, this);

        spinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, valores));
        selectedScore = -1;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();

        uid = i.getStringExtra("UID");
        h = new Handler(this);
        //TODO POSSIBLE TOAST

        }

    public void limpiarFormato(View v){
        opciones.clearCheck();
        spinner.setSelection(0);
        nombreElemento.setText("");
    }

    public void cancelar(View v){
        Intent i = new Intent();
        setResult(Activity.RESULT_CANCELED,i);
        finish();
    }

    public void onRadioButtonClicked(View view) {

    }

    public void addScoreButton(View v){
        Log.wtf("addScore", "entro a add score");
        category = "";
        String categoria = "";

        switch (opciones.getCheckedRadioButtonId()){
            case R.id.radioJuego:
                categoria = "juego";
                category = "games";
                break;
            case R.id.radioPeli:
                categoria = "pelicula";
                category = "movies";
                break;
            case R.id.radioSerie:
                categoria = "serie";
                category = "shows";
                break;
        }

        if(categoria.isEmpty()){
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show();
        }else{
            if(nombreElemento.getText().toString().trim().length() == 0){
                if(categoria.equals("serie") || categoria.equals("pelicula") ){
                    Toast.makeText(this, "Por favor, escribe la " + categoria + " que deseas buscar", Toast.LENGTH_SHORT).show();
                }else if (categoria.equals("juego")){
                    Toast.makeText(this, "Por favor, escribe el " + categoria + " que deseas buscar", Toast.LENGTH_SHORT).show();
                }
            }else{
                selectedScore = Integer.parseInt(spinner.getSelectedItem().toString());

                String name = nombreElemento.getText().toString().toLowerCase();
                nombreE = name;
                Uri.Builder builder = new Uri.Builder();

                builder.scheme("https")
                        .authority("dogetoing.herokuapp.com").appendPath(category)
                        .appendQueryParameter("name", name);

                String url = builder.build().toString();
                Log.wtf("get request", "request siuu");
                Request.get(h, GET_ELEMENTS, url).start();


            }
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        Log.wtf("Nat", "entr a handler");
        RequestResponse r = (RequestResponse) message.obj;

        if(r.requestCode == GET_ELEMENTS){
            if (r.responseCode == HttpURLConnection.HTTP_OK) {

                try {
                    JSONArray datos = new JSONArray(r.data);

                    for(int i = 0; i < datos.length(); i++){
                        JSONObject dato = datos.getJSONObject(i);
                        String datoE = dato.getString("name");
                        if(datoE.equals(nombreE)){
                            //Toast.makeText(getApplicationContext(),"Busqueda exitosa",Toast.LENGTH_SHORT).show();
                            JSONObject elemento = new JSONObject();
                            elemento.put("score", selectedScore);
                            elemento.put("id", dato.getInt("id"));

                            Uri.Builder builder = new Uri.Builder();

                            builder.scheme("https")
                                    .authority("dogetoing.herokuapp.com")
                                    .appendPath("users")
                                    .appendPath(uid)
                                    .appendPath(category);

                            String url = builder.build().toString();

                            Request.post(h, REGISTER_ELEMENT, url, elemento).start();

                            return true;
                        }
                    }
                    Toast.makeText(getApplicationContext(),"No se ha encontrado, revise nombre",Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Hubo un error en la busqueda",Toast.LENGTH_SHORT).show();
            }
        }else if(r.requestCode == REGISTER_ELEMENT){
            if(r.responseCode == HttpURLConnection.HTTP_CREATED){
                Toast.makeText(getApplicationContext(),"Se agregó la calificación",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"No se pudo agregar calificación",Toast.LENGTH_SHORT).show();
            }
        }

        return true;

    }
}