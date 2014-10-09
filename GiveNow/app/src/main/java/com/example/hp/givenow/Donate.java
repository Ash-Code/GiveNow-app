package com.example.hp.givenow;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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
import java.util.Timer;
import java.util.TimerTask;


public class Donate extends Activity {
    EditText message;
    NumberPicker picker;
    Timer timer;
    MyTimerTask myTimerTask;
    TextView tvIsConnected;
    Button donateB;
    Context mcontext;

    static final String KEY_AMOUNT = "donation";
    static final String KEY_MESSAGE = "message";
    static final String KEY_MESSAGE_REC="message_rec";
    static final String KEY_AMOUNT_REC="donation_rec";
    static final String KEY_SUCCESS = "ans";
    static int mid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        picker = (NumberPicker) findViewById(R.id.numberPicker);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        picker.setMaxValue(1000);
        picker.setMinValue(1);
        message = (EditText) findViewById(R.id.message);
        donateB = (Button) findViewById(R.id.donateB);
        initialize();


    }

    public void initialize() {
        mcontext = this.getApplicationContext();
        if (isConnected()) {
            tvIsConnected.setBackgroundColor(0x7CFC0000);
            tvIsConnected.setText("You are connected");
        } else {
            tvIsConnected.setBackgroundColor(0xCC663300);
            tvIsConnected.setText("You are NOT connected");
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 5000, 20000);

        donateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donate();
            }
        });

    }


    public void donate() {
        JSONObject obj = new JSONObject();
        int amount = picker.getValue();
        String messageS = message.getText().toString();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = pref.getString(Register.KEY_USERNAME, "-1");

        try {

            obj.put(KEY_AMOUNT, amount);
            obj.put(KEY_MESSAGE, messageS);
            obj.put(Register.KEY_USERNAME, username);

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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
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
            Log.d("DONATION FINAL RESULT", result);

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
            Log.d("DONATION POSTING RESPONSE", (httpResponse == null ? "null" : "notNull"));


            inputStream = httpResponse.getEntity().getContent();
            result = convertInputStreamToString(inputStream);
            JSONObject obj = null;

            if (!result.equals("")) {
                Log.d("DONATION JSON RESULT", result);
                obj = new JSONObject(result);
            } else
                return "Donation failed.";

            success = obj.getBoolean(KEY_SUCCESS);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        if (success) {
            return "Donation registered! we'll get back to you soon";
        } else {
            return "Donation registeration failed. Please try again";
        }
    }


    class MyTimerTask extends TimerTask {

        @Override
        public void run() {


            String url = "http://codeslayers.freevar.com/da_vive/wallet.php";
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String username = pref.getString(Register.KEY_USERNAME, "-1");
            JSONObject obj = new JSONObject();
            JSONObject obj2 = null;
            InputStream inputStream = null;
            String result = "";
            try {


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                obj.put(Register.KEY_USERNAME, username);
                String json = obj.toString();
                Log.d("TIMER POSTING JSON SENT",json);
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Log.d("TIMER POSTING RESPONSE", (httpResponse == null ? "null" : "notNull"));
                inputStream = httpResponse.getEntity().getContent();
                result = convertInputStreamToString(inputStream);

                if (!result.equals("")) {
                    Log.d("TIMER POSTING JSON RESULT", result);
                    obj2 = new JSONObject(result);
                } else
                    Log.d("TIMER POSTING SUCCESS?", "Unable to create JSON");


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            if (obj2 != null)
                notifier(obj2);


        }

    }

    public void notifier(JSONObject obj) {
        if (obj == null)
            return;
        String message = "unable to recover message";
        String amount = "-1";
        try {
            String temp = (String) obj.get(KEY_MESSAGE_REC);
            if (!(temp == null) && !temp.equals(""))
                message = temp;
            amount = obj.getString(KEY_AMOUNT_REC);
        } catch (Exception e) {

        }
        Intent i = new Intent(mcontext, Forward.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(KEY_AMOUNT, amount);
        i.putExtra(KEY_MESSAGE, message);
        Log.d("NOTIFIER",amount+" "+message);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(),
                0,
                i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("rs."+amount + " received!")
                        .setContentText(message).setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mid++, mBuilder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.donate, menu);
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
