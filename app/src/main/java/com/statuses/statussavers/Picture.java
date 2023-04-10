package com.statuses.statussavers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import java.util.Objects;

public class Picture extends AppCompatActivity {

    ImageView mparticularimage, download, mychatapp, share;

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
        if(show.equalsIgnoreCase("delete")) {
            download.setImageResource(R.mipmap.delete);
        }

        File destpath2 = new File(destpath);
        File file1 = new File(file);

        Glide.with(getApplicationContext()).load(uri).into(mparticularimage);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(show.equalsIgnoreCase("download")) {
                        //org.apache.commons.io.FileUtils.copyFileToDirectory(file1, destpath2);
                        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                        File imagesFolder = new File(directoryPath, "/StatusSaver");
                        if(!imagesFolder.exists()) {
                            imagesFolder.mkdirs();
                        }
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
                        ContentResolver resolver = getBaseContext().getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                        String directoryPath1 = Environment.DIRECTORY_PICTURES + "/StatusSaver";
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryPath1);
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                        }
                        Toast toast =  Toast.makeText(getApplicationContext(),"Image saved", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        org.apache.commons.io.FileUtils.delete(file1);
                        Toast toast = Toast.makeText(getApplicationContext(),"Image deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{destpath + filename}, new String[]{"*/*"},
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {

                            }

                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
//                Dialog dialog = new Dialog(Picture.this);
//                dialog.setContentView(R.layout.custom_dialog);
//                dialog.show();
//                Button button = dialog.findViewById(R.id.okbutton);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
            }
        });
    }

    private void shareImage() {
        BitmapDrawable drawable = (BitmapDrawable) mparticularimage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
        Uri uri = Uri.parse(bitmapPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share"));
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