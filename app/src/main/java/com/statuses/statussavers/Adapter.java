package com.statuses.statussavers;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<ModelClass> fileslist;
    boolean download;

    public Adapter(Context context, ArrayList<ModelClass> fileslist, boolean download) {
        this.context = context;
        this.fileslist = fileslist;
        this.download = download;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        final ModelClass modelClass = fileslist.get(position);
        if(modelClass.getUri().toString().endsWith(".mp4")) {
            holder.play.setVisibility(View.VISIBLE);
        }
        else {
            holder.play.setVisibility(View.INVISIBLE);
        }

        Glide.with(context).load(modelClass.getUri()).into(holder.mainstatus);

        holder.mainstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelClass.getUri().toString().endsWith(".mp4")) {
                    final String path = fileslist.get(position).getPath();
                    String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File imagesFolder = new File(directoryPath, "/Status Saver");
                    Intent intent = new Intent(context, Video.class);
                    intent.putExtra("DEST_PATH_VIDEO", imagesFolder.getAbsolutePath());
                    intent.putExtra("FILE_VIDEO", path);
                    intent.putExtra("FILENAME_VIDEO", modelClass.getFilename());
                    intent.putExtra("URI_VIDEO", modelClass.getUri().toString());
                    String show = "download";
                    if(!download) {
                        show = "delete";
                    }
                    intent.putExtra("show", show);
                    context.startActivity(intent);
                }
                else {
                    final String path = fileslist.get(position).getPath();
                    String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File imagesFolder = new File(directoryPath, "/Status Saver");
                    Intent intent = new Intent(context, Picture.class);
                    intent.putExtra("DEST_PATH", imagesFolder.getAbsolutePath());
                    intent.putExtra("FILE", path);
                    intent.putExtra("FILENAME_IMAGE", modelClass.getFilename());
                    intent.putExtra("URI_IMAGE", modelClass.getUri().toString());
                    String show = "download";
                    if(!download) {
                        show = "delete";
                    }
                    intent.putExtra("show", show);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mainstatus, play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainstatus = itemView.findViewById(R.id.thumbnailofstatus);
            play = itemView.findViewById(R.id.play);
        }
    }
}
