package com.statuses.statussavers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class HowToUse extends AppCompatActivity {

    private AdView howToUseAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("How To Use");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        howToUseAdView = (AdView) findViewById(R.id.howToUseAdView);
        setbannerAd();
    }

    private void setbannerAd() {
        MobileAds.initialize(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        howToUseAdView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}