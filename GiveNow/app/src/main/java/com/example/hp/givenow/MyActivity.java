package com.example.hp.givenow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MyActivity extends Activity {
    EditText tempName;
    EditText tempPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        tempName = (EditText) findViewById(R.id.usernameLL);
        tempPass = (EditText) findViewById(R.id.passwordLL);
        Button register = (Button) findViewById(R.id.regiLB);
        Button login = (Button) findViewById(R.id.loginLB);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String pass = pref.getString(Register.KEY_PASSWORD, "DEFAULT");
                String username = pref.getString(Register.KEY_USERNAME, "DEFAULT");


                if (tempName.getText().toString().equals(username) && tempPass.getText().toString().equals(pass)) {
                    Intent i = new Intent(getBaseContext(), Donate.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), "Login failed"+" "+username + " " + pass, Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), Register.class);
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
