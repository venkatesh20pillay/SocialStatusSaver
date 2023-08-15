package com.statuses.statussavers;
import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;

public class AppLovinOpenManager implements LifecycleObserver, MaxAdListener {
    private final MaxAppOpenAd appLovinOpenAd;
    private final Context context;

    private final String ADS_UNIT = "259e3b5b492bbc98";

    public AppLovinOpenManager(final Context context) {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        this.context = context;

        appLovinOpenAd = new MaxAppOpenAd(ADS_UNIT, context);
        appLovinOpenAd.setListener(this);
        appLovinOpenAd.loadAd();
    }

    private void showAdIfReady() {
        if (appLovinOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized()) return;

        if (appLovinOpenAd.isReady()) {
            appLovinOpenAd.showAd(ADS_UNIT);
        } else {
            appLovinOpenAd.loadAd();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        showAdIfReady();
    }

    @Override
    public void onAdLoaded(MaxAd maxAd) {

    }

    @Override
    public void onAdDisplayed(MaxAd maxAd) {

    }

    @Override
    public void onAdHidden(MaxAd maxAd) {
        appLovinOpenAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd maxAd) {

    }

    @Override
    public void onAdLoadFailed(String s, MaxError maxError) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
        appLovinOpenAd.loadAd();
    }
}