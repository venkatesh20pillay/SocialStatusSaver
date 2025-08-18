package com.statuses.statussavers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        bottomNavigationView = findViewById(R.id.bottomNavigationViewMore)
        cardView = findViewById(R.id.cardView)

        setupBottomBar()
        setupCardView()
    }

    private fun setupCardView() {
        cardView.setOnClickListener {
            val url = "https://860.game.qureka.com"
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MoreActivity, Uri.parse(url))
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }
    }

    private fun setupBottomBar() {
        bottomNavigationView.selectedItemId = R.id.more
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    openHomeActivity()
                    true
                }
                R.id.more -> true
                else -> false
            }
        }
    }

    private fun openHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.more
    }
}
