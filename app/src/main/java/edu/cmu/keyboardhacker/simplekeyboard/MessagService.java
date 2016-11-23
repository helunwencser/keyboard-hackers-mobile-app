package edu.cmu.keyboardhacker.simplekeyboard;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by helunwen on 11/23/16.
 */
public class MessagService extends AsyncTask<Void, Void, Void> {

    private String message;

    public MessagService(String message) {
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final String url = "http://rest-service.guides.spring.io/greeting";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Greeting greeting = restTemplate.getForObject(url, Greeting.class);
        System.out.println(String.format("Greeting %s %s\n", greeting.getId(), greeting.getContent()));
        return null;
    }

}

