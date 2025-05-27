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
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;

import org.jetbrains.annotations.NotNull;
import static android.os.Build.VERSION.SDK_INT;

public class StartActivity extends AppCompatActivity {

    TextView permission1;
    TextView permission2;
    Button permission1Button;
    Button permission2Button;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    MaxAdView maxAdView;
    ImageView imageViewSecond;
    ImageView useThisFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_v2);
        permission1 = (TextView) findViewById(R.id.permission1);
        permission2 = (TextView) findViewById(R.id.permission2);
        permission1Button = (Button) findViewById(R.id.permission1Button);
        permission2Button = (Button) findViewById(R.id.permission2Button);
        imageViewSecond = (ImageView) findViewById(R.id.second);
        useThisFolder = (ImageView) findViewById(R.id.usethisfolder2);
        setupLauncher();
        setView();
        setupOnClickButton();
        setApplovin();
    }

    private void setApplovin() {
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder("_MudM74bNJVdvK6QTPbWsZJ6vDPNKe5FewSXgwuxFv7gUivBxUg9FRqeTGZKBPZTl7byRprTRC93GbnEC9bLu5", this )
                .setMediationProvider( AppLovinMediationProvider.MAX )
                .build();
        AppLovinSdk.getInstance( this ).initialize( initConfig, new AppLovinSdk.SdkInitializationListener()
        {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration sdkConfig)
            {
                loadAppLovinAd();
            }
        } );
    }

    private void loadAppLovinAd() {
        maxAdView = (MaxAdView) findViewById(R.id.maxAd);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd maxAd) {

            }

            @Override
            public void onAdCollapsed(MaxAd maxAd) {

            }

            @Override
            public void onAdLoaded(MaxAd maxAd) {

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

            }
        });
        maxAdView.loadAd();
    }

    private void setView() {
        setPermission1ButtonView();
        setPermission2ButtonView();
        setLetsGoView();
    }

    private void setLetsGoView() {
        if(checkBothPermission()) {
            Toast.makeText(this, "Loading ...",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setPermission2ButtonView() {
        if(SDK_INT >= 30) {
            permission2.setText(R.string.storage_permission2);
            boolean allowed = readDataFromPrefs();
            if (allowed) {
                permission2Button.setText("Done");
                permission2Button.setTextColor(getResources().getColor(R.color.white));
                permission2Button.setBackground(getDrawable(R.drawable.rounded_corner));
                setLetsGoView();
            } else {
                permission2Button.setText("Click Here");
                permission2Button.setTextColor(getResources().getColor(R.color.white));
                permission2Button.setBackground(getDrawable(R.drawable.rounded_corner));
            }
        }
        else {
            permission2Button.setVisibility(View.GONE);
            permission2.setVisibility(View.GONE);
            imageViewSecond.setVisibility(View.GONE);
            useThisFolder.setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setPermission1ButtonView() {
        permission1.setText(R.string.storage_permission);
        if(SDK_INT >= 30) {
            boolean allowed = readPermission();
            if(allowed) {
                permission1Button.setText("Done");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackground(getDrawable(R.drawable.rounded_corner_2));
                setLetsGoView();
            }
            else {
                permission1Button.setText("Click Here");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackground(getDrawable(R.drawable.rounded_corner));
            }
        }

        else {
            if(ContextCompat.checkSelfPermission(StartActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                permission1Button.setText("Done");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackground(getDrawable(R.drawable.rounded_corner_2));
                setLetsGoView();
            }
            else {
                permission1Button.setText("Click Here");
                permission1Button.setTextColor(getResources().getColor(R.color.white));
                permission1Button.setBackground(getDrawable(R.drawable.rounded_corner));
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
    }

    private void checkPermission1() {
        if(SDK_INT >= 30) {
            boolean allowed = readPermission();
            if(allowed) {
                setPermission1ButtonView();
            }
            else {
                if (SDK_INT >= 33) {
                    ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, 2);

                } else {
                    ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
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
        String uri;
        if (SDK_INT >= 33) {
            uri = sh.getString("readwrite33", "");
        } else {
            uri = sh.getString("readwrite", "");
        }
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
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri tree = data.getData();
                                if (tree != null) {
                                    int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    try {
                                        getContentResolver().takePersistableUriPermission(tree, flags);
                                    } catch (SecurityException e) {
                                        Toast.makeText(getApplicationContext(), "Failed to persist permission: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    String path = tree.toString();
                                    if (path.endsWith("WhatsApp%2FMedia%2F.Statuses")) {
                                        SharedPreferences sh = StartActivity.this.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor ed = sh.edit();
                                        ed.putString("PATH", path);
                                        ed.apply();
                                        setPermission2ButtonView();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please don't change directory and make sure it's .Statuses", Toast.LENGTH_LONG).show();
                                    }
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
            if (requestCode == 2) {
                ed.putString("readwrite33", "true");
            } else {
                ed.putString("readwrite", "true");
            }
            ed.apply();
            setPermission1ButtonView();
        }
    }
}