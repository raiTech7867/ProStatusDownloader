package com.raitech.statussaver.activities.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.raitech.statussaver.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class HomeActivity : AppCompatActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE
    private val REQUEST_CODE = 100
    private lateinit var whatsApp_container: CardView
    private lateinit var context:Context
    // private lateinit var insta_container:CardView
    private lateinit var business_container: CardView
    private lateinit var mobNumber: EditText
    private lateinit var message: EditText
    private lateinit var sent: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        context=this
        appUpdateManager = AppUpdateManagerFactory.create(this)
        if (updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        init()
    }

    private fun init() {
        whatsApp_container = findViewById(R.id.card_whats_app)
        // insta_container=findViewById(R.id.card_instagram)
        business_container = findViewById(R.id.card_business)
        mobNumber=findViewById(R.id.et_mobNo)
        message=findViewById(R.id.et_message)
        sent=findViewById(R.id.msg_sent)
        whatsApp_container.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
//        insta_container.setOnClickListener {
//            val i=Intent(this,InstaGramActivity::class.java)
//            startActivity(i)
//        }
        business_container.setOnClickListener {
            val i = Intent(this, WhatsAppBusinessActivity::class.java)
            startActivity(i)
        }
        checkUpdate()
        sent.setOnClickListener {
            validate()
        }

    }

    private fun validate() {
        if (mobNumber.text.toString().isNullOrEmpty()){
            Toast.makeText(context,"Please provide mobile number",Toast.LENGTH_SHORT).show()
        }else if ( mobNumber.text.toString().length < 10){
            Toast.makeText(context,"Phone No is not valid",Toast.LENGTH_SHORT).show()
        }else if (message.text.toString().isNullOrEmpty()){
            Toast.makeText(context,"Please Enter Some Message",Toast.LENGTH_SHORT).show()
        }else{
            openWhatsApp(mobNumber.text.toString(),message.text.toString())
        }
    }

    private fun openWhatsApp(mobNo: String, message: String) {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.action = Intent.ACTION_VIEW
        sendIntent.setPackage("com.whatsapp")
        val url =
            "https://api.whatsapp.com/send?phone=" + "+91 "+mobNo + "&text=" + message
        sendIntent.data = Uri.parse(url)
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            startActivity(sendIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType==AppUpdateType.IMMEDIATE){
            inProgressUpdate()
        }
    }
    private val installStateUpdateListener=InstallStateUpdatedListener{state->
        if (state.installStatus()==InstallStatus.DOWNLOADED){
            Toast.makeText(applicationContext,"Download successfully.Restarting app in 5 seconds.",Toast.LENGTH_LONG).show()
            lifecycleScope.launch{
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }

    }

    private fun inProgressUpdate() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager?.startUpdateFlowForResult(
                    updateInfo,
                    updateType,
                    this,
                    123
                )
            }
        }
    }

    private fun checkUpdate() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info ->

            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed=when(updateType){
                AppUpdateType.FLEXIBLE->info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE->info.isImmediateUpdateAllowed
                else->false

            }
            if (isUpdateAvailable && isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    123
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==123){
            if (resultCode!= RESULT_OK){
                println("Something went wrong updateing...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }
    }

}