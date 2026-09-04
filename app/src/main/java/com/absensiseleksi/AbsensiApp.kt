package com.absensiseleksi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class AbsensiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue = packageName
        val osmPath = File(cacheDir, "osmdroid")
        osmPath.mkdirs()
        Configuration.getInstance().osmdroidTileCache = osmPath
    }
}