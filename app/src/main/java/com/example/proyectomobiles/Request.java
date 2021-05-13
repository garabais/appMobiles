package com.example.proyectomobiles;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request extends Thread {

    private int requestCode;
    private RequestType type;
    private String url;
    private JSONObject data;
    private Handler handler;

    private Request(Handler handler, int requestCode, RequestType type, String address, JSONObject data) {
        super();
        this.handler = handler;
        this.requestCode = requestCode;
        this.type = type;
        this.url = address;
        this.data = data;
    }

    public static Request post(Handler handler, int requestCode, String url, JSONObject data) {
        return new Request(handler, requestCode, RequestType.POST, url, data);
    }

    public static Request get(Handler handler, int requestCode, String url) {
        return new Request(handler ,requestCode, RequestType.GET, url, null);
    }

    @Override
    public void run() {
        super.run();

        Log.d("XAVITEST", "handleMessage: start");

        try {
            Log.d("XAVITEST", "handleMessage: url");
            URL address = new URL(url);
            Log.d("XAVITEST", "handleMessage: connection");
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //conn.setRequestProperty("Accept","application/json");

            if (type == RequestType.POST) {
                Log.d("XAVITEST", "handleMessage: POST");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data.toString());
                wr.flush();
            }

            Log.d("XAVITEST", "handleMessage: read");
            // Read the response with any response code
            // Because success isn't always represented with a code 400
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                builder.append(line);
            }

            Log.d("XAVITEST", "handleMessage: build");
            RequestResponse r = new RequestResponse();
            r.requestCode = requestCode;
            r.data = builder.toString();
            r.responseCode = conn.getResponseCode();

            Log.d("XAVITEST", "handleMessage: send");
            Message msg = new Message();
            msg.obj = r;
            handler.sendMessage(msg);
        } catch (Exception e) {

            Log.d("XAVITEST", "handleMessage: error" + e.toString());
            e.printStackTrace();
        }

    }
}

enum RequestType {
    GET,
    POST
}

class RequestResponse {
    int responseCode;
    int requestCode;
    String data;

    public RequestResponse() {
    }
}
