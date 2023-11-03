package com.example.sahha

import android.app.Application
import android.content.Context
import android.util.Log
import sdk.sahha.android.source.Sahha
import sdk.sahha.android.source.SahhaEnvironment
import sdk.sahha.android.source.SahhaSettings

/**
 * @author evan.xie
 * @time 2023/11/2 21:26
 * @description
 */
object SahhaManager {
    private val TAG = "SahhaManager"

    const val APP_ID = "29E3DDPPIBHNMJ4UZKHVEUP9C3FHUPDU"
    const val APP_SECRET = "bQzU+H4leetZZ/JjTMwz4Nv/yGY5BVnpegVzv5hbbyE="
    const val EXTERNAL_ID = "8XZCUVDOLO7JTKVFCT29ANT75JKZFBIY"
    fun config(application: Application) {
        Sahha.configure(
            application,
            SahhaSettings(environment = SahhaEnvironment.sandbox)
        ) { error, success ->
            Log.v(TAG, "config():$error, success:$success")
            if (error != null) {
                println(error)
            } else {
                println(success.toString())
            }
        }
    }

    fun authenticate() {
        Sahha.authenticate(APP_ID, APP_SECRET, EXTERNAL_ID) { error, success ->
            var greeting = ""
            if (success) greeting = "Successful"
            else greeting = error ?: "Failed"
            Log.d(TAG, "authenticate(): error:$error, success:$success greeting:$greeting")
        }
    }

    fun getSensorStatus(context: Context) {
        Sahha.getSensorStatus(context) { error, sensorStatus ->
            Log.d(TAG, "getSensorStatus(): error:$error, sensorStatus:$sensorStatus")
            if (error != null) {
                println(error)
            } else {
                println(sensorStatus.name)
            }
        }
    }

    fun enableSensors(context: Context) {
        Sahha.enableSensors(context) { error, sensorStatus ->
            Log.d(TAG, "enableSensors(): error:$error, sensorStatus:$sensorStatus")
            if (error != null) {
                println(error)
            } else {
                println(sensorStatus.name)
            }
        }
    }

    fun postSensorData() {
        Sahha.postSensorData { error, success ->
            Log.d(TAG, "postSensorData(): error:$error, success:$success")
            var manualPost = "xxx"
            if (success) manualPost = "Successful"
            else manualPost = error ?: "Failed"

        }
    }
}

