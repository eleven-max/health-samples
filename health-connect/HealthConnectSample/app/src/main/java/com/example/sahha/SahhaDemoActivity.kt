package com.example.sahha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthconnectsample.R
//import com.example.rook.RookManager
import com.example.rook.RookManager2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SahhaDemoActivity : AppCompatActivity() {

    companion object  {
        fun start(context: Context) {
            val intent = Intent(context, SahhaDemoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sahha_demo)

//        RookManager.init(this)
//        RookManager.getAuthorization()
//        RookManager.initRookHealthConnect(this)
//        RookManager.checkPermissions(this)

        GlobalScope.launch {
            val rookManager2 = RookManager2()
            rookManager2.initialize(this@SahhaDemoActivity)
            rookManager2.updateUserId()
            rookManager2.checkPermissions()
        }

//        val filter = IntentFilter()
//        filter.addAction(Intent.ACTION_SCREEN_ON)
//        filter.addAction(Intent.ACTION_SCREEN_OFF)
//        registerReceiver(sdk.sahha.android.data.receiver.PhoneScreenStateReceiver(), filter)

        findViewById<Button>(R.id.button_config).setOnClickListener {
            SahhaManager.config(application)
        }
        findViewById<Button>(R.id.button_authenticate).setOnClickListener {
            SahhaManager.authenticate()
        }
        findViewById<Button>(R.id.button_enableSensors).setOnClickListener {
            SahhaManager.enableSensors(applicationContext)
        }
        findViewById<Button>(R.id.button_getSensorStatus).setOnClickListener {
            SahhaManager.getSensorStatus(applicationContext)
        }
        findViewById<Button>(R.id.button_postSensorData).setOnClickListener {
            SahhaManager.postSensorData()
        }
    }
}