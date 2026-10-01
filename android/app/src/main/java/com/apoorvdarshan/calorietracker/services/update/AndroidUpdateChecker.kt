package com.apoorvdarshan.calorietracker.services.update

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.apoorvdarshan.calorietracker.MainActivity

sealed class AndroidUpdateState {
    object Idle : AndroidUpdateState()
    object Checking : AndroidUpdateState()
    data class UpToDate(val current: String, val latest: String?) : AndroidUpdateState()
    data class Available(val current: String, val latest: String) : AndroidUpdateState()
    data class Failed(val current: String) : AndroidUpdateState()
}

object AndroidUpdateChecker {
    const val ACTION_OPEN_PLAY_STORE = "private.build.update.disabled"
    fun openPlayStore(context: Context) = Unit
    fun currentVersion(context: Context): String = com.apoorvdarshan.calorietracker.BuildConfig.VERSION_NAME
    suspend fun check(context: Context, current: String): AndroidUpdateState = AndroidUpdateState.UpToDate(current, current)
    fun playStoreLaunchPendingIntent(context: Context, requestCode: Int): PendingIntent =
        PendingIntent.getActivity(context, requestCode, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
}
