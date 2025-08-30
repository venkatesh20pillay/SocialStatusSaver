package com.statuses.statussavers

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.statuses.statussavers.MainActivity

class StartActivity : AppCompatActivity() {
    var permission1: TextView? = null
    var permission2: TextView? = null
    var permission1Button: Button? = null
    var permission2Button: Button? = null
    var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var maxAdView: MaxAdView? = null
    var imageViewSecond: ImageView? = null
    var useThisFolder: ImageView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_v2)
        val rootView = findViewById<View>(R.id.root_layout)  // Make sure you have a root layout
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->

            // Get insets for system bars
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Adjust padding for root layout
            rootView.updatePadding(
                top = systemBarsInsets.top,  // Adjust top padding for status bar
                bottom = systemBarsInsets.bottom  // Adjust bottom padding for navigation bar
            )

            // Return consumed insets to indicate that we have handled them
            WindowInsetsCompat.CONSUMED
        }
        permission1 = findViewById<View>(R.id.permission1) as TextView
        permission2 = findViewById<View>(R.id.permission2) as TextView
        permission1Button = findViewById<View>(R.id.permission1Button) as Button
        permission2Button = findViewById<View>(R.id.permission2Button) as Button
        imageViewSecond = findViewById<View>(R.id.second) as ImageView
        useThisFolder = findViewById<View>(R.id.usethisfolder2) as ImageView
        setupLauncher()
        setView()
        setupOnClickButton()
        setApplovin()
    }

    private fun setApplovin() {
        val initConfig = AppLovinSdkInitializationConfiguration.builder(
            "_MudM74bNJVdvK6QTPbWsZJ6vDPNKe5FewSXgwuxFv7gUivBxUg9FRqeTGZKBPZTl7byRprTRC93GbnEC9bLu5",
            this
        )
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()
        AppLovinSdk.getInstance(this).initialize(initConfig) { loadAppLovinAd() }
    }

    private fun loadAppLovinAd() {
        maxAdView = findViewById<View>(R.id.maxAd) as MaxAdView
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
            }

            override fun onAdDisplayFailed(maxAd: MaxAd, maxError: MaxError) {
            }
        })
        maxAdView!!.loadAd()
    }

    private fun setView() {
        setPermission1ButtonView()
        setPermission2ButtonView()
        setLetsGoView()
    }

    private fun setLetsGoView() {
        if (checkBothPermission()) {
            Toast.makeText(
                this, "Loading ...",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun checkBothPermission(): Boolean {
        var readwrite = false
        var path = false
        if (VERSION.SDK_INT >= 30) {
            path = readDataFromPrefs()
            readwrite = readPermission()
        } else {
            path = true
            if (ContextCompat.checkSelfPermission(
                    this@StartActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                readwrite = true
            }
        }
        return path && readwrite
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPermission2ButtonView() {
        if (VERSION.SDK_INT >= 30) {
            permission2!!.setText(R.string.storage_permission2)
            val allowed = readDataFromPrefs()
            if (allowed) {
                permission2Button!!.text = "Done"
                permission2Button!!.setTextColor(resources.getColor(R.color.white))
                permission2Button!!.background = getDrawable(R.drawable.rounded_corner)
                setLetsGoView()
            } else {
                permission2Button!!.text = "Click Here"
                permission2Button!!.setTextColor(resources.getColor(R.color.white))
                permission2Button!!.background = getDrawable(R.drawable.rounded_corner)
            }
        } else {
            permission2Button!!.visibility = View.GONE
            permission2!!.visibility = View.GONE
            imageViewSecond!!.visibility = View.GONE
            useThisFolder!!.visibility = View.GONE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPermission1ButtonView() {
        permission1!!.setText(R.string.storage_permission)
        if (VERSION.SDK_INT >= 30) {
            val allowed = readPermission()
            if (allowed) {
                permission1Button!!.text = "Done"
                permission1Button!!.setTextColor(resources.getColor(R.color.white))
                permission1Button!!.background = getDrawable(R.drawable.rounded_corner_2)
                setLetsGoView()
            } else {
                permission1Button!!.text = "Click Here"
                permission1Button!!.setTextColor(resources.getColor(R.color.white))
                permission1Button!!.background = getDrawable(R.drawable.rounded_corner)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@StartActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permission1Button!!.text = "Done"
                permission1Button!!.setTextColor(resources.getColor(R.color.white))
                permission1Button!!.background = getDrawable(R.drawable.rounded_corner_2)
                setLetsGoView()
            } else {
                permission1Button!!.text = "Click Here"
                permission1Button!!.setTextColor(resources.getColor(R.color.white))
                permission1Button!!.background = getDrawable(R.drawable.rounded_corner)
            }
        }
    }

    private fun setupOnClickButton() {
        permission1Button!!.setOnClickListener {
            if (permission1Button!!.text.toString().equals("Click Here", ignoreCase = true)) {
                checkPermission1()
            }
        }
        permission2Button!!.setOnClickListener {
            if (permission2Button!!.text.toString().equals("Click Here", ignoreCase = true)) {
                checkPermission2()
            }
        }
    }

    private fun checkPermission1() {
        if (VERSION.SDK_INT >= 30) {
            val allowed = readPermission()
            if (allowed) {
                setPermission1ButtonView()
            } else {
                if (VERSION.SDK_INT >= 33) {
                    ActivityCompat.requestPermissions(
                        this@StartActivity,
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        ),
                        2
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this@StartActivity,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        1
                    )
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@StartActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setPermission1ButtonView()
            } else {
                ActivityCompat.requestPermissions(
                    this@StartActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
        }
    }

    private fun checkPermission2() {
        if (VERSION.SDK_INT > 29) {
            permission
        }
    }

    private fun readPermission(): Boolean {
        val sh = this@StartActivity.getSharedPreferences("PERMISSION", MODE_PRIVATE)
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

    private fun readDataFromPrefs(): Boolean {
        val sh = this@StartActivity.getSharedPreferences("DATA_PATH", MODE_PRIVATE)
        val uri = sh.getString("PATH", "")
        if (uri != null) {
            if (uri.isEmpty()) {
                return false
            }
        }
        return true
    }

    @get:RequiresApi(api = Build.VERSION_CODES.Q)
    private val permission: Unit
        get() {
            val storageManager =
                this@StartActivity.application.getSystemService(STORAGE_SERVICE) as StorageManager
            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
            val path = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
            var scheme = uri.toString()
            scheme = scheme.replace("/root/", "/document/")
            scheme += "%3A$path"
            uri = Uri.parse(scheme)
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            someActivityResultLauncher!!.launch(intent)
        }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected fun setupLauncher() {
        someActivityResultLauncher = registerForActivityResult(
            StartActivityForResult(),
            ActivityResultCallback { result ->

                // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        val tree = data.data
                        if (tree != null) {
                            val flags =
                                data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            try {
                                contentResolver.takePersistableUriPermission(tree, flags)
                            } catch (e: SecurityException) {
                                Toast.makeText(
                                    applicationContext,
                                    "Something went wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@ActivityResultCallback
                            }

                            val path = tree.toString()
                            if (path.endsWith("WhatsApp%2FMedia%2F.Statuses")) {
                                val sh = this@StartActivity.getSharedPreferences(
                                    "DATA_PATH",
                                    MODE_PRIVATE
                                )
                                val ed = sh.edit()
                                ed.putString("PATH", path)
                                ed.apply()
                                setPermission2ButtonView()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Please don't change directory and make sure it's .Statuses",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 1 && grantResults[0] == 0 && grantResults[1] == 0) {
            val sh = this@StartActivity.getSharedPreferences("PERMISSION", MODE_PRIVATE)
            val ed = sh.edit()
            if (requestCode == 2) {
                ed.putString("readwrite33", "true")
            } else {
                ed.putString("readwrite", "true")
            }
            ed.apply()
            setPermission1ButtonView()
        }
    }
}