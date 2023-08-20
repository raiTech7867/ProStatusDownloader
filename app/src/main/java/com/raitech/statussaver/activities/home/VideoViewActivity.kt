package com.raitech.statussaver.activities.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raitech.statussaver.R

class VideoViewActivity : AppCompatActivity() {

    private lateinit var videoView:VideoView
    private lateinit var img_share:FloatingActionButton
    private lateinit var back:ImageView
    private lateinit var status:FloatingActionButton
    private lateinit var toolbar: Toolbar
    private var mInterstitialAd: InterstitialAd? = null
    private var addCount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_color)
        init()

    }
    private fun init(){
        videoView=findViewById(R.id.v_view)
        img_share=findViewById(R.id.share)
        status=findViewById(R.id.status)
        back=findViewById(R.id.back)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        back.setOnClickListener {
            showAdd()
        }
        val fileUri = intent.getStringExtra("fileUri")
        val isShareable=intent.getBooleanExtra("isShareable",false)
        if (isShareable){
            img_share.visibility= View.VISIBLE
            status.visibility= View.VISIBLE
        }else{
            img_share.visibility= View.GONE
            status.visibility= View.GONE
        }
        try {
            val mediaController=MediaController(this)
            mediaController.setAnchorView(videoView)
            val video= Uri.parse(fileUri)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(video)

            videoView.setOnPreparedListener {
                videoView.start()
            }

        }catch (e:Exception){

            e.printStackTrace()

        }

        img_share.setOnClickListener {

            val sharingIntent=Intent(Intent.ACTION_SEND)
            sharingIntent.type="video/mp4"
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(fileUri))
            startActivity(sharingIntent)
            Toast.makeText(this,"Sharing Video Using...",Toast.LENGTH_SHORT).show()


        }
        status.setOnClickListener {
            val sharingIntent=Intent(Intent.ACTION_SEND)
            sharingIntent.type="video/mp4"
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(fileUri))
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sharingIntent.setPackage("com.whatsapp")
         try {
            startActivity(sharingIntent)
            }catch (e:Exception){
            Toast.makeText(this,"WhatsApp not available",Toast.LENGTH_SHORT).show()
            }
        }
        MobileAds.initialize(this) {
            loadAdd()
        }
    }
    fun loadAdd() {

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
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
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
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