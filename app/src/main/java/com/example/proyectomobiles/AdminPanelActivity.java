package com.example.proyectomobiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AdminPanelActivity extends AppCompatActivity {

    private RadioGroup rg;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        rg = findViewById(R.id.adminRG);
        rg.check(R.id.adminRadioPeliculas);

        Intent i  = getIntent();
        uid = i.getStringExtra("UID");
    }

    public void add(View v) {
        String m = detectMedia();

        Intent i = new Intent(this, AddEditMediaActivity.class);
        i.putExtra("UID", uid);
        i.putExtra("MEDIA", m);
        i.putExtra("ADD", true);

        startActivity(i);

    }

    public void update(View v) {
        String m = detectMedia();

        Intent i = new Intent(this, SearchEditDeleteMedia.class);
        i.putExtra("UID", uid);
        i.putExtra("MEDIA", m);
        i.putExtra("DELETE", false);

        startActivity(i);
    }

    public void delete(View v) {
        String m = detectMedia();

        Intent i = new Intent(this, SearchEditDeleteMedia.class);
        i.putExtra("UID", uid);
        i.putExtra("MEDIA", m);
        i.putExtra("DELETE", true);

        startActivity(i);
    }

    public void makeAdmin(View v) {

        Intent i = new Intent(this, MakeAdminActivity.class);
        i.putExtra("UID", uid);

        startActivity(i);
    }

    public String detectMedia() {
        int curr = rg.getCheckedRadioButtonId();

        if (curr == R.id.adminRadioPeliculas) {
            return "movies";
        } else if (curr == R.id.adminRadioJuego) {
            return "games";
        } else {
            return "shows";
        }
    }
}