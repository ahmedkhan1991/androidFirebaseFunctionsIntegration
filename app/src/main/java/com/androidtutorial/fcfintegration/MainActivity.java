package com.androidtutorial.fcfintegration;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText employeeNameET, employeeSalaryET, employeeAgeET;
    Button submitButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        employeeNameET = (EditText) findViewById(R.id.employeeName);
        employeeSalaryET = (EditText) findViewById(R.id.employeeSalary);
        employeeAgeET = (EditText) findViewById(R.id.employeeAge);

        submitButton = (Button) findViewById(R.id.submitId);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HTTPPostRequest().execute();
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private class HTTPPostRequest extends AsyncTask<Void, Void, Void> {

        HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JsonObject postData = new JsonObject();
                postData.addProperty("employeeName", employeeNameET.getText().toString());
                postData.addProperty("employeeSalary", employeeSalaryET.getText().toString());
                postData.addProperty("employeeAge", employeeAgeET.getText().toString());

                URL url = new URL("https://us-central1-awstest-4f3c2.cloudfunctions.net/employee/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }


    }
}
