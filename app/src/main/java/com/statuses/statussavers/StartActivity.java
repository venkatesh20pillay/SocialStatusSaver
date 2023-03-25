package com.statuses.statussavers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import static android.os.Build.VERSION.SDK_INT;

public class StartActivity extends AppCompatActivity {

    TextView permission1;
    TextView permission2;
    Button permission1Button;
    Button permission2Button;
    Button letsGo;
    ActivityResultLauncher<Intent> someActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        permission1 = (TextView) findViewById(R.id.permission1);
        permission2 = (TextView) findViewById(R.id.permission2);
        permission1Button = (Button) findViewById(R.id.permission1Button);
        permission2Button = (Button) findViewById(R.id.permission2Button);
        letsGo = (Button) findViewById(R.id.letsgo);
        setupLauncher();
        setView();
        setupOnClickButton();;
    }

    private void setView() {
        permission1.setText("Please give storage permission to save status");
        permission2.setText("Please give access to Android/Media folder to get all the status");
        permission1Button.setText("Click Here");
        permission2Button.setText("Click Here");
        letsGo.setText("Please give above 2 permissions");
        letsGo.setBackgroundColor(Color.parseColor("#A9A9A9"));
        letsGo.setTextColor(getResources().getColor(R.color.white));
    }

    private void setupOnClickButton() {
        permission1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permission1Button.getText().toString().equalsIgnoreCase("Click Here"));
                    checkPermission1();
            }
        });
        permission2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission2();
            }
        });
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(StartActivity.this, MainActivity.class);
               startActivity(intent);
            }
        });
    }

    private void checkPermission1() {
        ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        Intent intent= new Intent(MainActivity.class)
//        someActivityResultLauncher.launch(intent);
    }
    private void checkPermission2() {
       if(SDK_INT > 29) {

           getPermission();
       }
    }

    private void checkPermission() {

        if (SDK_INT >= 29) {
            boolean allowed = readDataFromPrefs();
            if (allowed) {
                 //setuplayout();
            } else {
                // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //openDialog();
                getPermission();
            }
        } else {
            if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //setuplayout();
            } else {
                ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //Intent intent= new Intent(MainActivity.class,WRITE_EXTERNAL_STORAGE)
                //someActivityResultLauncher.launch();
            }
        }
    }

    private Boolean readDataFromPrefs() {
        SharedPreferences sh = StartActivity.this.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
        String uri = sh.getString("PATH", "");
        if(uri!=null) {
            if (uri.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getPermission() {
        StorageManager storageManager = (StorageManager) StartActivity.this.getApplication().getSystemService(Context.STORAGE_SERVICE);
        Intent intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String path = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia";
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/tree/");
        scheme += "%3A" + path;
        uri = Uri.parse(scheme);
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        someActivityResultLauncher.launch(intent);
    }

    protected void setupLauncher() {

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Uri tree = result.getData().getData();
                            SharedPreferences sh = StartActivity.this.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sh.edit();
                            ed.putString("PATH", tree.toString());
                            ed.apply();
                           // setuplayout();
                        }
                        Log.d("veknatesh", "li");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 1 && grantResults[0] == 0 && grantResults[1] == 0) {
            permission1Button.setText("Done");
            permission1Button.setTextColor(getResources().getColor(R.color.black));
            permission1Button.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }
}