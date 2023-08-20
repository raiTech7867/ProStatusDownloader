package com.raitech.statussaver.fragment

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raitech.statussaver.R
import com.raitech.statussaver.adapter.FileAdapter
import com.raitech.statussaver.databinding.FragmentWABStatusBinding
import java.io.File

class WABStatusFragment : Fragment() {
  private lateinit var bindingStatus: FragmentWABStatusBinding
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
        return inflater.inflate(R.layout.fragment_w_a_b_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingStatus= FragmentWABStatusBinding.bind(view)
        fileLocation =
            File(Environment.getExternalStorageDirectory().toString() + "/Documents/WBusinessStatusSaver")
        list = ArrayList()
        checkPermissions(1)


        bindingStatus.swipeRefresh.setOnRefreshListener {

            try {
                createFileFolder()
                getAllFiles()
            } catch (e: Exception) {


            }
            bindingStatus.swipeRefresh.isRefreshing = false
        }

        if (list.size == 0) {
            bindingStatus.empty.visibility = View.VISIBLE
            bindingStatus.swipeRefresh.visibility = View.GONE

        } else {

            bindingStatus.empty.visibility = View.GONE
            bindingStatus.swipeRefresh.visibility = View.VISIBLE
        }

    }

    private fun createFileFolder() {

        if (!File("${Environment.getExternalStorageDirectory()}/Document/WBusinessStatusSaver/").exists())
            File("${Environment.getExternalStorageDirectory()}/Document/WBusinessStatusSaver/").mkdir()
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
            bindingStatus.rvSaved.adapter=statusAdapter
        }

    }
}