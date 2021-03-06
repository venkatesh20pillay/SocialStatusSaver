package com.statuses.statussavers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class VideosFragment extends Fragment {

    Adapter adapter;
    File[] files;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ArrayList<ModelClass> fileslist = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout refreshLayout2;
    TextView placeholder;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.videosfragment_layout, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        placeholder = (TextView) root.findViewById(R.id.empty_view);
        refreshLayout2 = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout_emptyView);
        setupOnClickText();
        setupReciever();
        setupLauncher();
        checkPermission();
        setRefresh();
        setRefresh2();
        return root;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                checkPermission();
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
    }

    private void setRefresh2(){
        refreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout2.setRefreshing(true);
                checkPermission();
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

    protected void setupLauncher() {

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                    }
                });
    }

    private ArrayList<ModelClass> getData() {

        ModelClass f;
        String targetpath = Environment.getExternalStorageDirectory().getAbsolutePath()+Constant.FOLDER_NAME+"Media/.Statuses";
        File targetdir = new File(targetpath);
        files = targetdir.listFiles();
        if(files == null) {
            //   String targetpath1 = "/storage/emulated/0"
            String targetpath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
            File targetdir1 = new File(targetpath1);
            files = targetdir1.listFiles();
        }
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new ModelClass(files[i].getAbsolutePath(), file.getName(), Uri.fromFile(file));
                if (f.getUri().toString().endsWith(".mp4")) {
                    fileslist.add(f);
                }
            }
        }
        if(!fileslist.isEmpty()) {
            refreshLayout2.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
        else {
            refreshLayout2.setVisibility(View.VISIBLE);
            placeholder.setText(getString(R.string.nostatusvideos));
        }
        return fileslist;
    }

    private void setupOnClickText() {
        placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = placeholder.getText().toString();
                if(str.equalsIgnoreCase(getString(R.string.nostatusvideos))) {
                    openHowToUse();
                }
                else {
                    checkPermission();
                }
            }
        });
    }

    private void openHowToUse() {
        Intent intent = new Intent(getContext(), HowToUse.class);
        startActivity(intent);
    }


    private void setupReciever() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra("message");
                setuplayout();
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter("custom-event-name"));
    }

    private void checkPermission() {

        if(SDK_INT >= 30) {
            boolean allowed = Environment.isExternalStorageManager();
            if(allowed) {
                setuplayout();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }

        else {
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                setuplayout();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

    }
}
