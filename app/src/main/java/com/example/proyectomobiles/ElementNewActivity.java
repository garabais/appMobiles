package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ElementNewActivity extends AppCompatActivity implements View.OnClickListener, Handler.Callback {

    public static String E_NAME = "E_NAME", E_SCORE = "E_SCORE", E_CAT = "E_CAT";

    private static final int GET_ELEMENTS = 1;
    private Handler h;
    private ArrayList<String> elements;
    private ElementSearchAdapter elementSearchAdapter;
    private RecyclerView resultadoElemento;

    private EditText nombreElemento;
    private Spinner spinner;
    private Integer[] valores = {0,1,2,3,4,5,6,7,8,9,10};
    public RadioGroup opciones;
    private DatabaseReference mDatabase;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_new);
        nombreElemento = findViewById(R.id.nombreElemento);
        spinner = findViewById(R.id.spinner);
        opciones = findViewById(R.id.opcionesElemento);
        resultadoElemento = findViewById(R.id.elementRecycler);

        //ElementSearchAdapter elementSearchAdapter = new ElementSearchAdapter(elements, this);

        spinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, valores));


        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();

        uid = i.getStringExtra("UID");
        h = new Handler(this);
        elements = new ArrayList<>();
        //TODO POSSIBLE TOAST
        /*spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                Toast.makeText(adapterView.getContext(),
                        (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });*/

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

    public void searchElementButton(View v){
        String categoria = "";

        switch (opciones.getCheckedRadioButtonId()){
            case R.id.radioJuego:
                categoria = "juego";
                break;
            case R.id.radioPeli:
                categoria = "pelicula";
                break;
            case R.id.radioSerie:
                categoria = "serie";
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
                }else{
                    String name = nombreElemento.getText().toString();

                    Uri.Builder builder = new Uri.Builder();

                    builder.scheme("https")
                            .authority("dogetoing.herokuapp.com").appendPath("movies")
                            .appendQueryParameter("name", name);

                    String url = builder.build().toString();

                    Request.get(h, GET_ELEMENTS, url).start();
                }
            }
        }

    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        String respuesta = message.obj.toString();

        elements = new ArrayList<>();
        try {
            JSONArray elementosPelicula = new JSONArray(respuesta);

            for (int i = 0; i < elementosPelicula.length(); i++) {
                JSONObject tempJSON = elementosPelicula.getJSONObject(i);
                elements.add(tempJSON.getString("name"));
            }

            resultadoElemento = findViewById(R.id.elementRecycler);
            ElementSearchAdapter adapter = new ElementSearchAdapter(elements, this, "movies");
            LinearLayoutManager llm = new LinearLayoutManager(this);

            resultadoElemento.setLayoutManager(llm);
            resultadoElemento.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }

//    public void guardar(View v) {
//        if (opciones.getCheckedRadioButtonId() != -1 && nombreElemento.getText().toString().isEmpty()){
//            Toast.makeText(this, "Please insert values", Toast.LENGTH_LONG);
//        } else {
//            String categoria = "";
//            switch (opciones.getCheckedRadioButtonId()){
//                case R.id.radioJuego:
//                    categoria = "juego";
//                    break;
//                case R.id.radioPeli:
//                    categoria = "pelicula";
//                    break;
//                case R.id.radioSerie:
//                    categoria = "serie";
//                    break;
//            }
//
//            String eName = nombreElemento.getText().toString().toLowerCase();
//            String eScore = spinner.getSelectedItem().toString();
//            mDatabase.child("users").child(uid).child(categoria).child(eName).child("score").setValue(eScore);
////            DatabaseReference db = mDatabase.child("users").child(uid).child(categoria).push();
////            db.child("name").setValue(nombreElemento.getText().toString().toLowerCase());
////            db.child("score").setValue(spinner.getSelectedItem().toString());
//            limpiarFormato(v);
//            Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
//            Intent i = new Intent();
//            setResult(Activity.RESULT_OK,i);
//            i.putExtra(E_NAME, eName);
//            i.putExtra(E_SCORE, eScore);
//            i.putExtra(E_CAT, categoria);
//            finish();
//        }
//
//    }
}