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
    BroadcastReceiver broadcastReceiver;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.imagesfragment_layout, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        refreshLayout2 = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout_emptyView);
        placeholder = (TextView) root.findViewById(R.id.empty_view);
      //  setupOnActivityResult();
        setupReciever();
        setupOnClickText();
        setupLauncher();
        checkPermission();
        setRefresh();
        return root;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
               else {
                   checkPermission();
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
        if(SDK_INT>=29) {
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
         //   String targetpath1 = "/storage/emulated/0"
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
        }
        return fileslist;
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

    protected void setupLauncher() {

       someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                       checkIfAllowed();
                    }
                });
    }

    private void checkIfAllowed() {
        if(SDK_INT>=29) {
            boolean allowed = readDataFromPrefs();
            if(allowed) {
                setuplayout();
            }
        }
    }


    private void openDialog() {
        Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();
                Button button = dialog.findViewById(R.id.okbutton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        try {
                            // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},2);
                           // openSomeActivityForResult();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void checkPermission() {

        if(SDK_INT >= 29) {
            boolean allowed = readDataFromPrefs();
            if(allowed) {
                setuplayout();
            }
            else {
               // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //openDialog();
                getPermission();
            }
        }
        else {
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                setuplayout();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //Intent intent= new Intent(MainActivity.class,WRITE_EXTERNAL_STORAGE)
                //someActivityResultLauncher.launch();
            }
       }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getPermission() {
        StorageManager storageManager = (StorageManager) getActivity().getApplication().getSystemService(Context.STORAGE_SERVICE);
        Intent intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String path = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia";
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/tree/");
        scheme += "%3A" + path;
        uri = Uri.parse(scheme);
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(intent, 4321);
    }

    private void setupOnActivityResult() {
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Uri tree = result.getData().getData();
                            SharedPreferences sh = getActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sh.edit();
                            ed.putString("PATH", tree.toString());
                            ed.apply();
                            setuplayout();
                        }
                    }
                });
    }

}
