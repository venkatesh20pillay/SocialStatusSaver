package com.statuses.statussavers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class Picture : AppCompatActivity() {

    private lateinit var mparticularimage: ImageView
    private lateinit var download: ImageView
    private lateinit var share: ImageView
    private lateinit var repost: ImageView
    private lateinit var rootLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        supportActionBar?.title = "Picture"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rootLayout = findViewById(R.id.root)

        mparticularimage = findViewById(R.id.particularimage)
        share = findViewById(R.id.share)
        download = findViewById(R.id.download)
        repost = findViewById(R.id.repost)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            rootLayout.updatePadding(bottom = systemBars.bottom)
            insets
        }

        share.setOnClickListener {
            if (VERSION.SDK_INT >= 30) {
                shareImage()
            } else {
                shareImage2()
            }
        }

        repost.setOnClickListener {
            if (VERSION.SDK_INT >= 30) {
                repostImage()
            } else {
                repostImage2()
            }
        }

        val intent = intent
        val destpath = intent.getStringExtra("DEST_PATH")
        val file = intent.getStringExtra("FILE")
        val uri = intent.getStringExtra("URI_IMAGE")
        val filename = intent.getStringExtra("FILENAME_IMAGE")
        val show = intent.getStringExtra("show")

        val destpath2 = File(destpath ?: "")
        val file1 = File(file ?: "")

        if (show.equals("delete", ignoreCase = true)) {
            if (!file1.absolutePath.contains("Pictures/Status Saver")) {
                download.visibility = View.GONE
            } else {
                download.setImageResource(R.mipmap.delete)
            }
        }

        Glide.with(applicationContext).load(uri).into(mparticularimage)

        download.setOnClickListener {
            if (show.equals("download", ignoreCase = true)) {
                try {
                    if (Build.VERSION.SDK_INT < 30) {
                        FileUtils.copyFileToDirectory(file1, destpath2)
                    } else {
                        if (!file1.exists()) {
                            file1.mkdirs()
                        }
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(uri))
                        val resolver: ContentResolver = baseContext.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Status Saver")
                        }
                        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        imageUri?.let { uriNonNull ->
                            resolver.openOutputStream(uriNonNull)?.use { out ->
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                            }
                        }
                    }
                    Toast.makeText(applicationContext, "Image saved", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                if (Build.VERSION.SDK_INT > 29) {
                    delete(file1, filename ?: "")
                } else {
                    try {
                        FileUtils.delete(file1)
                        Toast.makeText(applicationContext, "Image deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun delete(file: File, name: String) {
        val uri1 = convertFileToContentUri(file, name)
        if (uri1 == null) {
            Toast.makeText(applicationContext, "Video not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        try {
            contentResolver.delete(uri1, null, null)
            Toast.makeText(applicationContext, "Image deleted", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: SecurityException) {
            var pendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= 30) {
                val uris = ArrayList<Uri>()
                uris.add(uri1)
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
            } else if (e is RecoverableSecurityException) {
                pendingIntent = e.userAction.actionIntent
            }
            pendingIntent?.intentSender?.let {
                try {
                    startIntentSenderForResult(it, 100, null, 0, 0, 0)
                } catch (sendIntentException: IntentSender.SendIntentException) {
                    sendIntentException.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(applicationContext, "Image deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("Range")
    private fun convertFileToContentUri(file: File, name: String): Uri? {
        val path = file.absolutePath
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.DATA} = ?",
            arrayOf(path),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex(MediaStore.MediaColumns._ID))
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            }
        }
        return null
    }

    private fun shareImage2() {
        val bitmapValue = mparticularimage.getDrawable().toBitmap()
        val uri = getImageToShare(bitmapValue)
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun repostImage2() {
        val bitmapValue = mparticularimage.getDrawable().toBitmap()
        val uri = getImageToShare(bitmapValue)
        val whatsappPackage = "com.whatsapp"

        // The specific action used to share to status
        val WA_STATUS_ACTION = "com.whatsapp.intent.action.SEND_STATUS"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            // Set the Intent action to the specific WhatsApp Status action (may not be officially documented)
            // Alternatively, you can use Intent.ACTION_SEND and rely on setPackage
            // which will usually open the contact picker where the user can choose "My Status".
            action = Intent.ACTION_SEND

            // This is the key to targeting WhatsApp:
            setPackage(whatsappPackage)

            // Set the image URI and type
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg" // Use a generic image type or a specific one if known

            // Grant read permission to WhatsApp for the URI
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Check if WhatsApp is installed before attempting to start the activity
        if (shareIntent.resolveActivity(this.packageManager) != null) {
            try {
                // Use Intent.createChooser as a fallback, though for a direct share,
                // starting the intent directly is preferred if you're sure of the package.
                this.startActivity(shareIntent)
            } catch (e: Exception) {
                // Handle activity not found or other exceptions
                Toast.makeText(this, "Could not share to WhatsApp.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageToShare(bitmap: Bitmap): Uri? {
        var uri: Uri? = null
        try {
            val imageFolder = File(cacheDir, "images").apply { mkdirs() }
            val file = File(imageFolder, "shared_image.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            }
            uri = FileProvider.getUriForFile(
                this,
                "com.statuses.statussavers.provider",
                file
            )
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Error sharing image", Toast.LENGTH_LONG).show()
        }
        return uri
    }

    private fun shareImage() {
        val drawable = mparticularimage.drawable

        if (drawable !is BitmapDrawable) {
            Toast.makeText(this, "Image not available to share", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = drawable.bitmap
        var imageUri: Uri? = null
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "shared_image_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.IS_PENDING, 1) // for newer Android versions
            }
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageUri?.let {
                contentResolver.openOutputStream(it).use { out ->
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(it, values, null, null)
            } ?: run {
                Toast.makeText(this, "Failed to create image Uri", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Image save failed", Toast.LENGTH_SHORT).show()
        }

        imageUri?.let {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, it)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Share Image"))
        }
    }

    private fun repostImage() {
        val drawable = mparticularimage.drawable

        if (drawable !is BitmapDrawable) {
            Toast.makeText(this, "Image not available to share", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = drawable.bitmap
        var imageUri: Uri? = null
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "shared_image_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.IS_PENDING, 1) // for newer Android versions
            }
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageUri?.let {
                contentResolver.openOutputStream(it).use { out ->
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(it, values, null, null)
            } ?: run {
                Toast.makeText(this, "Failed to create image Uri", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Image save failed", Toast.LENGTH_SHORT).show()
        }

        imageUri?.let {
            val whatsappPackage = "com.whatsapp"

            // The specific action used to share to status
            val WA_STATUS_ACTION = "com.whatsapp.intent.action.SEND_STATUS"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                // Set the Intent action to the specific WhatsApp Status action (may not be officially documented)
                // Alternatively, you can use Intent.ACTION_SEND and rely on setPackage
                // which will usually open the contact picker where the user can choose "My Status".
                action = Intent.ACTION_SEND

                // This is the key to targeting WhatsApp:
                setPackage(whatsappPackage)

                // Set the image URI and type
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg" // Use a generic image type or a specific one if known

                // Grant read permission to WhatsApp for the URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Check if WhatsApp is installed before attempting to start the activity
            if (shareIntent.resolveActivity(this.packageManager) != null) {
                try {
                    // Use Intent.createChooser as a fallback, though for a direct share,
                    // starting the intent directly is preferred if you're sure of the package.
                    this.startActivity(shareIntent)
                } catch (e: Exception) {
                    // Handle activity not found or other exceptions
                    Toast.makeText(this, "Could not share to WhatsApp.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
