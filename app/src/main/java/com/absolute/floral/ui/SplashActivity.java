package com.absolute.floral.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.absolute.floral.R;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SplashActivity extends AppCompatActivity {

    AppCompatButton btn;
    TextView prgtext;
    ImageView piximage;
    CircularProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        btn=findViewById(R.id.pixelbutton);
        prgtext=findViewById(R.id.progresstext);
        progress=findViewById(R.id.loading_spinner);
        piximage=findViewById(R.id.pixelimage);
        TextView intro=findViewById(R.id.intro);
        TextView hi=findViewById(R.id.hello);

            final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadein);
            final Animation fade = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade);
        final Animation fadef = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadefirst);
        final Animation fadebut = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadebutton);
        hi.startAnimation(fadef);
        new Handler().postDelayed(() -> {
        }, 4000);
            piximage.startAnimation(an);
        new Handler().postDelayed(() -> {
        }, 2000);
        intro.startAnimation(fade);
        btn.startAnimation(fadebut);


        btn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
                btn.setVisibility(View.GONE);
                prgtext.setVisibility(View.VISIBLE);
                progress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putBoolean("activity_executed", true);
                    edt.commit();
                    Intent i = new Intent(SplashActivity.this, LoadingActivity.class);
                    startActivity(i);
                    progress.setVisibility(View.GONE);
                    prgtext.setVisibility(View.GONE);
                }, 4000);



        });

    }




    @Override
    protected void onResume() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        super.onResume();
    }
}
