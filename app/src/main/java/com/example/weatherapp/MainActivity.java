package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result;
    private TextView feels_like;
    private TextView description;
    private TextView max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result = findViewById(R.id.result);
        feels_like = findViewById(R.id.feels_like);
        description = findViewById(R.id.description);
        max = findViewById(R.id.max);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input,Toast.LENGTH_LONG).show();
                else{
                    String city = user_field.getText().toString();
                    String key ="08d0589adf72d0913fd05767c2cce2df";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" +key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });

    }

    private class GetURLData extends AsyncTask <String, String, String>{


        protected void onPreExecute() {
            super.onPreExecute();
            result.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection=null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings [0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) !=null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection !=null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result_info) {
            super.onPostExecute(result_info);

            try {
                JSONObject jsonObject= new JSONObject(result_info);
                result.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp"));
                feels_like.setText("Ощущается как: " + jsonObject.getJSONObject("main").getDouble("feels_like"));
                description.setText("минимальная температура: " + jsonObject.getJSONObject("main").getDouble("temp_min"));
                max.setText("максимальная температура: " + jsonObject.getJSONObject("main").getDouble("temp_max"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}