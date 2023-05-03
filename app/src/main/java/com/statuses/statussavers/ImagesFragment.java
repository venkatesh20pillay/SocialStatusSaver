package com.statuses.statussavers;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class ImagesFragment extends Fragment {

    Adapter adapter;
    File[] files;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    SwipeRefreshLayout refreshLayout2;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ArrayList<ModelClass> fileslist = new ArrayList<>();
    TextView placeholder;
    AdView imagesAdview;
    private InterstitialAd mInterstitialAd;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.imagesfragment_layout, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        refreshLayout2 = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout_emptyView);
        placeholder = (TextView) root.findViewById(R.id.empty_view);
        imagesAdview = (AdView) root.findViewById(R.id.imagesAdView);
        setupOnClickText();
        setRefresh();
        setuplayout();
        setbannerAd();
        initialiseAd();
        showFullAd();
        return root;
    }

    private void showFullAd() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInterstitialAd();
            }
        }, 15000);
    }

    private void setbannerAd() {
        MobileAds.initialize(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();
        imagesAdview.loadAd(adRequest);
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
            mInterstitialAd.show(getActivity());
        }
    }


    private void initialiseAd() {
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(getContext(), "ca-app-pub-4746738763099699/2967106703", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setuplayout() {
        fileslist.clear();
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(3));
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new Adapter(getActivity(), getData(), true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupOnClickText() {
        placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String str = placeholder.getText().toString();
               if(str.equalsIgnoreCase(getString(R.string.nostatusimages))) {
                   openHowToUse();
               }
            }
        });
    }

    private Boolean readDataFromPrefs() {
        SharedPreferences sh = getActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
        String uri = sh.getString("PATH", "");
        if(uri!=null) {
            if (uri.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void openHowToUse() {
        Intent intent = new Intent(getContext(), HowToUse.class);
        startActivity(intent);
    }


    private ArrayList<ModelClass> getData() {

        ModelClass f;
        if(SDK_INT>29) {
            SharedPreferences sh = getActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
            String uri = sh.getString("PATH", "");
            getContext().getContentResolver().takePersistableUriPermission(Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (uri != null) {
                DocumentFile fileDoc = DocumentFile.fromTreeUri(getActivity().getApplicationContext(),Uri.parse(uri));
                if (fileDoc.listFiles() != null){
                    DocumentFile[] files = fileDoc.listFiles();
                    for(int i = 0;i< files.length;i++) {
                        DocumentFile file = files[i];
                        f = new ModelClass(file.getUri().getPath(), file.getName(), file.getUri());
                        if (!f.getUri().toString().endsWith(".nomedia")&&(!f.getUri().toString().endsWith(".mp4"))) {
                            fileslist.add(f);
                        }
                    }
                }
            }
        }
        else {
        String targetpath = Environment.getExternalStorageDirectory().getAbsolutePath()+Constant.FOLDER_NAME+"Media/.Statuses";
        File targetdir = new File(targetpath);
        files = targetdir.listFiles();
        if(files == null) {
            String targetpath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            File targetdir1 = new File(targetpath1);
            files = targetdir1.listFiles();
        }
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new ModelClass(files[i].getAbsolutePath(), file.getName(), Uri.fromFile(file));
                if (!f.getUri().toString().endsWith(".nomedia")&&(!f.getUri().toString().endsWith(".mp4"))) {
                    fileslist.add(f);
                }
            }
        }}
        if(!fileslist.isEmpty()) {
            refreshLayout2.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
        else {
            refreshLayout2.setVisibility(View.VISIBLE);
            placeholder.setText(getString(R.string.nostatusimages));
            refreshLayout.setVisibility(View.GONE);
        }
        return fileslist;
    }

    private void setRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setuplayout();
                refreshLayout.setRefreshing(true);
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    }, 1000);
                }
            }
        });
        refreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setuplayout();
                refreshLayout2.setRefreshing(true);
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout2.setRefreshing(false);
                        }
                    }, 1000);
                }
            }
        });
    }
}
