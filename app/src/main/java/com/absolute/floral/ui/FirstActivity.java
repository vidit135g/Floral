package com.absolute.floral.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.absolute.floral.R;

public class FirstActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        new Handler().postDelayed(() -> {
            SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
            if (pref.getBoolean("activity_executed", false)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            }

            finish();
        }, SPLASH_TIME);
    }
}
