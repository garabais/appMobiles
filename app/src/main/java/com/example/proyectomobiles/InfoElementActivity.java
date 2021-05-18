package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfoElementActivity extends AppCompatActivity implements Handler.Callback {

    private TextView elementName, scoreAvgText, descriptionText, dateText;
    private Button deleteElement;
    private Spinner scoreSpinner;
    private String[] scores = {"0","1","2","3","4","5","6","7","8","9","10"};
    private ImageView img;
    private static final int GET_INFO = 5;
    private static final int DELETE_ELEMENT = 7;
    private String uid,elementID,typeElement,userURL, currentScore;
    Handler handler;

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
        scoreSpinner = findViewById(R.id.scoreSpinner);
        scoreSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scores));
        scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                InfoElementActivity.this.updateCategory((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        handler = new Handler(this);

        Intent i = getIntent();
        uid = i.getStringExtra("userID");
        elementID = i.getStringExtra("elementID");
        typeElement = i.getStringExtra("elementType");





    }
    @Override
    protected void onStart(){
        super.onStart();
        String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;
        userURL = "https://dogetoing.herokuapp.com/users/" + uid + "/" + typeElement + "/" + elementID;
        Request.get(InfoElementActivity.this.handler,GET_INFO,elementInfoURL).start();


    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        if (r.responseCode == HttpURLConnection.HTTP_OK) {
            if(r.requestCode==GET_INFO){
                try {
                    JSONObject jsonINFO = new JSONObject(r.data);
                    Log.wtf("NAME",r.data);
                    elementName.setText(jsonINFO.getString("name"));
                    scoreAvgText.setText(String.valueOf(jsonINFO.getDouble("score")));
                    String[] partsDate = jsonINFO.getString("releaseDate").split("T");
                    dateText.setText(partsDate[0]);
                    descriptionText.setText(jsonINFO.getString("description"));

                    try {
                        InputStream is = (InputStream) new URL(jsonINFO.getString("imageURL")).getContent();
                        Drawable d = Drawable.createFromStream(is, "src name");
                        img.setImageDrawable(d);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"ERROR DESCARGANDO LA IMAGEN",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (r.requestCode==DELETE_ELEMENT){
                Toast.makeText(getApplicationContext(),"Elemento eliminado de la colección del usuario",Toast.LENGTH_SHORT).show();
            }



        } else {
            Toast.makeText(getApplicationContext(),"Error al obtener la info",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void deleteElement(View v){
        Request.delete(InfoElementActivity.this.handler,DELETE_ELEMENT,userURL).start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateCategory(String cat){
        if (cat.equals("Pelicula") || cat.equals("Serie") || cat.equals("Juego")){
            String c = cat.toLowerCase();

            if (currentScore.equals(c)){
                return;
            }

            currentScore = c;

            //this.updateLists();

        }  else {
            return;
        }

    }
}