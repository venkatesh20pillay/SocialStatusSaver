package com.statuses.statussavers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class Video extends AppCompatActivity {

    ImageView download, mychatapp, share;
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
        if(show.equalsIgnoreCase("delete")) {
            download.setImageResource(R.mipmap.delete);
        }

        File destpath2 = new File(destpath);
        File file1 = new File(file);

        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mparticularvideo);
        mediaController.setAnchorView(mparticularvideo);
        uri1 = Uri.parse(uri);
        mparticularvideo.setMediaController(mediaController);
        mparticularvideo.setVideoURI(uri1);
        mparticularvideo.requestFocus();
        mparticularvideo.start();

    //    Glide.with(getApplicationContext()).load(uri).into(mparticularvideo);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(show.equalsIgnoreCase("download")) {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(file1, destpath2);
                        Toast toast =  Toast.makeText(getApplicationContext(),"Video saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        org.apache.commons.io.FileUtils.delete(file1);
                        Toast toast =  Toast.makeText(getApplicationContext(),"Video deleted", Toast.LENGTH_SHORT);
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
//                Dialog dialog = new Dialog(Video.this);
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

    private void shareVideo() {
        Uri mUri = null;
        try {
            Field mUriField = VideoView.class.getDeclaredField("mUri");
            mUriField.setAccessible(true);
            mUri = (Uri)mUriField.get(mparticularvideo);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("video/mp4");
            videoshare.putExtra(Intent.EXTRA_STREAM,mUri);

            startActivity(Intent.createChooser(videoshare, "Share"));
        } catch(Exception e) {

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