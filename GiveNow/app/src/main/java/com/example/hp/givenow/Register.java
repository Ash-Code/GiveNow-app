package com.example.hp.givenow;

import android.app.Activity;
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


public class Register extends Activity {

    static final String KEY_USERNAME = "loginId";
    static final String KEY_PASSWORD = "passw";
    static final String KEY_ADDRESS = "addr";
    static final String KEY_PHONE = "phno";
    static final String KEY_BAL = "wallet_bal";
    static final String KEY_NAME = "name";
    static final String KEY_SUCCESS = "ans";
    EditText name;
    EditText username;
    EditText address;
    EditText password;
    EditText bal;
    EditText phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        address = (EditText) findViewById(R.id.address);
        password = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.phone);
        bal = (EditText) findViewById(R.id.bal);
        Button regButton = (Button) findViewById(R.id.regbutton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameS = name.getText().toString();
                String usernameS = username.getText().toString();
                String addressS = address.getText().toString();
                String passwordS = password.getText().toString();
                String phoneS = phone.getText().toString();
                String balS = bal.getText().toString();
                JSONObject obj = new JSONObject();
                try {

                    obj.put(KEY_NAME, nameS);
                    obj.put(KEY_ADDRESS, addressS);
                    obj.put(KEY_PASSWORD, passwordS);
                    obj.put(KEY_USERNAME, usernameS);
                    obj.put(KEY_PHONE, phoneS);
                    obj.put(KEY_BAL, balS);


                    if (!register(obj)) {
                        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_NAME, nameS);
                        editor.putString(KEY_ADDRESS, addressS);
                        editor.putString(KEY_PASSWORD, passwordS);
                        editor.putString(KEY_USERNAME, usernameS);
                        editor.putString(KEY_BAL, balS);
                        editor.putString(KEY_PHONE, phoneS);
                        boolean result = editor.commit();
                        Toast.makeText(getBaseContext(), result + " " + pref.getString(KEY_USERNAME, "-1") + " " + pref.getString(KEY_PASSWORD, "-1"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {

                }

            }
        });


    }


    public boolean register(JSONObject obj) {

        if (isConnected()) {
            new HttpAsyncTask().execute(new String[]{"http://codeslayers.freevar.com/da_vive/reg.php", obj.toString()});
            Log.d("REGISTRATION", "connected");
            return true;
        } else {
            Toast.makeText(getBaseContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            return false;
        }
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

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            // 3. build jsonObject


            // 4. convert JSONObject to JSON to String

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d("REGISTRATION POSTING RESPONSE", (httpResponse==null?"null":"notNull"));

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            result = convertInputStreamToString(inputStream);
            JSONObject obj = null;
            // 10. convert inputstream to string
            if (!result.equals("")){
                Log.d("REGISTRATION JSON RESULT",result);
                obj = new JSONObject(result);}
            else
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
        getMenuInflater().inflate(R.menu.register, menu);
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
