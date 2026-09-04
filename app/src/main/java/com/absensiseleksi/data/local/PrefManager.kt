package com.absensiseleksi.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("absensi_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun saveName(name: String) {
        prefs.edit().putString("name", name).apply()
    }

    fun getName(): String {
        return prefs.getString("name", "User") ?: "User"
    }

    fun saveLastAttendanceDate(date: String) {
        prefs.edit().putString("last_attendance_date", date).apply()
    }

    fun getLastAttendanceDate(): String? {
        return prefs.getString("last_attendance_date", null)
    }

    fun saveLastAttendanceTime(time: String) {
        prefs.edit().putString("last_attendance_time", time).apply()
    }

    fun getLastAttendanceTime(): String? {
        return prefs.getString("last_attendance_time", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}