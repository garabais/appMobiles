package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class CollectionUser extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private static final int UPDATE_LIST = 1;
    private Spinner spinner;
    private RecyclerView recycler;
    private String category, uid;
    private Handler h;
    private ArrayList<Media> data;
    private MediaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_user);

        spinner = findViewById(R.id.spinnerCategoriaCol);
        recycler = findViewById(R.id.recyclerCollection);

        h = new Handler(this);

        Intent i = getIntent();
        uid = i.getStringExtra("UID");

        data = new ArrayList<>();

        mAdapter = new MediaAdapter(data, this);
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        String[] categorias = {"Pelicula", "Juego", "Serie"};

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                CollectionUser.this.setCategory((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCategory("Peliculas");
    }

    void setCategory(String cat) {
        switch (cat){
            case "Peliculas":
                category = "movies";
                break;
            case "Juego":
                category = "games";
                break;
            case "Serie":
                category = "shows";
                break;
            default:
                return;
        }

        String url = String.format("https://dogetoing.herokuapp.com/users/%s/%s", uid, category);

        Request.get(h, UPDATE_LIST, url).start();

    }


    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {

        RequestResponse r = (RequestResponse) message.obj;



        if(r.requestCode == UPDATE_LIST) {
            if (r.responseCode == HttpURLConnection.HTTP_OK) {
                try {

                    JSONArray a = new JSONArray(r.data);

                    if (a.length() == 0){
                        Toast.makeText(this, "No tienes ningun review, agrega uno!", Toast.LENGTH_SHORT).show();
                    }
                    data.clear();
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject ob = a.getJSONObject(i);
                        data.add(new Media(ob.getInt("id"), ob.getString("name"), ob.getString("description"), ob.getString("imageURL"), ob.getString("releaseDate"),ob.getDouble("score")));
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
        int pos = recycler.getChildLayoutPosition(view);



        Intent i = new Intent(this, InfoElementActivity.class);
        i.putExtra("userID", uid);
        i.putExtra("elementType", category);
        i.putExtra("elementID", data.get(pos).getId());

        startActivity(i);
    }
}