package com.raitech.statussaver.activities.home

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.webkit.DownloadListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raitech.statussaver.R
import com.raitech.statussaver.databinding.ActivityInstaGramBinding


class InstaGramActivity : AppCompatActivity() {
   private lateinit var instaGramBinding: ActivityInstaGramBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instaGramBinding=ActivityInstaGramBinding.inflate(layoutInflater)
        setContentView(instaGramBinding.root)

        init()
    }

    private fun init() {
        instaGramBinding.toolbar.title=""
        setSupportActionBar(instaGramBinding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       instaGramBinding.instaWeb.settings.javaScriptEnabled=true

        instaGramBinding.instaWeb.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
           val i=Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
        })

        instaGramBinding.instaWeb.loadUrl("")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.privacy_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home-> {
                finish()
                return true
            }
            R.id.privacy_policy -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pro-status-downloader/home"))
                startActivity(browserIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}