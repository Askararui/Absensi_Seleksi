package com.absensiseleksi.ui.riwayat

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.absensiseleksi.R
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RiwayatDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_detail)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val nama = intent.getStringExtra(EXTRA_NAMA)
        val timestamp = intent.getLongExtra(EXTRA_TIME, 0L)
        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        val sPath = intent.getStringExtra(EXTRA_SELFIE)
        val bPath = intent.getStringExtra(EXTRA_PHOTO)

        findViewById<TextView>(R.id.tvNamaKaryawan).text = nama
        findViewById<TextView>(R.id.tvLatitude).text = lat.toString()
        findViewById<TextView>(R.id.tvLongitude).text = lon.toString()

        val sdf = SimpleDateFormat("dd MMMM yyyy; HH:mm", Locale("id", "ID"))
        findViewById<TextView>(R.id.tvWaktu).text = sdf.format(Date(timestamp))

        sPath?.let {
            findViewById<ImageView>(R.id.ivAvatar).setImageURI(Uri.fromFile(File(it)))
        }

        bPath?.let {
            findViewById<ImageView>(R.id.ivBuktiFoto).setImageURI(Uri.fromFile(File(it)))
        }
    }

    private fun setupListeners() {
        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.addClickAnimation()
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
        const val EXTRA_SELFIE = "extra_selfie"
        const val EXTRA_PHOTO = "extra_photo"
    }
}