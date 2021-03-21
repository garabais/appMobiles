package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ElementNew extends AppCompatActivity {

    private EditText puntaje, nombreElemento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_new);
        puntaje = findViewById(R.id.puntuacionElemento);
        nombreElemento = findViewById(R.id.nombreElemento);
    }

    public void limpiarFormato(View v){
        puntaje.setText("");
        nombreElemento.setText("");
    }

    public void cancelar(View v){
        Intent i = new Intent();
        setResult(Activity.RESULT_CANCELED,i);
        finish();
    }
}