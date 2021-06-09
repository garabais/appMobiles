package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MediaSearchActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private final static int GET_MEDIA = 0;

    private RadioGroup rg;
    private Handler h;
    private ArrayList<Media> media;
    private MediaAdapter mAdapter;
    private RecyclerView rv;
    private EditText text;
    private String uid, mediaType;
    private boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search);
        Intent i = getIntent();
        uid = i.getStringExtra("UID");
        user = i.getBooleanExtra("USER", false);

        rg = findViewById(R.id.mediaSearchGroup);
        text = findViewById(R.id.mediaSearchText);
        rv = findViewById(R.id.mediaSearchRecycler);

        h = new Handler(this);

        media = new ArrayList<>();
        mAdapter = new MediaAdapter(media,this);

        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void search(View v) {
        String query = text.getText().toString();
        detectMedia();
        if (query.isEmpty()){
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com").appendPath(mediaType);

            String url = builder.build().toString();

            Request.get(h, GET_MEDIA, url).start();
        } else {
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com").appendPath(mediaType)
                    .appendQueryParameter("name", query);

            String url = builder.build().toString();

            Request.get(h, GET_MEDIA, url).start();
        }
    }


    public void detectMedia() {
        int curr = rg.getCheckedRadioButtonId();

        if (curr == R.id.mediaSearchMovies) {
            mediaType = "movies";
        } else if (curr == R.id.mediaSearchGames) {
            mediaType = "games";
        } else {
            mediaType = "shows";
        }
    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {

        RequestResponse r = (RequestResponse) message.obj;
        if (r.requestCode == GET_MEDIA) {
            if (r.responseCode == HttpURLConnection.HTTP_OK) {

                try {
                    Log.d("RESPONCETEST", "handleMessage: start");
                    JSONArray a = new JSONArray(r.data);
                    Log.d("RESPONCETEST", "handleMessage: array");
                    if (a.length() == 0){
                        Toast.makeText(this, "Nothing found", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("RESPONCETEST", "handleMessage: " + a.length());
                    media.clear();
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject ob = a.getJSONObject(i);
                        media.add(new Media(ob.getInt("id"), ob.getString("name"), ob.getString("description"), ob.getString("imageURL"), ob.getString("releaseDate"),ob.getDouble("score")));
                    }

                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int pos = rv.getChildLayoutPosition(view);

        Intent i = new Intent(this, InfoElementActivity.class);
        i.putExtra("userID", uid);
        i.putExtra("elementType", mediaType);
        i.putExtra("elementID", media.get(pos).getId());

        startActivity(i);

    }
}