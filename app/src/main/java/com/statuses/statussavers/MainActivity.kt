package com.statuses.statussavers

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.statuses.statussavers.databinding.ActivityMainBinding
import com.statuses.statussavers.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var myDialog: Dialog? = null
    var maxAdView: MaxAdView? = null
    private var interstitialAd: MaxInterstitialAd? = null
    private var adSpacetextView: TextView? = null
    private var linearLayout: LinearLayout? = null
    private var reviewManager: ReviewManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val tabs = binding!!.tabs
        ViewCompat.setOnApplyWindowInsetsListener(tabs) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding!!.appBarLayout.setPadding(0, systemBars.top + 150, 0, 0)
            binding!!.viewPager.updatePadding(bottom = systemBars.bottom + 200)
            binding!!.bottomNavigationView.updatePadding(bottom = systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = binding!!.viewPager
        viewPager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)
        maxAdView = findViewById<View>(R.id.maxAd1) as MaxAdView
        bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        adSpacetextView = findViewById<View>(R.id.adSpace) as TextView
        linearLayout = findViewById<View>(R.id.linearlayout) as LinearLayout
        setupBottomBar()
        //checkAndInitAds()
        updatePopupData()
    }

    private fun checkAndInitAds() {
        if (HelperClass.adsDisabled) {
            adSpacetextView!!.visibility = View.GONE
            maxAdView!!.visibility = View.GONE
        } else {
            setApplovin()
        }

    }

    private fun updatePopupData() {
        val sh = this@MainActivity.getSharedPreferences("POPUP", MODE_PRIVATE)
        val ed = sh.edit()
        var counter = sh.getInt("popup", 0)
        if (counter >= 0) {
            counter++
            ed.putInt("popup", counter)
            ed.apply()
        }
    }

    private fun updateReviewData() {
        val sh = this@MainActivity.getSharedPreferences("REVIEW", MODE_PRIVATE)
        val ed = sh.edit()
        var counter = sh.getInt("review", 0)
        var openPopup = false
        if (counter >= 0) {
            counter++
            if (counter % 5 == 0) {
                openPopup = true
            }
            if (counter == 5) {
                counter = 0
            }
            ed.putInt("review", counter)
            ed.apply()
        }
        if (openPopup) {
            setupReviewpopup()
        }
    }

    private fun setupReviewpopup() {
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result

                val flow = reviewManager!!.launchReviewFlow(this, reviewInfo!!)
                flow.addOnCompleteListener { task1: Task<Void?>? -> }
            } else {
                // There was some problem, log or handle the error code.
            }
        }
    }

    private fun setApplovin() {
        val initConfig = AppLovinSdkInitializationConfiguration.builder(
            "_MudM74bNJVdvK6QTPbWsZJ6vDPNKe5FewSXgwuxFv7gUivBxUg9FRqeTGZKBPZTl7byRprTRC93GbnEC9bLu5",
            this
        )
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()
        AppLovinSdk.getInstance(this).initialize(initConfig) {
            maxAdxInitialised = true
            linearLayout!!.visibility = View.GONE
            loadAppLovinAd()
            loadInterstitialAd()
        }
    }

    private fun loadInterstitialAd() {
        interstitialAd = MaxInterstitialAd("af63f34ee8ec7860", this)
        interstitialAd!!.setListener(object : MaxAdListener {
            override fun onAdLoaded(maxAd: MaxAd) {
            }

            override fun onAdDisplayed(maxAd: MaxAd) {
            }

            override fun onAdHidden(maxAd: MaxAd) {
                interstitialAd!!.loadAd()
            }

            override fun onAdClicked(maxAd: MaxAd) {
            }

            override fun onAdLoadFailed(s: String, maxError: MaxError) {
                interstitialAd!!.loadAd()
            }

            override fun onAdDisplayFailed(maxAd: MaxAd, maxError: MaxError) {
            }
        })

        // Load the first ad
        interstitialAd!!.loadAd()
    }

    private fun loadAppLovinAd() {
        adSpacetextView!!.visibility = View.GONE
        maxAdView!!.visibility = View.VISIBLE
        maxAdView!!.setListener(object : MaxAdViewAdListener {
            override fun onAdExpanded(maxAd: MaxAd) {
            }

            override fun onAdCollapsed(maxAd: MaxAd) {
            }

            override fun onAdLoaded(maxAd: MaxAd) {

            }

            override fun onAdDisplayed(maxAd: MaxAd) {
            }

            override fun onAdHidden(maxAd: MaxAd) {
            }

            override fun onAdClicked(maxAd: MaxAd) {
            }

            override fun onAdLoadFailed(s: String, maxError: MaxError) {
                maxAdView!!.loadAd()
            }

            override fun onAdDisplayFailed(maxAd: MaxAd, maxError: MaxError) {
            }
        })
        maxAdView!!.loadAd()
    }

    private fun showPopup() {
        val sh = this@MainActivity.getSharedPreferences("POPUP", MODE_PRIVATE)
        val counter = sh.getInt("popup", 0)
        if (counter < 3) {
            return
        }
        val ed = sh.edit()
        ed.putInt("popup", 0)
        ed.apply()
        myDialog = Dialog(this)
        myDialog!!.setContentView(R.layout.popup_layout)
        myDialog!!.setCanceledOnTouchOutside(false)
        val txtClose = myDialog!!.findViewById<View>(R.id.close) as TextView
        val popupImage = myDialog!!.findViewById<View>(R.id.popupimage) as ImageView
        txtClose.setOnClickListener { myDialog!!.dismiss() }
        popupImage.setOnClickListener {
            myDialog!!.dismiss()
            val url = "https://860.game.qureka.com"
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MainActivity, Uri.parse(url))
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }
        myDialog!!.show()
    }

    private fun setupBottomBar() {
        bottomNavigationView!!.selectedItemId = R.id.home
        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> return@OnNavigationItemSelectedListener true
                R.id.dm -> {
                    openDirectMessageActivity()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.more -> {
                    openMoreActivity()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Find the specific item and update its visibility
        val removeAds = menu.findItem(R.id.removeads)
        if(HelperClass.adsDisabled) {
            removeAds.isVisible = false
        } else {
            removeAds.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView!!.selectedItemId = R.id.home
        if (HelperClass.adsDisabled) {
            adSpacetextView!!.visibility = View.GONE
            maxAdView!!.visibility = View.GONE
        }
        count += 1
        if (count == 2) {
            updateReviewData()
        } else if (count == 5) {
            showApplovinInterstitialAd()
        } else if (count > 5 && count % 4 == 0) {
            showApplovinInterstitialAd()
        }
    }

    private fun showApplovinInterstitialAd() {
        if (!HelperClass.adsDisabled && interstitialAd != null && interstitialAd!!.isReady) {
            interstitialAd!!.showAd()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.howtouse -> {
                openHowToUse()
                return true
            }
            R.id.removeads -> {
                openRemoveAds()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun openHowToUse() {
        val intent = Intent(this, DirectMessage::class.java)
        startActivity(intent)
    }

    private fun openRemoveAds() {
        val intent = Intent(this, RewardAds::class.java)
        startActivity(intent)
    }

    private fun openDirectMessageActivity() {
        bottomNavigationView!!.selectedItemId = R.id.home
        val intent = Intent(this, DirectMessage::class.java)
        startActivity(intent)
    }

    private fun openMoreActivity() {
        bottomNavigationView!!.selectedItemId = R.id.home
        val intent = Intent(this, MoreActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private var count = 0
        var maxAdxInitialised: Boolean = false
    }
}