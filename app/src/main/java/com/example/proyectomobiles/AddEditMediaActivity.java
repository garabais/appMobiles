package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Date;

public class AddEditMediaActivity extends AppCompatActivity implements Handler.Callback{

    private final static int GET_MEDIA = 0;
    private final static int ADD_MEDIA = 1;
    private final static int UPDATE_MEDIA = 2;

    private EditText nameV, descV, urlV;
    private Button b;

    private DatePicker dateV;
    private String uid, media;
    private int mid;
    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_media);
        Intent i = getIntent();
        uid = i.getStringExtra("UID");
        media = i.getStringExtra("MEDIA");
        mid = i.getIntExtra("MEDIA_ID", -1);

        nameV = findViewById(R.id.addEditName);
        descV = findViewById(R.id.addEditDesc);
        urlV = findViewById(R.id.addEditURL);
        dateV = findViewById(R.id.addEditDate);
        b = findViewById(R.id.addEditButton);

        h = new Handler(this);

        if (mid != -1) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com")
                    .appendPath(media)
                    .appendPath(mid + "");
            String url = builder.build().toString();
            Log.d("RESPTEST", "handleMessage: " + url);
            Request.get(h, GET_MEDIA, url).start();

            b.setText("UPDATE");
        }



    }

    public void addEdit(View v) {
        String name = nameV.getText().toString();
        String desc = descV.getText().toString();
        String imgUrl = urlV.getText().toString();


        if (name.isEmpty() || desc.isEmpty() || imgUrl.isEmpty()) {
            Toast.makeText(this, "Fill al the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format("%s-%s-%s", dateV.getYear(), dateV.getMonth() + 1, dateV.getDayOfMonth());
        Log.d("DATETEST", "addEdit: " + date);

        JSONObject data = new JSONObject();
        try {

            data.put("name", name);
            data.put("description", desc);
            data.put("imageURL", imgUrl);
            data.put("releaseDate", date);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mid == -1) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com").appendPath(media);
            String url = builder.build().toString();
            Log.d("RESPTEST", "handleMessage: " + url);
            Request.post(h, ADD_MEDIA, url, data).start();


        } else {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com")
                    .appendPath(media)
                    .appendPath(mid + "");
            String url = builder.build().toString();
            Log.d("RESPTEST", "handleMessage: " + url);
            Request.put(h, UPDATE_MEDIA, url, data).start();


        }
    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;


        if (r.requestCode == ADD_MEDIA) {
            if (r.responseCode == HttpURLConnection.HTTP_CREATED) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + r.data, Toast.LENGTH_SHORT).show();
            }

        } else if (r.requestCode == GET_MEDIA) {
            if (r.responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject o = new JSONObject(r.data);
                    nameV.setText(o.getString("name"));
                    descV.setText(o.getString("description"));
                    urlV.setText(o.getString("imageURL"));

                    String tempDate = o.getString("releaseDate");
                    String[] dateHour = tempDate.split("T");
                    Log.d("RESPTEST", "handleMessage: " + dateHour[0]);
                    String[] date = dateHour[0].split("-");
                    dateV.updateDate(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (r.requestCode == UPDATE_MEDIA) {
            if (r.responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + r.data, Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }
}