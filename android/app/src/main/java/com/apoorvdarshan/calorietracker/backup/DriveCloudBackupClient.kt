package com.apoorvdarshan.calorietracker.backup

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender

/** Compatibility facade for existing local data flows; this build has no cloud transport. */
class DriveCloudBackupClient(webClientId: String) {
    sealed class AuthOutcome {
        data class Token(val accessToken: String) : AuthOutcome()
        data class Resolution(val intentSender: IntentSender) : AuthOutcome()
    }
    private fun disabled(): Nothing = error("此版本已移除云备份。请使用本地导出。")
    fun accountPickerIntent(): Intent = disabled()
    fun accountFromPickerResult(data: Intent?): Account? = null
    suspend fun authorize(activity: Activity, account: Account): AuthOutcome = disabled()
    suspend fun clearSignInSession(context: Context) = Unit
    fun parseAuthorizationResult(activity: Activity, data: Intent?): String? = null
    suspend fun accountEmail(accessToken: String): String? = null
    suspend fun findBackupFileId(accessToken: String): String? = null
    suspend fun download(accessToken: String, fileId: String): ByteArray = disabled()
    suspend fun delete(accessToken: String, fileId: String) = Unit
    suspend fun revoke(accessToken: String) = Unit
    suspend fun upload(accessToken: String, bytes: ByteArray, existingFileId: String?): String = disabled()
}
