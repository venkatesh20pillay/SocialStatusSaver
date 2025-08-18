package com.statuses.statussavers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SavedFragment : Fragment() {

    private var adapter: Adapter? = null
    private var files: Array<File>? = null
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
        val root = inflater.inflate(R.layout.savedfragment_layout, container, false) as ViewGroup
        recyclerView = root.findViewById(R.id.recyclerview)
        refreshLayout = root.findViewById(R.id.swipe)
        placeholder = root.findViewById(R.id.empty_view)
        refreshLayout2 = root.findViewById(R.id.swipeRefreshLayout_emptyView)
        recyclerView.addItemDecoration(RecyclerViewItemDecorator(3))
        recyclerView.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = gridLayoutManager
        setupOnClickText()
        setRefresh()
        setRefresh2()
        loadSavedStatuses()
        return root
    }

    private fun setRefresh() {
        refreshLayout.setOnRefreshListener {
            loadSavedStatuses()
            refreshLayout.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                refreshLayout.isRefreshing = false
            }, 1000)
        }
    }

    private fun setRefresh2() {
        refreshLayout2.setOnRefreshListener {
            loadSavedStatuses()
            refreshLayout2.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                refreshLayout2.isRefreshing = false
            }, 1000)
        }
    }

    private fun setuplayout(data: ArrayList<ModelClass>) {
        fileslist.clear()
        adapter = activity?.let { Adapter(it, data, false) }
        if (data.isNotEmpty()) {
            refreshLayout2.visibility = View.GONE
            refreshLayout.visibility = View.VISIBLE
        } else {
            refreshLayout2.visibility = View.VISIBLE
            refreshLayout.visibility = View.GONE
            if (isAdded) {
                placeholder.text = getString(R.string.nosavedstatus)
            } else {
                placeholder.text = "No saved status to show."
            }
        }
        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadSavedStatuses()
    }

    private fun loadSavedStatuses() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                val result = getSavedStatusesInBackground()
                handler.post { setuplayout(result) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                executor.shutdown()
            }
        }
    }

    private fun getSavedStatusesInBackground(): ArrayList<ModelClass> {
        val filesList = ArrayList<ModelClass>()

        try {
            // Path 1: /Status Saver
            val targetPath = Environment.getExternalStorageDirectory().absolutePath + "/Status Saver"
            val targetDir = File(targetPath)
            val files = targetDir.listFiles()
            if (files != null) {
                for (file in files) {
                    val m = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                    if (!m.uri.toString().endsWith(".nomedia")) {
                        filesList.add(m)
                    }
                }
            }
            // Path 2: /Pictures/Status Saver
            val targetPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Status Saver"
            val targetDir1 = File(targetPath1)
            val files1 = targetDir1.listFiles()
            if (files1 != null) {
                for (file in files1) {
                    val m = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                    if (!m.uri.toString().endsWith(".nomedia")) {
                        filesList.add(m)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return filesList
    }

    private fun getData(): ArrayList<ModelClass> {
        fileslist.clear()
        val targetpath = Environment.getExternalStorageDirectory().absolutePath + "/Status Saver"
        val targetdir = File(targetpath)
        files = targetdir.listFiles()
        if (files != null) {
            for (file in files!!) {
                val m = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                if (!m.uri.toString().endsWith(".nomedia")) {
                    fileslist.add(m)
                }
            }
        }
        val targetpath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Status Saver"
        val targetdir1 = File(targetpath1)
        val files1 = targetdir1.listFiles()
        if (files1 != null) {
            for (file in files1) {
                val m = ModelClass(file.absolutePath, file.name, Uri.fromFile(file))
                if (!m.uri.toString().endsWith(".nomedia")) {
                    fileslist.add(m)
                }
            }
        }
        if (fileslist.isNotEmpty()) {
            refreshLayout2.visibility = View.GONE
            refreshLayout.visibility = View.VISIBLE
        } else {
            refreshLayout2.visibility = View.VISIBLE
            refreshLayout.visibility = View.GONE
            placeholder.text = getString(R.string.nosavedstatus)
        }
        return fileslist
    }

    private fun setupOnClickText() {
        placeholder.setOnClickListener {
            val str = placeholder.text.toString()
            if (str.equals(getString(R.string.nosavedstatus), ignoreCase = true)) {
                openHowToUse()
            }
        }
    }

    private fun openHowToUse() {
        val intent = Intent(context, HowToUse::class.java)
        startActivity(intent)
    }
}
