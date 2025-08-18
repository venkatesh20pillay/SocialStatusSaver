package com.statuses.statussavers

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class Adapter(
    private val context: Context,
    private val fileslist: ArrayList<ModelClass>,
    private val download: Boolean
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelClass = fileslist[position]

        holder.play.visibility = if (modelClass.uri.toString().endsWith(".mp4")) View.VISIBLE else View.INVISIBLE

        Glide.with(context).load(modelClass.uri).into(holder.mainstatus)

        holder.mainstatus.setOnClickListener {
            val path = modelClass.path
            val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val imagesFolder = File(directoryPath, "/Status Saver")
            val intent = if (modelClass.uri.toString().endsWith(".mp4")) {
                Intent(context, Video::class.java).apply {
                    putExtra("DEST_PATH_VIDEO", imagesFolder.absolutePath)
                    putExtra("FILE_VIDEO", path)
                    putExtra("FILENAME_VIDEO", modelClass.filename)
                    putExtra("URI_VIDEO", modelClass.uri.toString())
                    putExtra("show", if (download) "download" else "delete")
                }
            } else {
                Intent(context, Picture::class.java).apply {
                    putExtra("DEST_PATH", imagesFolder.absolutePath)
                    putExtra("FILE", path)
                    putExtra("FILENAME_IMAGE", modelClass.filename)
                    putExtra("URI_IMAGE", modelClass.uri.toString())
                    putExtra("show", if (download) "download" else "delete")
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = fileslist.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainstatus: ImageView = itemView.findViewById(R.id.thumbnailofstatus)
        val play: ImageView = itemView.findViewById(R.id.play)
    }
}
