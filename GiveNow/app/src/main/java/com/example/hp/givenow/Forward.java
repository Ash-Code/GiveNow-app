package com.example.hp.givenow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Forward extends Activity {
    static final String KEY_SUCCESS = "ans";
    EditText message;
    TextView amount;
    Button forward;
    Button cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        amount = (TextView) findViewById(R.id.amountF);
        message = (EditText) findViewById(R.id.messageF);
        forward = (Button) findViewById(R.id.forward);
        cancel = (Button) findViewById(R.id.cancel);
        String am = bundle.getString(Donate.KEY_AMOUNT);
        String mes = bundle.getString(Donate.KEY_MESSAGE);
        amount.setText(am);
        message.setText(mes);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    String mes = message.getText().toString();
                    String am = amount.getText().toString();
                    forward(am, mes);
                } else {
                    Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void forward(String am, String mes) {
        JSONObject obj = new JSONObject();
        String messageS = message.getText().toString();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = pref.getString(Register.KEY_USERNAME, "-1");
        String password = pref.getString(Register.KEY_PASSWORD, "-1");
        try {

            obj.put(Donate.KEY_AMOUNT, am);
            obj.put(Donate.KEY_MESSAGE, mes);
            obj.put(Register.KEY_USERNAME, username);
            obj.put(Register.KEY_PASSWORD, password);
        } catch (Exception e) {

        }
        new HttpAsyncTask().execute(new String[]{"http://codeslayers.freevar.com/da_vive/donate_req.php", obj.toString()});
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null)
            sb.append(line);

        inputStream.close();
        return sb.toString();

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("ASYNC HTTP POST", urls[1]);
            return POST(urls[0], urls[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("REGISTRATION FINAL RESULT", result);
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }


    public String POST(String url, String json) {

        boolean success = false;
        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");


            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d("REGISTRATION POSTING RESPONSE", (httpResponse == null ? "null" : "notNull"));


            inputStream = httpResponse.getEntity().getContent();
            result = convertInputStreamToString(inputStream);
            JSONObject obj = null;
            if (!result.equals("")) {
                Log.d("REGISTRATION JSON RESULT", result);
                obj = new JSONObject(result);
            } else
                return "Registration failed.";

            success = obj.getBoolean(KEY_SUCCESS);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        if (success) {
            return "Registration successful!";
        } else {
            return "Registration failed.";
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forward, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
