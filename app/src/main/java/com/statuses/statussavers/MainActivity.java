package com.statuses.statussavers;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.storage.StorageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Dialog myDialog;
    MaxAdView maxAdView;
    private MaxInterstitialAd interstitialAd;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;
    public static boolean adxIntitalised = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        //mainAdView = (AdView) findViewById(R.id.mainAdView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        tabs.setupWithViewPager(viewPager);
        //setbannerAd();
//        initialiseAd();
        setupBottomBar();
        //setApplovin();
        setupAdx();
        updatePopupData();
    }

    private void setupAdx() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                MainActivity.adxIntitalised = true;
                setupAdxAd();
            }
        });
    }

    private void setupAdxAd() {
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(this,"/7047,22946396544/apl/mix2ad3223/com.statuses.statussavers/interstitialinapp", adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        // The mAdManagerInterstitialAd reference will be null until
                        // an ad is loaded.
                        mAdManagerInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mAdManagerInterstitialAd = null;
                    }
                });
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
                loadAppLovinAd();
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
            //initialiseAd();
        } else if (MainActivity.count == 7) {
            //showInterstitialAd();
            //showApplovinInterstitialAd();
            showAdxInterstitial();
        } else if (MainActivity.count == 3) {
            showPopup();
        }
    }

    private void showApplovinInterstitialAd() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        }
    }

    private void showAdxInterstitial() {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd.show(MainActivity.this);
        }
    }

    private void initialiseAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-4746738763099699/2967106703", adRequest, new InterstitialAdLoadCallback() {
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