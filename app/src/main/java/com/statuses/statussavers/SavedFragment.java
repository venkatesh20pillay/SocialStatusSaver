package com.statuses.statussavers;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SavedFragment extends Fragment {

    Adapter adapter;
    File[] files;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ArrayList<ModelClass> fileslist = new ArrayList<>();
    SwipeRefreshLayout refreshLayout2;
    TextView placeholder;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.savedfragment_layout, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        placeholder = (TextView) root.findViewById(R.id.empty_view);
        refreshLayout2 = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout_emptyView);
        setupOnClickText();
        setupLauncher();
        setRefresh();
        setRefresh2();
        loadSavedStatuses();
        return root;
    }

    private void setRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSavedStatuses();
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
    }

    private void setRefresh2(){
        refreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSavedStatuses();
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

    private void setuplayout(ArrayList<ModelClass> data) {
        fileslist.clear();
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(3));
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new Adapter(getActivity(), getData(), false);
        if (data != null && !data.isEmpty()) {
            refreshLayout2.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout2.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
            placeholder.setText(getString(R.string.nosavedstatus));
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedStatuses();
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

    private void loadSavedStatuses() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                ArrayList<ModelClass> result = getSavedStatusesInBackground();

                handler.post(() -> setuplayout(result));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });
    }

    private ArrayList<ModelClass> getSavedStatusesInBackground() {
        ArrayList<ModelClass> filesList = new ArrayList<>();
        File[] files;
        ModelClass f;

        try {
            // Path 1: /Status Saver
            String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Status Saver";
            File targetDir = new File(targetPath);
            files = targetDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    f = new ModelClass(file.getAbsolutePath(), file.getName(), Uri.fromFile(file));
                    if (!f.getUri().toString().endsWith(".nomedia")) {
                        filesList.add(f);
                    }
                }
            }

            // Path 2: /Pictures/Status Saver
            String targetPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/Status Saver";
            File targetDir1 = new File(targetPath1);
            File[] files1 = targetDir1.listFiles();

            if (files1 != null) {
                for (File file : files1) {
                    f = new ModelClass(file.getAbsolutePath(), file.getName(), Uri.fromFile(file));
                    if (!f.getUri().toString().endsWith(".nomedia")) {
                        filesList.add(f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filesList;
    }


    private ArrayList<ModelClass> getData() {

        ModelClass f;
        String targetpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Status Saver";
        File targetdir = new File(targetpath);
        files = targetdir.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new ModelClass(files[i].getAbsolutePath(), file.getName(), Uri.fromFile(file));
                if (!f.getUri().toString().endsWith(".nomedia")) {
                    fileslist.add(f);
                }
            }
        }

        String targetpath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Status Saver";
        File targetdir1 = new File(targetpath1);
        File [] files1= targetdir1.listFiles();
        if(files1 != null) {
            for (int i = 0; i < files1.length; i++) {
                File file = files1[i];
                f = new ModelClass(files1[i].getAbsolutePath(), file.getName(), Uri.fromFile(file));
                if (!f.getUri().toString().endsWith(".nomedia")) {
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
            refreshLayout.setVisibility(View.GONE);
            placeholder.setText(getString(R.string.nosavedstatus));
        }

        return fileslist;
    }

    private void setupOnClickText() {
        placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = placeholder.getText().toString();
                if(str.equalsIgnoreCase(getString(R.string.nosavedstatus))) {
                    openHowToUse();
                }
            }
        });
    }

    private void openHowToUse() {
        Intent intent = new Intent(getContext(), HowToUse.class);
        startActivity(intent);
    }

}
