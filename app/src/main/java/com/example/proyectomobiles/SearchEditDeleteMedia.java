package com.example.proyectomobiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SearchEditDeleteMedia extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private static final int GET_MEDIA = 0;
    private static final int DELETE_MEDIA = 1;

    private String uid, media;
    private EditText searchName;
    private Handler h;
    private boolean del;

    private ArrayList<String> names;
    private ArrayList<Media> m;

    private SingleElementAdapter adapter;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_edit_delete_media);

        Intent i  = getIntent();
        uid = i.getStringExtra("UID");
        media = i.getStringExtra("MEDIA");
        del = i.getBooleanExtra("DELETE", false);

        searchName = findViewById(R.id.editDeleteSearchName);
        rv = findViewById(R.id.editDeleteFollowRecycler);
        h = new Handler(this);

        names = new ArrayList<>();
        m = new ArrayList<>();
        adapter = new SingleElementAdapter(names, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void search(View v) {
        String name = searchName.getText().toString();
        if (!name.isEmpty()) {
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("https")
                    .authority("dogetoing.herokuapp.com").appendPath(media)
                    .appendQueryParameter("name", name);

            String url = builder.build().toString();

            Request.get(h, GET_MEDIA, url).start();

        } else {
            Toast.makeText(this, "Please enter a name to search", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        RequestResponse r = (RequestResponse) message.obj;

        if (r.requestCode == GET_MEDIA) {

            if (r.responseCode == HttpURLConnection.HTTP_OK) {

                try {
                    JSONArray d = new JSONArray(r.data);

                    m.clear();
                    names.clear();


                    for (int i = 0; i < d.length(); i++) {
                        JSONObject ob = d.getJSONObject(i);
                        Media curr = new Media(ob.getInt("id"), ob.getString("name"), ob.getString("description"), ob.getString("imageURL"), ob.getString("releaseDate"),ob.getDouble("score"));
                        m.add(curr);
                        names.add(curr.getName());
                    }


                    adapter.notifyDataSetChanged();

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
        if (del) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(String.format("Are you sure you want to erase %s?", m.get(pos).getName()))
                    .setTitle("Warning");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    //Request.post(h, DELETE_MEDIA, "https://dogetoing.herokuapp.com/admin", data).start();
                    Uri.Builder builder = new Uri.Builder();

                    builder.scheme("https")
                            .authority("dogetoing.herokuapp.com")
                            .appendPath(media)
                            .appendPath(m.get(pos).getId() + "");

                    String url = builder.build().toString();

                    Request.delete(h, DELETE_MEDIA, url).start();
                    Toast.makeText(SearchEditDeleteMedia.this, String.format("%s deleted", names.get(pos)), Toast.LENGTH_SHORT).show();
                    names.remove(pos);
                    m.remove(pos);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    Toast.makeText(SearchEditDeleteMedia.this, "Delete canceled", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Intent i = new Intent(this, AddEditMediaActivity.class);
            i.putExtra("UID", uid);
            i.putExtra("MEDIA", media);
            //i.putExtra("ADD", false);
            i.putExtra("MEDIA_ID", m.get(pos).getId());

            startActivity(i);
        }
    }
}