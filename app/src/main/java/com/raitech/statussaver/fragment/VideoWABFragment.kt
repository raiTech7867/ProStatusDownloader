package com.raitech.statussaver.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.raitech.statussaver.R
import com.raitech.statussaver.adapter.StatusAdapter
import com.raitech.statussaver.databinding.FragmentImageWABBinding
import com.raitech.statussaver.databinding.FragmentVideoWABBinding
import com.raitech.statussaver.models.StatusModel
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.OutputStream


class VideoWABFragment : Fragment() {

    private lateinit var videoWABBinding: FragmentVideoWABBinding
    private lateinit var list: ArrayList<StatusModel>
    private lateinit var statusAdapter: StatusAdapter
    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_w_a_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoWABBinding = FragmentVideoWABBinding.bind(view)
        list = ArrayList()
        val result = readDataFromPref()

        if (result) {

            val prefs = requireContext().applicationContext.getSharedPreferences(
                "DATA_PATHS",
                AppCompatActivity.MODE_PRIVATE
            )
            val uriPath = prefs.getString("PATHS", "")
            requireContext().applicationContext.contentResolver.takePersistableUriPermission(
                Uri.parse(
                    uriPath
                ), Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            if (uriPath != null) {
                val fileDoc = DocumentFile.fromTreeUri(
                    requireContext().applicationContext,
                    Uri.parse(uriPath)
                )
                list.clear()
                for (file: DocumentFile in fileDoc!!.listFiles()) {

                    if (!file.name!!.endsWith(".nomedia")) {
                        if (file.name!!.endsWith(".mp4")) {
                            val data = StatusModel(file.name!!, file.uri.toString())
                            list.add(data)
                        }
                    } else {


                    }
                }
                setUpRecyclerView(list)

            }

        } else {
            getFolderPermission()
        }

        videoWABBinding.swipeRefresh.setOnRefreshListener {

            checkPermissions(1)

            if (result) {

                val prefs = requireContext().applicationContext.getSharedPreferences(
                    "DATA_PATHS",
                    AppCompatActivity.MODE_PRIVATE
                )
                val uriPath = prefs.getString("PATHS", "")
                requireContext().applicationContext.contentResolver.takePersistableUriPermission(
                    Uri.parse(
                        uriPath
                    ), Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                if (uriPath != null) {
                    val fileDoc = DocumentFile.fromTreeUri(
                        requireContext().applicationContext,
                        Uri.parse(uriPath)
                    )
                    list.clear()
                    for (file: DocumentFile in fileDoc!!.listFiles()) {

                        if (!file.name!!.endsWith(".nomedia")) {
                            if (file.name!!.endsWith(".mp4")) {
                                val data = StatusModel(file.name!!, file.uri.toString())
                                list.add(data)
                            }
                        } else {


                        }
                    }
                    setUpRecyclerView(list)
                }

            }

            if (list.size == 0) {
                videoWABBinding.noVideo.visibility = View.VISIBLE
                videoWABBinding.swipeRefresh.visibility = View.GONE
            } else {
                videoWABBinding.noVideo.visibility = View.GONE
                videoWABBinding.swipeRefresh.visibility = View.VISIBLE
            }
            videoWABBinding.swipeRefresh.isRefreshing = false
        }


        if (list.size == 0) {
            videoWABBinding.noVideo.visibility = View.VISIBLE
            videoWABBinding.swipeRefresh.visibility = View.GONE
        }
        else {
            videoWABBinding.noVideo.visibility = View.GONE
            videoWABBinding.swipeRefresh.visibility = View.VISIBLE
        }

    }

    private fun readDataFromPref(): Boolean {

        val prefs = requireContext().applicationContext.getSharedPreferences(
            "DATA_PATHS",
            AppCompatActivity.MODE_PRIVATE
        )
        val uriPath = prefs.getString("PATHS", "")

        if (uriPath != null) {
            if (uriPath.isEmpty()) {
                return false
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 12345) {
            val treeUri = data?.data
            val sharedPrefences = requireContext().applicationContext.getSharedPreferences(
                "DATA_PATHS",
                AppCompatActivity.MODE_PRIVATE
            )
            val myEdit = sharedPrefences.edit()
            myEdit.putString("PATHS", treeUri.toString())
            myEdit.apply()
            if (treeUri != null) {
                requireContext().applicationContext.contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                val fileDoc = DocumentFile.fromTreeUri(requireContext().applicationContext, treeUri)
                list.clear()

                for (file: DocumentFile in fileDoc!!.listFiles()) {
                    if (!file.name!!.endsWith(".nomedia")) {
                        if (file.name!!.endsWith(".mp4")) {
                            val data = StatusModel(file.name, file.uri.toString())
                            list.add(data)
                        }
                    }
                    setUpRecyclerView(list)
                }

            }
        }
    }


    private fun getFolderPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val sm =
                requireContext().applicationContext.getSystemService(AppCompatActivity.STORAGE_SERVICE) as StorageManager
            val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
            val starDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses"
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
            var scheme = uri.toString()
            scheme=scheme.replace("/root/","/document/")
            scheme += "%3A$starDir"
            uri = Uri.parse(scheme)
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            startActivityForResult(intent, 12345)

        }
        checkPermissions(1)
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
            getData()
        }
        return true
    }

    private fun getData() {

        var targetPath =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            targetPath =
                Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"
        }

        val targetDirector = File(targetPath)
        val allFiles = targetDirector.listFiles()

        try {
            list.clear()

            for (file in allFiles) {
                if (!file.name.endsWith(".nomedia")) {
                    if (file.name.endsWith(".mp4")) {
                        list.add(StatusModel(file.name, file.path))
                    }
                }
            }
            setUpRecyclerView(list)
        } catch (e: Exception) {

            Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpRecyclerView(list: ArrayList<StatusModel>) {

        statusAdapter = requireContext().applicationContext?.let {
            StatusAdapter(requireContext(), list) { statusModel: StatusModel ->
                listItemClicked(statusModel)
            }
        }!!
        videoWABBinding.rvVideo.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = statusAdapter
        }


    }

    private fun listItemClicked(status: StatusModel) {
        saveFile(status)

    }

    private fun saveFile(status: StatusModel) {


        if (status.fileUri!!.endsWith(".mp4")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val inputStream =
                    requireContext().applicationContext.contentResolver.openInputStream(
                        Uri.parse(status.fileUri)
                    )

                val fileName = "status_saver_${System.currentTimeMillis()}.mp4"

                try {
                    val value = ContentValues()
                    value.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    value.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    value.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + "/WBusinessStatusSaver"
                    )
                    val uri = requireContext().applicationContext.contentResolver.insert(
                        MediaStore.Files.getContentUri("external"), value
                    )
                    val outPutStream: OutputStream = uri?.let {
                        requireContext().applicationContext.contentResolver.openOutputStream(it)
                    }!!

                    if (inputStream!=null){
                        outPutStream.write(inputStream.readBytes())
                    }

                    outPutStream.close()
                    Toast.makeText(requireContext(), "Video Saved", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()
                }

            }
            else {
                try {
                    createFileFolder()
                    val saveFilePath =
                        "${Environment.getExternalStorageDirectory()}/Document/WBusinessStatusSaver/"
                    val path: String = status.fileUri
                    val fileName = path.substring(path.lastIndexOf("/") + 1)
                    val file = File(path)
                    val destFile = File(saveFilePath)
                    try {
                        FileUtils.copyFileToDirectory(file, destFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val fileNameChange = "status_saver_${System.currentTimeMillis()}.mp4"
                    val newFile = File(saveFilePath + fileNameChange)
                    var contentType = "video/*"
                    MediaScannerConnection.scanFile(context,
                        arrayOf(newFile.absolutePath),
                        arrayOf(contentType),
                        object : MediaScannerConnection.MediaScannerConnectionClient {
                            override fun onScanCompleted(path: String?, uri: Uri?) {

                            }

                            override fun onMediaScannerConnected() {

                            }

                        }

                    )
                    val from= File(saveFilePath,fileName)
                    val to= File(saveFilePath,fileNameChange)
                    from.renameTo(to).apply {
                        Toast.makeText(requireContext(),"Video Saved", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {

                    Toast.makeText(requireContext(),""+e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
        else{


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                var bitmap: Bitmap?=null
                val fileName="status_saver_${System.currentTimeMillis()}.jpg"
                var fos: OutputStream?=null
                bitmap= MediaStore.Images.Media.getBitmap(requireContext().applicationContext.contentResolver,
                    Uri.parse(status.fileUri))
                requireContext().applicationContext.contentResolver.also {resolver->

                    val contentValue= ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME,fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE,"video/mp4")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS+"/StatusSaver")
                    }
                    val imageUri: Uri?=resolver.insert(MediaStore.Files.getContentUri("external"),contentValue)
                    fos=imageUri?.let { resolver.openOutputStream(it) }
                }

                fos.use {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG,100,it)
                    Toast.makeText(requireContext(),"Image Saved", Toast.LENGTH_SHORT).show()
                }

            }
            else {
                try {
                    createFileFolder()
                    val saveFilePath =
                        "${Environment.getExternalStorageDirectory()}/Document/BusinessStatusSaver/"
                    val path: String = status.fileUri
                    val fileName = path.substring(path.lastIndexOf("/") + 1)
                    val file = File(path)
                    val destFile = File(saveFilePath)
                    try {
                        FileUtils.copyFileToDirectory(file, destFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val fileNameChange = "status_saver_${System.currentTimeMillis()}.jpg"
                    val newFile = File(saveFilePath + fileNameChange)
                    var contentType = "image/*"
                    MediaScannerConnection.scanFile(context,
                        arrayOf(newFile.absolutePath),
                        arrayOf(contentType),
                        object : MediaScannerConnection.MediaScannerConnectionClient {
                            override fun onScanCompleted(path: String?, uri: Uri?) {

                            }

                            override fun onMediaScannerConnected() {

                            }

                        }

                    )
                    val from= File(saveFilePath,fileName)
                    val to= File(saveFilePath,fileNameChange)
                    from.renameTo(to).apply {
                        Toast.makeText(requireContext(),"Image Saved", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {

                    Toast.makeText(requireContext(),""+e.message, Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun createFileFolder() {

        if (!File("${Environment.getExternalStorageDirectory()}/Document/BusinessStatusSaver/").exists())
            File("${Environment.getExternalStorageDirectory()}/Document/BusinessStatusSaver/").mkdir()  .apply {




            }


    }
}