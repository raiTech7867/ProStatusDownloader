package com.raitech.statussaver.activities.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raitech.statussaver.R

class ImageViewActivity : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var share: FloatingActionButton
    private lateinit var status: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var back:ImageView
    private lateinit var adRequest: AdRequest
    private var addCount=0
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_color)
        init()
    }
    private fun init(){
        img = findViewById(R.id.img_show)
        share = findViewById(R.id.share)
        status = findViewById(R.id.status)
        back=findViewById(R.id.back)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        adRequest = AdRequest.Builder().build()

        back.setOnClickListener {
            showAdd()
        }
        val fileUri = intent.getStringExtra("fileUri")
        val isShareable=intent.getBooleanExtra("isShareable",false)
        if (isShareable){
            share.visibility=View.VISIBLE
            status.visibility=View.VISIBLE
        }else{
            share.visibility=View.GONE
            status.visibility=View.GONE
        }
        Glide.with(applicationContext).load(fileUri).into(img)

        share.setOnClickListener {
            val sharingIntent=Intent(Intent.ACTION_SEND)
            sharingIntent.type="image/jpg"
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(fileUri))
            startActivity(Intent.createChooser(sharingIntent,"Share Image Using..."))

        }

        status.setOnClickListener {

            val sharingIntent=Intent(Intent.ACTION_SEND)
            sharingIntent.type="image/jpg"
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(fileUri))
            sharingIntent.setPackage("com.whatsapp")
            try {
                startActivity(sharingIntent)
            }catch (e:Exception){
                Toast.makeText(this,"WhatsApp not available", Toast.LENGTH_SHORT).show()
            }

        }
        MobileAds.initialize(this@ImageViewActivity) {
            loadAdd()
        }
    }
    fun loadAdd() {

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-1571351871825837/3287426478",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {

                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                }
            })


    }
    fun showAdd(){
        if (mInterstitialAd != null) {
           if (addCount%2==0){
                mInterstitialAd?.show(this)
                mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.

                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        mInterstitialAd = null
                        loadAdd()
                        onBackPressed()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        // Called when ad fails to show.

                        mInterstitialAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.

                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.

                    }
                }
            }else{
               onBackPressed()
           }

        } else {
            Log.d("TAG", "The interstitial add wasn't ready yet.")
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.privacy_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.privacy_policy -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pro-status-downloader/home"))
                startActivity(browserIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}