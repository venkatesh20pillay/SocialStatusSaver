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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.StrictMode
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.util.ArrayList

class Video : AppCompatActivity() {

    private lateinit var download: ImageView
    private lateinit var share: ImageView
    private lateinit var mparticularvideo: VideoView
    private lateinit var uri1: Uri
    private lateinit var rootLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContentView(R.layout.activity_video)
        supportActionBar?.title = "Video"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rootLayout = findViewById(R.id.root_layout)
        mparticularvideo = findViewById(R.id.particularvideo)
        share = findViewById(R.id.share)
        download = findViewById(R.id.download)
        share.setOnClickListener { shareVideo() }

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            rootLayout.updatePadding(bottom = systemBars.bottom)
            insets
        }

        val intent = intent
        val destpath = intent.getStringExtra("DEST_PATH_VIDEO")
        val file = intent.getStringExtra("FILE_VIDEO")
        val uri = intent.getStringExtra("URI_VIDEO")
        val filename = intent.getStringExtra("FILENAME_VIDEO")
        val show = intent.getStringExtra("show") ?: ""
        val destpath2 = File(destpath ?: "")
        val file1 = File(file ?: "")

        if (show.equals("delete", ignoreCase = true)) {
            if (!file1.absolutePath.contains("Pictures/Status Saver")) {
                download.visibility = View.GONE
            } else {
                download.setImageResource(R.mipmap.delete)
            }
        }

        val mediaController = MediaController(this)
        mediaController.setMediaPlayer(mparticularvideo)
        mediaController.setAnchorView(mparticularvideo)
        uri1 = Uri.parse(uri)
        mparticularvideo.setMediaController(mediaController)
        mparticularvideo.setVideoURI(uri1)
        mparticularvideo.requestFocus()
        mparticularvideo.start()

        download.setOnClickListener {
            try {
                if (show.equals("download", ignoreCase = true)) {
                    if (Build.VERSION.SDK_INT < 30) {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(file1, destpath2)
                    } else {
                        val imagesFolder = file1
                        if (!imagesFolder.exists()) {
                            imagesFolder.mkdirs()
                        }
                        val resolver: ContentResolver = baseContext.contentResolver
                        val inputStream: InputStream? = resolver.openInputStream(Uri.parse(uri))
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Status Saver")
                            put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                            put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                            put(MediaStore.Video.Media.IS_PENDING, 1)
                        }
                        val videoUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                        if (inputStream != null && videoUri != null) {
                            val pdf = baseContext.contentResolver.openFileDescriptor(videoUri, "w")
                            pdf?.let {
                                FileOutputStream(it.fileDescriptor).use { out ->
                                    val buf = ByteArray(8192)
                                    var len: Int
                                    while (inputStream.read(buf).also { len = it } > 0) {
                                        out.write(buf, 0, len)
                                    }
                                }
                                it.close()
                            }
                            contentValues.clear()
                            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                            baseContext.contentResolver.update(videoUri, contentValues, null, null)
                        }
                    }
                    Toast.makeText(applicationContext, "Video saved", Toast.LENGTH_SHORT).show()
                } else {
                    if (Build.VERSION.SDK_INT > 29) {
                        delete(file1, filename ?: "")
                    } else {
                        org.apache.commons.io.FileUtils.delete(file1)
                        Toast.makeText(applicationContext, "Video deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
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
            val resolver = baseContext.contentResolver
            resolver.delete(uri1, null, null)
            Toast.makeText(applicationContext, "Video deleted", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: SecurityException) {
            var pendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= 30) {
                val uris = ArrayList<Uri>()
                uris.add(uri1)
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
            } else if (e is RecoverableSecurityException) {
                val exception = e as RecoverableSecurityException
                pendingIntent = exception.userAction.actionIntent
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
            Toast.makeText(applicationContext, "Video deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun convertFileToContentUri(file: File, name: String): Uri? {
        val path = file.absolutePath
        val cursor: Cursor? = baseContext.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID),
            "${MediaStore.Video.Media.DATA} = ?",
            arrayOf(path), null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                @SuppressLint("Range")
                val id = it.getInt(it.getColumnIndex(MediaStore.MediaColumns._ID))
                return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "$id")
            }
        }
        return null
    }

    private fun shareVideo() {
        var mUri: Uri? = null
        try {
            val mUriField: Field = VideoView::class.java.getDeclaredField("mUri")
            mUriField.isAccessible = true
            mUri = mUriField.get(mparticularvideo) as Uri?
            if (mUri != null) {
                val videoshare = Intent(Intent.ACTION_SEND).apply {
                    type = "video/mp4"
                    putExtra(Intent.EXTRA_STREAM, mUri)
                }
                startActivity(Intent.createChooser(videoshare, "Share"))
            } else {
                Toast.makeText(applicationContext, "Unable to share", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Unable to share", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // mparticularvideo.start()
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
