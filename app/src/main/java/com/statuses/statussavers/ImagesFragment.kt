package com.statuses.statussavers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImagesFragment : Fragment() {

    private var adapter: Adapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var refreshLayout2: SwipeRefreshLayout
    private var fileslist = ArrayList<ModelClass>()
    private lateinit var placeholder: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.imagesfragment_layout, container, false)
        recyclerView = root.findViewById(R.id.recyclerview)
        refreshLayout = root.findViewById(R.id.swipe)
        refreshLayout2 = root.findViewById(R.id.swipeRefreshLayout_emptyView)
        placeholder = root.findViewById(R.id.empty_view)

        recyclerView.addItemDecoration(RecyclerViewItemDecorator(3))
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        setupOnClickText()
        setRefresh()
        loadStatusImages()

        return root
    }

    override fun onResume() {
        super.onResume()
        loadStatusImages()
    }

    private fun setuplayout(data: ArrayList<ModelClass>) {
        fileslist.clear()
        adapter = activity?.let { Adapter(it, data, true) }

        if (data.isNotEmpty()) {
            refreshLayout2.visibility = View.GONE
            refreshLayout.visibility = View.VISIBLE
        } else {
            refreshLayout2.visibility = View.VISIBLE
            if (isAdded) {
                placeholder.text = getString(R.string.nostatusimages)
            } else {
                placeholder.text = "No images to show."
            }
            refreshLayout.visibility = View.GONE
        }

        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun loadStatusImages() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                val result = getDataInBackground()
                handler.post { setuplayout(result) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                executor.shutdown()
            }
        }
    }

    private fun getDataInBackground(): ArrayList<ModelClass> {
        val filesList = ArrayList<ModelClass>()

        try {
            if (android.os.Build.VERSION.SDK_INT > 29) {
                val sh = activity?.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
                val uriString = sh?.getString("PATH", "")

                if (!uriString.isNullOrEmpty()) {
                    val uri = Uri.parse(uriString)
                    context?.contentResolver?.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val fileDoc = context?.let { DocumentFile.fromTreeUri(it, uri) }
                    val docFiles = fileDoc?.listFiles()

                    docFiles?.forEach { file ->
                        val f = ModelClass(file.uri.path ?: "", file.name ?: "", file.uri)
                        val fileUri = f.uri.toString()
                        if (!fileUri.endsWith(".nomedia") && !fileUri.endsWith(".mp4")) {
                            filesList.add(f)
                        }
                    }
                }
            } else {
                var targetPath = Environment.getExternalStorageDirectory().absolutePath +
                        Constant.FOLDER_NAME + "Media/.Statuses"
                var targetDir = File(targetPath)
                var files = targetDir.listFiles()

                if (files == null) {
                    val fallbackPath = Environment.getExternalStorageDirectory().absolutePath +
                            "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
                    val fallbackDir = File(fallbackPath)
                    files = fallbackDir.listFiles()
                }

                files?.forEach { file ->
                    val f = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                    val fileUri = f.uri.toString()
                    if (!fileUri.endsWith(".nomedia") && !fileUri.endsWith(".mp4")) {
                        filesList.add(f)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return filesList
    }

    private fun setupOnClickText() {
        placeholder.setOnClickListener {
            val str = placeholder.text.toString()
            if (str.equals(getString(R.string.nostatusimages), ignoreCase = true)) {
                openHowToUse()
            }
        }
    }

    private fun openHowToUse() {
        val intent = Intent(context, HowToUse::class.java)
        startActivity(intent)
    }

    private fun setRefresh() {
        refreshLayout.setOnRefreshListener {
            loadStatusImages()
            refreshLayout.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                refreshLayout.isRefreshing = false
            }, 1000)
        }

        refreshLayout2.setOnRefreshListener {
            loadStatusImages()
            refreshLayout2.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                refreshLayout2.isRefreshing = false
            }, 1000)
        }
    }

}
