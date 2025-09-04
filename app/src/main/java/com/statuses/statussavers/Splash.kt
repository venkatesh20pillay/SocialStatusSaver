package com.statuses.statussavers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Splash : AppCompatActivity() {
    private var subscriptionManager: SubscriptionManager? = SubscriptionManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val actionBar = this.supportActionBar
        actionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            initialChecks()
        }, SPLASH_TIMER.toLong())
    }

    private fun initialChecks() {
        subscriptionManager?.initBillingClient(this) { isSetupFinished ->
            if (isSetupFinished) {
                subscriptionManager?.checkSubscription { isSubscribed ->
                    Handler(Looper.getMainLooper()).post {
                        HelperClass.adsDisabled = isSubscribed
                        startPremissionCheck()
                    }
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    startPremissionCheck()
                }
            }
        }
    }

    private fun startPremissionCheck() {
            val intent = if (checkBothPermission()) {
                Intent(this@Splash, MainActivity::class.java)
            } else {
                Intent(this@Splash, StartActivity::class.java)
            }
            startActivity(intent)
            finish()
    }

    private fun checkBothPermission(): Boolean {
        var readwrite = false
        var path = false
        if (VERSION.SDK_INT >= 30) {
            path = readDataFromPrefs()
            readwrite = readPermission()
        } else {
            path = true
            readwrite = readPermission()
        }
        return path && readwrite
    }

    private fun readPermission(): Boolean {
        if (VERSION.SDK_INT <= 29) {
            return if (ContextCompat.checkSelfPermission(
                    this@Splash,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                false
            }
        } else {
            val sh = this@Splash.getSharedPreferences("PERMISSION", MODE_PRIVATE)
            val uri = if (VERSION.SDK_INT >= 33) {
                sh.getString("readwrite33", "")
            } else {
                sh.getString("readwrite", "")
            }
            if (uri != null) {
                if (uri.isEmpty()) {
                    return false
                }
            }
            return true
        }
    }

    private fun readDataFromPrefs(): Boolean {
        val sh = this@Splash.getSharedPreferences("DATA_PATH", MODE_PRIVATE)
        val uri = sh.getString("PATH", "")
        if (uri != null) {
            if (uri.isEmpty()) {
                return false
            }
        }
        return true
    }

    companion object {
        const val SPLASH_TIMER: Int = 1000
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionManager?.endConnection()
        subscriptionManager = null
    }
}