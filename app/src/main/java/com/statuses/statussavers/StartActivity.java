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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static android.os.Build.VERSION.SDK_INT;

public class StartActivity extends AppCompatActivity {

    TextView permission1;
    TextView permission2;
    TextView note;
    Button permission1Button;
    Button permission2Button;
    Button letsGo;
    ImageView usethisfolder;
    ActivityResultLauncher<Intent> someActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        permission1 = (TextView) findViewById(R.id.permission1);
        permission2 = (TextView) findViewById(R.id.permission2);
        note = (TextView) findViewById(R.id.note);
        usethisfolder = (ImageView) findViewById(R.id.usethisfolder);
        permission1Button = (Button) findViewById(R.id.permission1Button);
        permission2Button = (Button) findViewById(R.id.permission2Button);
        letsGo = (Button) findViewById(R.id.letsgo);
        setupLauncher();
        setView();
        setupOnClickButton();
    }

    private void setView() {
        setPermission1ButtonView();
        setPermission2ButtonView();
        setLetsGoView();
    }

    private void setLetsGoView() {
        if(checkBothPermission()) {
            letsGo.setText("Let's Go");
            letsGo.setBackgroundColor(Color.parseColor("#005C29"));
            letsGo.setTextColor(getResources().getColor(R.color.white));
        }
        else {
            letsGo.setText("Please give above permissions");
            letsGo.setBackgroundColor(Color.parseColor("#808080"));
            letsGo.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private Boolean checkBothPermission() {
        Boolean readwrite = false;
        Boolean path = false;
        if(SDK_INT >=30) {
            path = readDataFromPrefs();
            readwrite = readPermission();
        }
        else {
            path = true;
            if(ContextCompat.checkSelfPermission(StartActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                readwrite = true;
            }

        }
        return path && readwrite;
    }

    private void setPermission2ButtonView() {
        if(SDK_INT >= 30) {
            permission2.setText("Please give access to .Statuses folder to get all the status");
            note.setText("* Make user you are on .Statuses folder, click on use this folder and allow. See image below for reference.");
            usethisfolder.setImageResource(R.mipmap.usethisfolder);
            boolean allowed = readDataFromPrefs();
            if (allowed) {
                permission2Button.setText("Done");
                permission2Button.setTextColor(getResources().getColor(R.color.black));
                permission2Button.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                permission2Button.setText("Click Here");
                permission2Button.setTextColor(getResources().getColor(R.color.white));
                permission2Button.setBackgroundColor(Color.parseColor("#005C29"));
            }
        }
        else {
            permission2Button.setVisibility(View.GONE);
            permission2.setVisibility(View.GONE);
            usethisfolder.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }
    }

    private void setPermission1ButtonView() {
        permission1.setText("Please give storage permission to save status");
        if(SDK_INT >= 30) {
            boolean allowed = readPermission();
            if(allowed) {
                permission1Button.setText("Done");
                permission1Button.setTextColor(getResources().getColor(R.color.black));
                permission1Button.setBackgroundColor(getResources().getColor(R.color.white));
            }
            else {
                permission1Button.setText("Click Here");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackgroundColor(Color.parseColor("#005C29"));
            }
        }

        else {
            if(ContextCompat.checkSelfPermission(StartActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                permission1Button.setText("Done");
                permission1Button.setTextColor(getResources().getColor(R.color.black));
                permission1Button.setBackgroundColor(getResources().getColor(R.color.white));
            }
            else {
                permission1Button.setText("Click Here");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackgroundColor(Color.parseColor("#005C29"));
            }
        }
    }

    private void setupOnClickButton() {
        permission1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permission1Button.getText().toString().equalsIgnoreCase("Click Here")) {
                    checkPermission1();
                }
            }
        });
        permission2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permission2Button.getText().toString().equalsIgnoreCase("Click Here")) {
                    checkPermission2();
                }
            }
        });
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBothPermission()) {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void checkPermission1() {
        if(SDK_INT >= 30) {
            boolean allowed = readPermission();
            if(allowed) {
                setPermission1ButtonView();
            }
            else {
                ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }

        else {
            if(ContextCompat.checkSelfPermission(StartActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
               setPermission1ButtonView();
            }
            else {
                ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }    }
    private void checkPermission2() {
       if(SDK_INT > 29) {
           getPermission();
       }
    }

    private Boolean readPermission() {
        SharedPreferences sh = StartActivity.this.getSharedPreferences("PERMISSION", Context.MODE_PRIVATE);
        String uri = sh.getString("readwrite", "");
        if(uri!=null) {
            if (uri.isEmpty()) {
                return false;
            }
        }
        return true;
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
        String path = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + path;
        uri = Uri.parse(scheme);
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        someActivityResultLauncher.launch(intent);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setupLauncher() {

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                   // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Uri tree = result.getData().getData();
                            String path = tree.toString();
                            if(path != null) {
                                getContentResolver().takePersistableUriPermission(Uri.parse(path), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                if (path.endsWith("WhatsApp%2FMedia%2F.Statuses")) {
                                    SharedPreferences sh = StartActivity.this.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor ed = sh.edit();
                                    ed.putString("PATH", path);
                                    ed.apply();
                                    setPermission2ButtonView();
                                    setLetsGoView();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Please don't change directory and make sure its .Statuses", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 1 && grantResults[0] == 0 && grantResults[1] == 0) {
            SharedPreferences sh = StartActivity.this.getSharedPreferences("PERMISSION", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sh.edit();
            ed.putString("readwrite", "true");
            ed.apply();
            setPermission1ButtonView();
            setLetsGoView();
        }
    }
}