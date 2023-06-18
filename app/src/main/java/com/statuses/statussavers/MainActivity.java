package com.statuses.statussavers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.storage.StorageManager;
import android.view.Menu;
import android.view.MenuItem;

import com.statuses.statussavers.ui.main.SectionsPagerAdapter;
import com.statuses.statussavers.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AdView mainAdView;
    private InterstitialAd mInterstitialAd;
    private static int count = 0;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        mainAdView = (AdView) findViewById(R.id.mainAdView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        tabs.setupWithViewPager(viewPager);
        setbannerAd();
        initialiseAd();
        setupBottomBar();
    }

    private void setupBottomBar() {
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        return true;
                    case R.id.more:
                        openMoreActivity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home);
        MainActivity.count += 1;
        if (MainActivity.count == 2) {
            initialiseAd();
        } else if (MainActivity.count == 5) {
            showInterstitialAd();
        }
    }

    private void initialiseAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
            mInterstitialAd.show(this);
        }
    }

    private void setbannerAd() {
        MobileAds.initialize(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        mainAdView.loadAd(adRequest);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.howtouse:
                openHowToUse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openHowToUse() {
        Intent intent = new Intent(this, HowToUse.class);
        startActivity(intent);
    }

    private void openMoreActivity() {
        bottomNavigationView.setSelectedItemId(R.id.home);
        Intent intent = new Intent(this, MoreActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}