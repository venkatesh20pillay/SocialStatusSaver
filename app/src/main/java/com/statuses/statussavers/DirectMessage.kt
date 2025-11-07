package com.statuses.statussavers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hbb20.CountryCodePicker

class DirectMessage : AppCompatActivity() {

    private lateinit var ccp: CountryCodePicker
    private lateinit var editPhoneNumber: EditText
    private lateinit var btnStart: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            bottomNavigationView.updatePadding(bottom = systemBars.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationDirectMessage)
        ccp = findViewById(R.id.country_code)
        editPhoneNumber = findViewById(R.id.edit_phone_number)
        btnStart = findViewById(R.id.button_start)

        ccp.registerCarrierNumberEditText(editPhoneNumber)
        setupBottomBar()
        btnStart.setOnClickListener {
            // Initiate the validation and message logic
            validateAndStartWhatsApp()
        }
    }

    private fun setupBottomBar() {
        bottomNavigationView.selectedItemId = R.id.dm
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    openHomeActivity()
                    true
                }
                R.id.more -> {
                    openMoreActivity()
                    true
                }
                else -> false
            }
        }
    }

    private fun openHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun openMoreActivity() {
        bottomNavigationView!!.selectedItemId = R.id.home
        val intent = Intent(this, MoreActivity::class.java)
        startActivity(intent)
    }

    private fun validateAndStartWhatsApp() {
        // A. Use CCP's built-in validation
        if (ccp.isValidFullNumber) {

            // B. Get the formatted full number (including country code, e.g., +919876543210)
            // The CCP library provides a number formatted correctly for WhatsApp's URI scheme.
            val fullPhoneNumber = ccp.fullNumber

            // C. Clean the number for WhatsApp (remove leading '+' or other non-digits)
            // WhatsApp's URI scheme usually works best with just digits.
            // Example: +919876543210 -> 919876543210
            val numberForWhatsApp = fullPhoneNumber.replace("[^0-9]".toRegex(), "")

            // D. Open WhatsApp Intent
            openWhatsApp(numberForWhatsApp)

        } else {
            // E. Show an error if validation fails
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
            editPhoneNumber.error = "Invalid Number"
        }
    }

    private fun openWhatsApp(number: String) {
        try {
            // WhatsApp URI to start a chat
            val uri = Uri.parse("https://wa.me/$number")

            // Create an Intent to open the URI
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Optional: Specify package to ensure it opens in WhatsApp (useful for avoiding browser selection)
            // Note: Uncommenting this line might cause an exception if WhatsApp isn't installed.
            // intent.setPackage("com.whatsapp")

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp is not installed on this device.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}