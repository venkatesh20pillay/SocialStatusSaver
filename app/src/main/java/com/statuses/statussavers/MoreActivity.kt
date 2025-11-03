package com.statuses.statussavers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cardView: CardView
    private lateinit var rootLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        rootLayout = findViewById(R.id.rootLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationViewMore)
        cardView = findViewById(R.id.cardView)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            rootLayout.updatePadding(top = systemBars.top)
            val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(15, 180, 15, 0)
            cardView.layoutParams = layoutParams
            bottomNavigationView.updatePadding(bottom = systemBars.bottom)
            insets
        }

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
                R.id.dm -> {
                    openDirectMessageActivity()
                    true
                }
                R.id.more -> true
                else -> false
            }
        }
    }

    private fun openDirectMessageActivity() {
        bottomNavigationView!!.selectedItemId = R.id.home
        val intent = Intent(this, DirectMessage::class.java)
        startActivity(intent)
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
