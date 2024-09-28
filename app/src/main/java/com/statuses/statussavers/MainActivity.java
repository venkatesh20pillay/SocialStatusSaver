package com.statuses.statussavers;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.statuses.statussavers.ui.main.SectionsPagerAdapter;
import com.statuses.statussavers.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static int count = 0;
    private BottomNavigationView bottomNavigationView;
    private Dialog myDialog;
    MaxAdView maxAdView;
    private MaxInterstitialAd interstitialAd;
    private static boolean maxAdxInitialised = false;
    private TextView textView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        textView = (TextView) findViewById(R.id.adSpace);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        tabs.setupWithViewPager(viewPager);
        setupBottomBar();
        setApplovin();
        updatePopupData();
    }

    private void updatePopupData() {
        SharedPreferences sh = MainActivity.this.getSharedPreferences("POPUP", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sh.edit();
        int counter = sh.getInt("popup",0);
        if (counter >= 0) {
            counter++;
            ed.putInt("popup", counter);
            ed.apply();
        }
    }

    private void setApplovin() {
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(AppLovinSdkConfiguration appLovinSdkConfiguration) {
                MainActivity.maxAdxInitialised = true;
                if(linearLayout.getVisibility() == View.GONE) {
                    loadAppLovinAd();
                }
                loadInterstitialAd();
            }
        });
    }

    private void loadInterstitialAd() {
        interstitialAd = new MaxInterstitialAd( "af63f34ee8ec7860", this );
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd maxAd) {

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {
                interstitialAd.loadAd();
            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {
                interstitialAd.loadAd();
            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

            }
        });

        // Load the first ad
        interstitialAd.loadAd();
    }

    private void loadAppLovinAd() {
        maxAdView = (MaxAdView) findViewById(R.id.maxAd1);
        maxAdView.setVisibility(View.VISIBLE);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd maxAd) {

            }

            @Override
            public void onAdCollapsed(MaxAd maxAd) {

            }

            @Override
            public void onAdLoaded(MaxAd maxAd) {

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

            }
        });
        maxAdView.loadAd();
    }

    private void showPopup() {
        SharedPreferences sh = MainActivity.this.getSharedPreferences("POPUP", Context.MODE_PRIVATE);
        int counter = sh.getInt("popup", 0);
        if(counter < 3) {
            return;
        }
        SharedPreferences.Editor ed = sh.edit();
        ed.putInt("popup", 0);
        ed.apply();
        TextView txtClose;
        ImageView popupImage;
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.popup_layout);
        myDialog.setCanceledOnTouchOutside(false);
        txtClose = (TextView) myDialog.findViewById(R.id.close);
        popupImage = (ImageView) myDialog.findViewById(R.id.popupimage);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        popupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                String url = "https://860.game.qureka.com";
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
                }
                catch (ActivityNotFoundException e) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        });
        myDialog.show();
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

        } else if (MainActivity.count == 5) {
            showApplovinInterstitialAd();
        } else if (MainActivity.count > 5 && MainActivity.count % 4 == 0) {
            showApplovinInterstitialAd();
        }
    }

    private void showApplovinInterstitialAd() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        }
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