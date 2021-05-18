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

    public String[] extras;

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

    public static Request delete(Handler handler, int requestCode, String url) {
        return new Request(handler ,requestCode, RequestType.DELETE, url, null);
    }
    public static Request put(Handler handler, int requestCode, String url, JSONObject data) {
        return new Request(handler, requestCode, RequestType.PUT, url, data);
    }

    @Override
    public void run() {
        super.run();

        try {

            URL address = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) address.openConnection();
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //conn.setRequestProperty("Accept","application/json");

            switch (type) {
                case GET:
                    conn.setRequestMethod("GET");
                    break;
                case POST:
                    conn.setRequestMethod("POST");
                    break;
                case DELETE:
                    conn.setRequestMethod("DELETE");
                    break;
                case PUT:
                    conn.setRequestMethod("PUT");
                    break;
            }
            if (type == RequestType.POST || type == RequestType.PUT) {

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data.toString());
                wr.flush();
            }


            // Because success isn't always represented with a code 400
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                builder.append(line);
            }


            RequestResponse r = new RequestResponse();
            r.requestCode = requestCode;
            r.data = builder.toString();
            r.responseCode = conn.getResponseCode();
            r.extras = extras;

            Message msg = new Message();
            msg.obj = r;

            if (handler != null) {
                handler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

enum RequestType {
    GET,
    POST,
    DELETE,
    PUT
}

class RequestResponse {
    int responseCode;
    int requestCode;
    String data;
    public String[] extras;

    public RequestResponse() {
    }
}
