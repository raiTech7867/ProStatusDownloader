package com.raitech.statussaver.activities.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raitech.statussaver.BuildConfig
import com.raitech.statussaver.R
import com.raitech.statussaver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

      private lateinit var toolbar:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       toolbar=findViewById(R.id.toolbar)
        toolbar.title=""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_color)
        val fragments = supportFragmentManager.findFragmentById(R.id.container_view) as NavHost
        NavigationUI.setupWithNavController(binding.bottomNav, fragments.navController)
        binding.shareUrl.setOnClickListener {
            val appPackageName = BuildConfig.APPLICATION_ID
            val appName = this.getString(R.string.app_name)
            val shareBodyText = "https://play.google.com/store/apps/details?id=$appPackageName"

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, appName)
                putExtra(Intent.EXTRA_TEXT, shareBodyText)
            }
           startActivity(Intent.createChooser(sendIntent, null))
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->finish()

        }
        return true
    }

}