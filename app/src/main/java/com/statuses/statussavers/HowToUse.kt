package com.statuses.statussavers

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView

class HowToUse : AppCompatActivity() {

    private lateinit var maxAdView: MaxAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use_2)
        supportActionBar?.let {
            it.title = "How To Use"
            it.setDisplayHomeAsUpEnabled(true)
        }
        if (MainActivity.maxAdxInitialised) {
            loadAppLovinAd()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadAppLovinAd() {
        maxAdView = findViewById(R.id.maxAd)
        maxAdView.visibility = View.VISIBLE

        maxAdView.setListener(object : MaxAdViewAdListener {
            override fun onAdExpanded(maxAd: MaxAd) {}

            override fun onAdCollapsed(maxAd: MaxAd) {}

            override fun onAdLoaded(maxAd: MaxAd) {}

            override fun onAdDisplayed(maxAd: MaxAd) {}

            override fun onAdHidden(maxAd: MaxAd) {}

            override fun onAdClicked(maxAd: MaxAd) {}

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                maxAdView.loadAd()
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
        })

        maxAdView.loadAd()
    }
}
