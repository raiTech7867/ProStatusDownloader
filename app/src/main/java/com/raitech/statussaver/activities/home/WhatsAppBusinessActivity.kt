package com.raitech.statussaver.activities.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.ui.NavigationUI
import com.raitech.statussaver.R
import com.raitech.statussaver.databinding.ActivityWhatsAppBusinessBinding

class WhatsAppBusinessActivity : AppCompatActivity() {

    private lateinit var businessBinding: ActivityWhatsAppBusinessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        businessBinding=ActivityWhatsAppBusinessBinding.inflate(layoutInflater)
        setContentView(businessBinding.root)
        businessBinding.toolbar.title=""
        setSupportActionBar(businessBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        init()
    }
    private fun init(){
        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_color)
        val fragments = supportFragmentManager.findFragmentById(R.id.container_view_business) as NavHost
        NavigationUI.setupWithNavController(businessBinding.bottomNavBusiness, fragments.navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}