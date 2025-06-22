package com.statuses.statussavers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import static android.os.Build.VERSION.SDK_INT;

public class Video extends AppCompatActivity {

    ImageView download, share;
    VideoView mparticularvideo;
    Uri uri1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_video);
        getSupportActionBar().setTitle("Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mparticularvideo = findViewById(R.id.particularvideo);
        share = findViewById(R.id.share);
        download = findViewById(R.id.download);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareVideo();
            }
        });

        Intent intent = getIntent();
        String destpath = intent.getStringExtra("DEST_PATH_VIDEO");
        String file = intent.getStringExtra("FILE_VIDEO");
        String uri = intent.getStringExtra("URI_VIDEO");
        String filename = intent.getStringExtra("FILENAME_VIDEO");
        String show = intent.getStringExtra("show");
        File destpath2 = new File(destpath);
        File file1 = new File(file);

        if(show.equalsIgnoreCase("delete")) {
            if (!file1.getAbsolutePath().contains("Pictures/Status Saver")) {
                download.setVisibility(View.GONE);
            }
            else {
                download.setImageResource(R.mipmap.delete);
            }
        }

        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mparticularvideo);
        mediaController.setAnchorView(mparticularvideo);
        uri1 = Uri.parse(uri);
        mparticularvideo.setMediaController(mediaController);
        mparticularvideo.setVideoURI(uri1);
        mparticularvideo.requestFocus();
        mparticularvideo.start();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(show.equalsIgnoreCase("download")) {
                        if (SDK_INT < 30) {
                            org.apache.commons.io.FileUtils.copyFileToDirectory(file1, destpath2);
                        } else {
                            File imagesFolder = file1;
                            if (!imagesFolder.exists()) {
                                imagesFolder.mkdirs();
                            }
                            ContentResolver resolver = getBaseContext().getContentResolver();
                            InputStream inputStream = resolver.openInputStream(Uri.parse(uri));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                            String directoryPath1 = Environment.DIRECTORY_PICTURES + "/Status Saver";
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryPath1);
                            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis()/1000);
                            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
                            contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
                            Uri videoUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                            if (inputStream != null) {
                                ParcelFileDescriptor pdf = getBaseContext().getContentResolver().openFileDescriptor(videoUri, "w");
                                assert pdf!=null;
                                FileOutputStream out = new FileOutputStream(pdf.getFileDescriptor());
                                byte[] buf = new byte[8192];
                                int len;
                                int progress = 0;
                                while((len = inputStream.read(buf)) > 0) {
                                    progress = progress + len;
                                    out.write(buf, 0, len);
                                }
                                out.close();
                                pdf.close();
                                contentValues.clear();
                                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
                                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
                                getBaseContext().getContentResolver().update(videoUri, contentValues, null, null);
                            }
                        }
                        Toast toast =  Toast.makeText(getApplicationContext(),"Video saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        if (SDK_INT >= 29) {
                            delete(file1, filename);
                        } else {
                            org.apache.commons.io.FileUtils.delete(file1);
                            Toast toast = Toast.makeText(getApplicationContext(), "Video deleted", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void delete(File file, String name) {
        Uri uri1 = convertFileToContentUri(file, name);
        if (uri1 == null) {
            return;
        }
        try {
            ContentResolver resolver = getBaseContext().getContentResolver();
            resolver.delete(uri1, null, null);
            Toast toast = Toast.makeText(getApplicationContext(), "Video deleted", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        catch (SecurityException e){
            PendingIntent pendingIntent = null;
            if (SDK_INT >= 30) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(uri1);
                pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), uris);
            } else if ( e instanceof RecoverableSecurityException) {
                RecoverableSecurityException exception = (RecoverableSecurityException) e;
                pendingIntent = exception.getUserAction().getActionIntent();

            }
            if (pendingIntent != null) {
                IntentSender intentSender = pendingIntent.getIntentSender();
                try {
                    startIntentSenderForResult(intentSender, 100, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Toast toast = Toast.makeText(getApplicationContext(), "Video deleted", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }


    private Uri convertFileToContentUri(File file, String name) {
        String path = file.getAbsolutePath();
        Cursor cursor = getBaseContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ""+id);
        }
        else if (file.exists()) {
            ContentResolver resolver = getBaseContext().getContentResolver();
            Uri picCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            String directoryPath1 = Environment.DIRECTORY_PICTURES + "/StatusSaver";
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryPath1);
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
            Uri imageUri = resolver.insert(picCollection, contentValues);
            contentValues.clear();
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
            return imageUri;
        }
        else {
            return null;
        }
    }

    private void shareVideo() {
        Uri mUri = null;
        try {
            Field mUriField = VideoView.class.getDeclaredField("mUri");
            mUriField.setAccessible(true);
            mUri = (Uri)mUriField.get(mparticularvideo);
            if (mUri != null) {
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.setType("video/mp4");
                videoshare.putExtra(Intent.EXTRA_STREAM, mUri);
                startActivity(Intent.createChooser(videoshare, "Share"));
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to share", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        } catch(Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Unable to share", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // mparticularvideo.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}