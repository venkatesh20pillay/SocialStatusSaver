package com.statuses.statussavers

import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RewardAds : AppCompatActivity() {

    private lateinit var buyButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_ads)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.let {
            it.title = "Remove Ads"
            it.setDisplayHomeAsUpEnabled(true)
        }
        buyButton = findViewById(R.id.btn_buy)
        buyButton.setOnClickListener {
            if (buyButton.text.equals("Subscribe")) {
                startPurchaseFlow()
            } else {

            }
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

    private fun startPurchaseFlow() {
        SubscriptionManager.onPurchaseCallback = { value, isSuccessFull ->
            Toast.makeText(this, value, Toast.LENGTH_LONG).show()
            if (isSuccessFull) {
                HelperClass.adsDisabled = true
                finish()
            }
        }
        SubscriptionManager.initBillingClient(this) { isSetupFinished ->
            if (isSetupFinished) {
                SubscriptionManager.launchSubscriptionFlow(this)
            } else {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}