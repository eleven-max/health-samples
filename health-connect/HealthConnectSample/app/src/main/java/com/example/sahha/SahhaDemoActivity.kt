package com.example.sahha

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthconnectsample.R
import sdk.sahha.android.source.Sahha


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

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(sdk.sahha.android.data.receiver.PhoneScreenStateReceiver(), filter)

        SahhaManager.config(application)
        SahhaManager.authenticate()
        SahhaManager.enableSensors(applicationContext)
        SahhaManager.getSensorStatus(applicationContext)
        SahhaManager.postSensorData()

        Sahha
    }
}