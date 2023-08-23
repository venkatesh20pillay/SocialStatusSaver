package com.statuses.statussavers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import static android.os.Build.VERSION.SDK_INT;

public class Splash extends AppCompatActivity {
    final static int SPLASH_TIMER = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (checkBothPermission()) {
                    intent = new Intent(Splash.this, MainActivity.class);
                } else {
                     intent = new Intent(Splash.this, StartActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);

    }

    private Boolean checkBothPermission() {
        Boolean readwrite = false;
        Boolean path = false;
        if(SDK_INT >=30) {
            path = readDataFromPrefs();
            readwrite = readPermission();
        }
        else {
            path = true;
            readwrite = readPermission();

        }
        return path && readwrite;
    }

    private Boolean readPermission() {
        if (SDK_INT <=29) {
            if (ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                return false;
            }
        } else {
            SharedPreferences sh = Splash.this.getSharedPreferences("PERMISSION", Context.MODE_PRIVATE);
            String uri;
            if (SDK_INT >= 33) {
                uri = sh.getString("readwrite33", "");
            } else {
                uri = sh.getString("readwrite", "");
            }
            if (uri != null) {
                if (uri.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    private Boolean readDataFromPrefs() {
        SharedPreferences sh = Splash.this.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
        String uri = sh.getString("PATH", "");
        if(uri!=null) {
            if (uri.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}