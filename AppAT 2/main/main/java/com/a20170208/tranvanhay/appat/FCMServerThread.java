package com.a20170208.tranvanhay.appat;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hay Tran on 22/04/2017.
 */

public class FCMServerThread extends Thread {
    String name;
    public FCMServerThread(String name) {
        this.name = name;
    }
    private static final String TAG = FCMServerThread.class.getSimpleName();
    @Override
    public void run() {
        Log.d(TAG,"Starting send to FCM Server with message: " + this.name);
        // Declaration of Message Parameters
        String message_url = "https://fcm.googleapis.com/fcm/send";
        String message_sender_id = "/topics/news";
        String message_key = "key=AAAARWpK7RI:APA91bF4y6jbEOKk_8Doh9hu2pq6GFmpTZ4_PFnRkcPyH3v30uEhcgn-IUZGsE7aHbo1vuFYuamzJEjm14Mc3SrlrVGszVueyQiI0AZTdvYB5DSiWNtMH1zBu2_OfxDVqwZW7jauehvg";
        try {
            URL object=new URL(message_url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            // Generating a Header for HTTP post
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", message_key);
            con.setRequestMethod("POST");
            // Generating a JSONObject for the content of the message
            JSONObject message = new JSONObject();
            JSONObject protocol = new JSONObject();
            try {
                message.put("title","From My App AT");
                message.put("body",this.name);
                message.put("sound","default");
                protocol.put("to", message_sender_id);
                protocol.put("notification", message);
            } catch (JSONException e) {
                Log.d(TAG,"Exception catched:" + e.toString());
            }
            Log.d(TAG,"Preparing finished! Start to write output stream. ");
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(protocol.toString());
            wr.flush();
            wr.close();
            Log.d(TAG,"Writing output stream finished! Starting get data from InputStream");
            // Display what returns the POST request
            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.d(TAG,"Response from FCM Server: " + sb.toString());
            } else {
                Log.d(TAG,"Response from FCM Server: " + con.getResponseMessage());
            }
        } catch (IOException e){
            Log.d(TAG,"Exception catched:" + e.toString());
        }
        Log.d(TAG,"Send to FCM finish!");
    }
}