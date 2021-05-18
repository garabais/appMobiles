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
    private JSONObject jsonUserInfo;
    private Spinner scoreSpinner;
    private String[] scores = {"0","1","2","3","4","5","6","7","8","9","10"};
    private ImageView img;
    private static final int GET_INFO = 5;
    private static final int GET_USER_INFO = 6;
    private static final int DELETE_ELEMENT = 7;
    private static final int UPDATE_ELEMENT = 8;
    private String uid,typeElement,userURL, currentScore;
    Handler handler;
    private int currScore, elementID;
    private boolean canChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_element);
        elementName = findViewById(R.id.elementName);
        scoreAvgText = findViewById(R.id.ScoreAvgText);
        descriptionText = findViewById(R.id.descriptionText);
        dateText = findViewById(R.id.dateText);
        deleteElement = findViewById(R.id.borrarElemento);
        img = findViewById(R.id.imgElement);
        scoreSpinner = findViewById(R.id.scoreSpinner);
        Intent i = getIntent();
        uid = i.getStringExtra("userID");
        elementID = i.getIntExtra("elementID", -1);
        typeElement = i.getStringExtra("elementType");

        canChange = false;
        handler = new Handler(this);
        scoreSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scores));
        scoreSpinner.setSelection(1);
        scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (canChange){
                    String cal = (String) adapterView.getItemAtPosition(i);

                    try {
                        updateCategory(Integer.valueOf(cal));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        currScore = -1;


    }
    @Override
    protected void onStart(){
        super.onStart();
        if (elementID == -1) {
            finish();
        }
        String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;
        userURL = "https://dogetoing.herokuapp.com/users/" + uid + "/" + typeElement + "/" + elementID;
        Request.get(this.handler,GET_INFO,elementInfoURL).start();
        Request.get(this.handler,GET_USER_INFO,userURL).start();

    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;
        Log.d("HANDLER", "handleMessage: " + r.requestCode);
        Log.d("HANDLER", "handleMessage: " + r.responseCode);
        Log.d("HANDLER", "handleMessage: " + r.data);


        if(r.requestCode==GET_INFO){
            if (r.responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject jsonINFO = new JSONObject(r.data);
                    elementName.setText(jsonINFO.getString("name"));
                    scoreAvgText.setText(String.format("%.2f", jsonINFO.getDouble("score")));
                    String[] partsDate = jsonINFO.getString("releaseDate").split("T");
                    dateText.setText(partsDate[0]);
                    descriptionText.setText(jsonINFO.getString("description"));


//                    try {
//                        InputStream is = (InputStream) new URL(jsonINFO.getString("imageURL")).getContent();
//                        Drawable d = Drawable.createFromStream(is, "src name");
//                        img.setImageDrawable(d);
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(),"ERROR DESCARGANDO LA IMAGEN",Toast.LENGTH_SHORT).show();
//                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(),"Error al obtener la info",Toast.LENGTH_SHORT).show();
            }

        } else if (r.requestCode==DELETE_ELEMENT){
            if (r.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                Toast.makeText(getApplicationContext(),"Elemento eliminado de la colección del usuario",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"No se ha podido eliminar el elemento de la colección del usuario",Toast.LENGTH_SHORT).show();
            }

        } else if (r.requestCode == UPDATE_ELEMENT) {
            if (r.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                Toast.makeText(getApplicationContext(),"Elemento actualizado de la colección del usuario",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"No se ha podido actualizar el elemento de la colección del usuario",Toast.LENGTH_SHORT).show();
            }
        } else if (r.requestCode==GET_USER_INFO){
            if (r.responseCode == HttpURLConnection.HTTP_OK){
                try {
                    jsonUserInfo = new JSONObject(r.data);
                    currScore = jsonUserInfo.getInt("score");

                    scoreSpinner.setSelection(currScore);

                    canChange = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return true;
    }

    public void deleteElement(View v){
        Request.delete(InfoElementActivity.this.handler,DELETE_ELEMENT,userURL).start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateCategory(int s) throws JSONException {
        if(s != currScore){
            JSONObject jsonScore = new JSONObject();
            jsonScore.put("score", s);

            Request.put(this.handler,UPDATE_ELEMENT,userURL,jsonScore).start();
        }
    }
}