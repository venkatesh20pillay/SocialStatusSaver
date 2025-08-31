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

class VideosFragment : Fragment() {

    private var adapter: Adapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var fileslist = ArrayList<ModelClass>()
    private lateinit var refreshLayout2: SwipeRefreshLayout
    private lateinit var placeholder: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.videosfragment_layout, container, false)
        recyclerView = root.findViewById(R.id.recyclerview)
        refreshLayout = root.findViewById(R.id.swipe)
        placeholder = root.findViewById(R.id.empty_view)
        refreshLayout2 = root.findViewById(R.id.swipeRefreshLayout_emptyView)

        recyclerView.addItemDecoration(RecyclerViewItemDecorator(3))
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        setupOnClickText()
        setRefresh()
        setRefresh2()
        loadStatusData()

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setRefresh() {
        refreshLayout.setOnRefreshListener {
            loadStatusData()
            refreshLayout.isRefreshing = true
            Handler().postDelayed({ refreshLayout.isRefreshing = false }, 1000)
        }
    }

    private fun setRefresh2() {
        refreshLayout2.setOnRefreshListener {
            loadStatusData()
            refreshLayout2.isRefreshing = true
            Handler().postDelayed({ refreshLayout2.isRefreshing = false }, 1000)
        }
    }

    private fun setuplayout(data: ArrayList<ModelClass>?) {
        fileslist.clear()
        adapter = data?.let { activity?.let { it1 -> Adapter(it1, it, true) } }

        if (!data.isNullOrEmpty()) {
            refreshLayout2.visibility = View.GONE
            refreshLayout.visibility = View.VISIBLE
        } else {
            refreshLayout2.visibility = View.VISIBLE

            if (isAdded) {
                placeholder.text = getString(R.string.nostatusvideos)
            } else {
                placeholder.text = "No videos to show."
            }

            refreshLayout.visibility = View.GONE
        }
        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun loadStatusData() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                val result = getDataInBackground()
                handler.post {
                    setuplayout(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                executor.shutdown()
            }
        }
    }

    private fun getDataInBackground(): ArrayList<ModelClass> {
        val filesList = ArrayList<ModelClass>()
        var files: Array<File>? = null

        try {
           if (android.os.Build.VERSION.SDK_INT > 29) {
                val sh: SharedPreferences? = activity?.getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
                val uri = sh?.getString("PATH", "")
                if (!uri.isNullOrEmpty()) {
                    context?.contentResolver?.takePersistableUriPermission(
                        Uri.parse(uri),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    val fileDoc = context?.let { DocumentFile.fromTreeUri(it, Uri.parse(uri)) }
                    fileDoc?.listFiles()?.forEach { file ->
                        val f = file.uri.path?.let { file.name?.let { it1 ->
                            ModelClass(it,
                                it1, file.uri)
                        } }
                        if (file.uri.toString().endsWith(".mp4") && !file.uri.toString().endsWith(".nomedia")) {
                            if (f != null) {
                                filesList.add(f)
                            }
                        }
                    }
                }
            } else {
                val targetPath = Environment.getExternalStorageDirectory().absolutePath +
                        Constant.FOLDER_NAME + "Media/.Statuses"
                val targetDir = File(targetPath)
                files = targetDir.listFiles()

                if (files == null) {
                    val fallbackPath = Environment.getExternalStorageDirectory().absolutePath +
                            "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
                    val fallbackDir = File(fallbackPath)
                    files = fallbackDir.listFiles()
                }

                files?.forEach { file ->
                    val f = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                    if (f.uri.toString().endsWith(".mp4")) {
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
            if (str.equals(getString(R.string.nostatusvideos), ignoreCase = true)) {
                openHowToUse()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadStatusData()
    }

    private fun openHowToUse() {
        val intent = Intent(context, HowToUse::class.java)
        startActivity(intent)
    }
}
