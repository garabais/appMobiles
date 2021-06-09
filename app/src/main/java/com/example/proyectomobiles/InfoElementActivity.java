package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoElementActivity extends AppCompatActivity implements Handler.Callback {

    private TextView elementName, scoreAvgText, descriptionText, dateText;
    private JSONObject jsonUserInfo;
    private Spinner scoreSpinner;
    private String[] scores = {"-","0","1","2","3","4","5","6","7","8","9","10"};
    private ImageView img;
    private static final int GET_INFO = 5;
    private static final int GET_USER_MEDIA_INFO = 6;
    private static final int DELETE_ELEMENT = 7;
    private static final int UPDATE_ELEMENT = 8;
    private static final int ADD_ELEMENT = 9;
    private String uid,typeElement,userURL;
    Handler handler;
    private int currScore, elementID;
    private boolean userScored;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_element);
        elementName = findViewById(R.id.elementName);
        scoreAvgText = findViewById(R.id.ScoreAvgText);
        descriptionText = findViewById(R.id.descriptionText);
        dateText = findViewById(R.id.dateText);
        img = findViewById(R.id.imgElement);
        scoreSpinner = findViewById(R.id.scoreSpinner);
        Intent i = getIntent();
        uid = i.getStringExtra("userID");
        elementID = i.getIntExtra("elementID", -1);
        typeElement = i.getStringExtra("elementType");
        userScored = false;

        userURL = "https://dogetoing.herokuapp.com/users/" + uid + "/" + typeElement + "/" + elementID;


        handler = new Handler(this);
        scoreSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scores));
        //scoreSpinner.setSelection(1);
        currScore = 0;
        scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String cal = (String) adapterView.getItemAtPosition(i);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);


                if ( cal.equals("-")) {
                    if (userScored && currScore != 0){
                        Log.d("UPDATETEST", "onItemSelected: DELETE");
                        Request.delete(InfoElementActivity.this.handler,DELETE_ELEMENT,userURL).start();
                        currScore = 0;
                        userScored = false;
                    }

                } else if (currScore != Integer.valueOf(cal)){

                    int score = Integer.valueOf(cal);
                    JSONObject jsonScore = new JSONObject();
                    try {
                        jsonScore.put("score", score);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (userScored) {
                        Log.d("UPDATETEST", "onItemSelected: CHANGE");


                        //userURL = "https://dogetoing.herokuapp.com/users/" + uid + "/" + typeElement + "/" + ;
                        Request.put(InfoElementActivity.this.handler,UPDATE_ELEMENT,userURL,jsonScore).start();

                    } else {
                        Log.d("UPDATETEST", "onItemSelected: SCORE");

                        try {
                            jsonScore.put("id", elementID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String url = "https://dogetoing.herokuapp.com/users/" + uid + "/" + typeElement;

                        Request.post(InfoElementActivity.this.handler,ADD_ELEMENT,url,jsonScore).start();
                        userScored = true;
                    }
                    currScore = score;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    @Override
    protected void onStart(){
        super.onStart();
        if (elementID == -1) {
            finish();
        }
        String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;

        Request.get(this.handler,GET_INFO,elementInfoURL).start();
        Request.get(this.handler,GET_USER_MEDIA_INFO,userURL).start();

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
                    double s = jsonINFO.getDouble("score");
                    if (s == -1) {
                        scoreAvgText.setText("-");
                    } else {
                        scoreAvgText.setText(String.format("%.2f", s));
                    }

                    String[] partsDate = jsonINFO.getString("releaseDate").split("T");
                    dateText.setText(partsDate[0]);
                    descriptionText.setText(jsonINFO.getString("description"));
                    String imageUrl = jsonINFO.getString("imageURL");



                    new DownloadImageTask(img).execute(imageUrl);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(),"Error al obtener la info",Toast.LENGTH_SHORT).show();
            }

        } else if (r.requestCode==DELETE_ELEMENT){
            if (r.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                Toast.makeText(getApplicationContext(),"Elemento eliminado de la colección del usuario",Toast.LENGTH_SHORT).show();
                String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;
                Request.get(this.handler,GET_INFO,elementInfoURL).start();
                //finish();
            } else {
                Toast.makeText(getApplicationContext(),"No se ha podido eliminar el elemento de la colección del usuario",Toast.LENGTH_SHORT).show();
            }

        } else if (r.requestCode == UPDATE_ELEMENT) {
            if (r.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                Toast.makeText(getApplicationContext(),"Elemento actualizado de la colección del usuario",Toast.LENGTH_SHORT).show();
                String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;
                Request.get(this.handler,GET_INFO,elementInfoURL).start();
            } else {
                Toast.makeText(getApplicationContext(),"No se ha podido actualizar el elemento de la colección del usuario",Toast.LENGTH_SHORT).show();
            }
        } else if (r.requestCode==GET_USER_MEDIA_INFO) {
            if (r.responseCode == HttpURLConnection.HTTP_OK){
                try {
                    jsonUserInfo = new JSONObject(r.data);
                    currScore = jsonUserInfo.getInt("score");

                    scoreSpinner.setSelection(currScore + 1);

                    userScored = true;
                    //canChange = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else if(r.requestCode == ADD_ELEMENT) {
            if (r.responseCode == HttpURLConnection.HTTP_CREATED) {
                Toast.makeText(getApplicationContext(),"Elemento actualizado de la colección del usuario",Toast.LENGTH_SHORT).show();
                String elementInfoURL = "https://dogetoing.herokuapp.com/" + typeElement + "/" + elementID;
                Request.get(this.handler,GET_INFO,elementInfoURL).start();
                userScored = true;
            }
        }

        return true;
    }

    public void deleteElement(View v){

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

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}