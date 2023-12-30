package com.example.rook

import android.content.Context
import android.util.Log
import com.rookmotion.rook.sdk.RookConfigurationManager
import com.rookmotion.rook.sdk.RookHealthPermissionsManager
import com.rookmotion.rook.sdk.domain.enums.HealthPermission
import com.rookmotion.rook.sdk.domain.environment.RookEnvironment
import com.rookmotion.rook.sdk.domain.exception.DeviceNotSupportedException
import com.rookmotion.rook.sdk.domain.exception.HealthConnectNotInstalledException
import com.rookmotion.rook.sdk.domain.exception.MissingConfigurationException
import com.rookmotion.rook.sdk.domain.exception.SDKNotInitializedException
import com.rookmotion.rook.sdk.domain.exception.TimeoutException
import com.rookmotion.rook.sdk.domain.exception.UserNotInitializedException
import com.rookmotion.rook.sdk.domain.model.RookConfiguration
import sdk.sahha.android.BuildConfig

/**
 * @author evan.xie
 * @time 2023/12/19 22:31
 * @description
 */
class RookManager2 {

    private val TAG = "RookManager2"
    private  val CLIENT_UUID = "19eee479-e17b-4e0d-9730-5e5c377d0b14"
    private  val SECRET_KEY = "jSJSDOlrGVFt3H2vEB4b5O6wzR2FwkYXT7Xi"

    private val userID = "G3x1-2a.bC"
//    private var context: Context? = null

    private var configurationManager: RookConfigurationManager? = null
    suspend fun initialize(context: Context) {
//        this.context = context

        configurationManager = RookConfigurationManager(context)
        val environment =
            if (BuildConfig.DEBUG) RookEnvironment.SANDBOX else RookEnvironment.PRODUCTION

        val rookConfiguration = RookConfiguration(
            CLIENT_UUID,
            SECRET_KEY,
            environment,
        )

        if (BuildConfig.DEBUG) {
            configurationManager?.enableLocalLogs() // MUST be called first if you want to enable native logs
        }

        configurationManager?.setConfiguration(rookConfiguration)

        val result = configurationManager?.initRook()

        result?.fold(
            {
                // SDK initialized successfully
                Log.v(TAG, "SDK initialized successfully")
            },
            {
                // Error initializing SDK

                Log.v(TAG, "Error initializing SDK")

                val error = when (it) {
                    is MissingConfigurationException -> "MissingConfigurationException: ${it.message}"
                    is TimeoutException -> "TimeoutException: ${it.message}"
                    else -> it.localizedMessage
                }
            }
        )
    }

    suspend fun updateUserId() {
        val result = configurationManager?.updateUserID(userID)

        result?.fold(
            {
                // userID updated successfully
                Log.v(TAG, "userID updated successfully")
            },
            {
                // Error updating userID

                val error = when (it) {
                    is SDKNotInitializedException -> "SDKNotInitializedException: ${it.message}"
                    is TimeoutException -> "TimeoutException: ${it.message}"
                    else -> it.localizedMessage
                }
                Log.v(TAG, "Error updating userID:$error")
            }
        )
    }
    suspend fun checkPermissions() {
        configurationManager?.let { manager ->
            val rookHealthPermissionsManager = RookHealthPermissionsManager(manager)
            val result = rookHealthPermissionsManager.checkPermissions(HealthPermission.ALL)

            result.fold(
                {
                    val message = if (it) {
                        "All permissions are granted! You can skip the next 2 steps"
                    } else {
                        "There are missing permissions. Please grant them"
                    }
                    Log.v(TAG, "check Permissions:$message")
                },
                {
                    // Error checking all permissions
                    Log.v(TAG, "Error checking all permissions")
                    val error = when (it) {
                        is SDKNotInitializedException -> "SDKNotInitializedException: ${it.message}"
                        is UserNotInitializedException -> "UserNotInitializedException: ${it.message}"
                        is HealthConnectNotInstalledException -> "HealthConnectNotInstalledException: ${it.message}"
                        is DeviceNotSupportedException -> "DeviceNotSupportedException: ${it.message}"
                        else -> it.localizedMessage
                    }
                    Log.w(TAG, "Error checking all permissions. $error")
                }
            )
        }


    }
}