package com.statuses.statussavers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.os.Build.VERSION.SDK_INT;

public class VideosFragment extends Fragment {

    Adapter adapter;
    File[] files;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    ArrayList<ModelClass> fileslist = new ArrayList<>();
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
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(3));
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        setupOnClickText();
        setRefresh();
        setRefresh2();
        loadStatusData();
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
                loadStatusData();
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
                loadStatusData();
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
        adapter = new Adapter(getActivity(), data, true);

        if (data != null && !data.isEmpty()) {
            refreshLayout2.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout2.setVisibility(View.VISIBLE);

            if (isAdded()) {
                placeholder.setText(getString(R.string.nostatusvideos));
            } else {
                // Optional: fallback text or do nothing
                placeholder.setText("No videos to show.");
            }

            refreshLayout.setVisibility(View.GONE);
        }

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadStatusData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                ArrayList<ModelClass> result = getDataInBackground();

                handler.post(() -> setuplayout(result));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });
    }

    private ArrayList<ModelClass> getDataInBackground() {
        ArrayList<ModelClass> filesList = new ArrayList<>();
        File[] files = null;
        ModelClass f;

        try {
            if (SDK_INT > 29) {
                SharedPreferences sh = getActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                String uri = sh.getString("PATH", "");
                if (uri != null && !uri.isEmpty()) {
                    getContext().getContentResolver().takePersistableUriPermission(
                            Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    DocumentFile fileDoc = DocumentFile.fromTreeUri(getContext(), Uri.parse(uri));
                    if (fileDoc != null) {
                        for (DocumentFile file : fileDoc.listFiles()) {
                            f = new ModelClass(file.getUri().getPath(), file.getName(), file.getUri());
                            if (file.getUri().toString().endsWith(".mp4") &&
                                    !file.getUri().toString().endsWith(".nomedia")) {
                                filesList.add(f);
                            }
                        }
                    }
                }
            } else {
                String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + Constant.FOLDER_NAME + "Media/.Statuses";
                File targetDir = new File(targetPath);
                files = targetDir.listFiles();

                if (files == null) {
                    String fallbackPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
                    File fallbackDir = new File(fallbackPath);
                    files = fallbackDir.listFiles();
                }

                if (files != null) {
                    for (File file : files) {
                        f = new ModelClass(file.getAbsolutePath(), file.getName(), Uri.fromFile(file));
                        if (f.getUri().toString().endsWith(".mp4")) {
                            filesList.add(f);
                        }
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
        try {
            if (SDK_INT > 29) {
                SharedPreferences sh = getActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                String uri = sh.getString("PATH", "");
                getContext().getContentResolver().takePersistableUriPermission(Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (uri != null) {
                    DocumentFile fileDoc = DocumentFile.fromTreeUri(getActivity().getApplicationContext(), Uri.parse(uri));
                    if (fileDoc.listFiles() != null) {
                        DocumentFile[] files = fileDoc.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            DocumentFile file = files[i];
                            f = new ModelClass(file.getUri().getPath(), file.getName(), file.getUri());
                            if (!f.getUri().toString().endsWith(".nomedia") && (f.getUri().toString().endsWith(".mp4"))) {
                                fileslist.add(f);
                            }
                        }
                    }
                }
            } else {
                String targetpath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.FOLDER_NAME + "Media/.Statuses";
                File targetdir = new File(targetpath);
                files = targetdir.listFiles();
                if (files == null) {
                    String targetpath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
                    File targetdir1 = new File(targetpath1);
                    files = targetdir1.listFiles();
                }
            }
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    f = new ModelClass(files[i].getAbsolutePath(), file.getName(), Uri.fromFile(file));
                    if (f.getUri().toString().endsWith(".mp4")) {
                        fileslist.add(f);
                    }
                }
            }
            if (!fileslist.isEmpty()) {
                refreshLayout2.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            } else {
                refreshLayout2.setVisibility(View.VISIBLE);
                placeholder.setText(getString(R.string.nostatusvideos));
                refreshLayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            
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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatusData();
    }

    private void openHowToUse() {
        Intent intent = new Intent(getContext(), HowToUse.class);
        startActivity(intent);
    }
}
