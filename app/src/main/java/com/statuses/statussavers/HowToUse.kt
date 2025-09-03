package com.statuses.statussavers

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView

class HowToUse : AppCompatActivity() {

    private lateinit var maxAdView: MaxAdView
    private lateinit var rootLayout: View
    private lateinit var scrollView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use_2)
        rootLayout = findViewById(R.id.rootLayout)
        scrollView = findViewById(R.id.scrollView)
        maxAdView = findViewById(R.id.maxAd)

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Push content below the status bar
            scrollView.updatePadding(top = 130)

            // Raise AdView above navigation bar
            maxAdView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom + 10
            }

            insets
        }
        supportActionBar?.let {
            it.title = "How To Use"
            it.setDisplayHomeAsUpEnabled(true)
        }
        if (!HelperClass.adsDisabled && MainActivity.maxAdxInitialised) {
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
