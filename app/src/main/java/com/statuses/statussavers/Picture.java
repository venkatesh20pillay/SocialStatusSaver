package com.statuses.statussavers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.os.Build.VERSION.SDK_INT;

public class Picture extends AppCompatActivity {

    ImageView mparticularimage, download, share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        getSupportActionBar().setTitle("Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mparticularimage = findViewById(R.id.particularimage);
        share = findViewById(R.id.share);
        download = findViewById(R.id.download);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
        Intent intent = getIntent();
        String destpath = intent.getStringExtra("DEST_PATH");
        String file = intent.getStringExtra("FILE");
        String uri = intent.getStringExtra("URI_IMAGE");
        String filename = intent.getStringExtra("FILENAME_IMAGE");
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

        Glide.with(getApplicationContext()).load(uri).into(mparticularimage);

        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                    if(show.equalsIgnoreCase("download")) {
                        try {
                            if (SDK_INT < 30) {
                                org.apache.commons.io.FileUtils.copyFileToDirectory(file1, destpath2);
                            } else {
                                File imagesFolder = file1;
                                if (!imagesFolder.exists()) {
                                    imagesFolder.mkdirs();
                                }
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
                                ContentResolver resolver = getBaseContext().getContentResolver();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                                String directoryPath1 = Environment.DIRECTORY_PICTURES + "/Status Saver";
                                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryPath1);
                                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                                if (bitmap != null) {
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.close();
                                }
                            }
                            Toast toast = Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                            if (SDK_INT > 29) {
                                delete(file1, filename);
                            }
                            else {
                                try {
                                    org.apache.commons.io.FileUtils.delete(file1);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_SHORT);
                                    toast.show();
                                    finish();
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                            }
                        }
                    }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void delete(File file, String name) {
        Uri uri1 = convertFileToContentUri(file, name);
        if (uri1 == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Video not found", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }
        try {
            ContentResolver resolver = getBaseContext().getContentResolver();
            resolver.delete(uri1, null, null);
            Toast toast = Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        catch (SecurityException e){
            PendingIntent pendingIntent = null;
            if (SDK_INT >= 30) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(uri1);
                pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), uris);
            } else if ( e instanceof RecoverableSecurityException ) {
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
            Toast toast = Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    private Uri convertFileToContentUri(File file, String name) {
        String path = file.getAbsolutePath();
        Cursor cursor = getBaseContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""+id);
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    private void shareImage() {
        Drawable drawable = mparticularimage.getDrawable();

        if (!(drawable instanceof BitmapDrawable)) {
            Toast.makeText(this, "Image not available to share", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        Uri imageUri = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "shared_image_" + System.currentTimeMillis() + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);  // Optional but helps with newer Androids

            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                OutputStream out = getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();

                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);  // Publish the image
                getContentResolver().update(imageUri, values, null, null);
            } else {
                Toast.makeText(this, "Failed to create image Uri", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Image save failed", Toast.LENGTH_SHORT).show();
        }
        if (imageUri != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share Image"));
        }
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