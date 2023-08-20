package com.raitech.statussaver.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.raitech.statussaver.R
import com.raitech.statussaver.adapter.FileAdapter
import com.raitech.statussaver.adapter.StatusAdapter
import com.raitech.statussaver.databinding.FragmentImageBinding
import com.raitech.statussaver.databinding.FragmentVideoBinding
import com.raitech.statussaver.models.StatusModel
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.OutputStream
import com.raitech.statussaver.databinding.FragmentSavedBinding


class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private lateinit var list: ArrayList<File>
    private lateinit var statusAdapter: FileAdapter
    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    private var fileLocation: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedBinding.bind(view)
        fileLocation =
            File(Environment.getExternalStorageDirectory().toString() + "/Documents/StatusSaver")
        list = ArrayList()
        checkPermissions(0)


        binding.swipeRefresh.setOnRefreshListener {

            try {
                createFileFolder()
                getAllFiles()
            } catch (e: Exception) {


            }
            binding.swipeRefresh.isRefreshing = false
        }

        if (list.size == 0) {
            binding.empty.visibility = View.VISIBLE
            binding.swipeRefresh.visibility = View.GONE

        } else {

            binding.empty.visibility = View.GONE
            binding.swipeRefresh.visibility = View.VISIBLE
        }

    }

    private fun createFileFolder() {

        if (!File("${Environment.getExternalStorageDirectory()}/Document/StatusSaver/").exists())
            File("${Environment.getExternalStorageDirectory()}/Document/StatusSaver/").mkdir()
                .apply {

                }
    }

    private fun checkPermissions(type: Int): Boolean {
        var result: Int
        val listPermissionNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(requireContext(), p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(p)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                (activity as Activity?)!!,
                listPermissionNeeded.toTypedArray(),
                type
            )
            return false
        } else {
            try {
                createFileFolder()
                getAllFiles()
            } catch (e: Exception) {

            }
        }
        return true
    }

    private fun getAllFiles() {
        list = ArrayList()
        val files: Array<File> = fileLocation!!.listFiles()!!
        if (files != null) {

            for (file in files) {

                list.add(file)

            }
            statusAdapter = FileAdapter(requireContext(),list)
            binding.rvSaved.adapter=statusAdapter
        }

    }
}