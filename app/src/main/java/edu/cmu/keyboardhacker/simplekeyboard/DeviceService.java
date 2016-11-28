package edu.cmu.keyboardhacker.simplekeyboard;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by helunwen on 11/23/16.
 */
public class DeviceService extends AsyncTask<Void, Void, Void> {

    private String url = RestURL.deviceServiceURL;

    private HttpURLConnection httpURLConnection;

    private String deviceId;

    private String installDate;

    public DeviceService(String deviceId, String installDate) {
        this.deviceId = deviceId;
        this.installDate = installDate;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < 5; i++) {
            try {
                URL url = new URL(this.url);
                this.httpURLConnection = (HttpURLConnection) url.openConnection();
                this.httpURLConnection.setDoOutput(true);
                this.httpURLConnection.setChunkedStreamingMode(0);
                this.httpURLConnection.setRequestMethod("PUT");
                this.httpURLConnection.setRequestProperty("async", "true");
                this.httpURLConnection.setRequestProperty("crossDomain", "true");
                this.httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                this.httpURLConnection.setRequestProperty("cache-control", "no-cache");
                String parameters = String.format("deviceId=%s&installDate=%s",
                        URLEncoder.encode(this.deviceId, "UTF-8"),
                        URLEncoder.encode(this.installDate, "UTF-8"));
                OutputStream output = new BufferedOutputStream(this.httpURLConnection.getOutputStream());
                output.write(parameters.toString().getBytes());
                output.flush();
                if (this.httpURLConnection.getResponseCode() == 200) {
                    break;
                } else {
                    this.deviceId = UUID.randomUUID().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.httpURLConnection.disconnect();
            }
        }
        return null;
    }
}
