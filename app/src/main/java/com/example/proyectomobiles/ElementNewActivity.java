package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class ElementNewActivity extends AppCompatActivity {

    private EditText nombreElemento;
    private Spinner spinner;
    private Integer[] valores = {0,1,2,3,4,5,6,7,8,9,10};
    private RadioGroup opciones;
    private DatabaseReference mDatabase;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_new);
        nombreElemento = findViewById(R.id.nombreElemento);
        spinner = findViewById(R.id.spinner);
        opciones = findViewById(R.id.opcionesElemento);

        spinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, valores));


        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();

        uid = i.getStringExtra("UID");

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

    public void guardar(View v) {
        if (opciones.getCheckedRadioButtonId() != -1 && nombreElemento.getText().toString().isEmpty()){
            Toast.makeText(this, "Please insert values", Toast.LENGTH_LONG);
        } else {
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

            mDatabase.child("users").child(uid).child(categoria).child(nombreElemento.getText().toString().toLowerCase()).child("score").setValue(spinner.getSelectedItem().toString());
//            DatabaseReference db = mDatabase.child("users").child(uid).child(categoria).push();
//            db.child("name").setValue(nombreElemento.getText().toString().toLowerCase());
//            db.child("score").setValue(spinner.getSelectedItem().toString());
            limpiarFormato(v);
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
            Intent i = new Intent();
            setResult(Activity.RESULT_OK,i);
            finish();
        }

    }
}