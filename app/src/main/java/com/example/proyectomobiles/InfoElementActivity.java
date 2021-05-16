package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoElementActivity extends AppCompatActivity {

    private TextView elementName, scoreAvgText, descriptionText, dateText;
    private Button deleteElement;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_element);
        elementName = findViewById(R.id.nombreElemento);
        scoreAvgText = findViewById(R.id.ScoreAvgText);
        descriptionText = findViewById(R.id.descriptionText);
        dateText = findViewById(R.id.dateText);
        deleteElement = findViewById(R.id.borrarElemento);
        img = findViewById(R.id.imgElement);




    }
}