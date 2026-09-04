package com.absensiseleksi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absensiseleksi.data.local.PrefManager
import com.absensiseleksi.ui.attendance.AttendanceActivity
import com.absensiseleksi.ui.login.LoginActivity
import com.absensiseleksi.ui.riwayat.RiwayatAdapter
import com.absensiseleksi.ui.riwayat.RiwayatDetailActivity
import com.absensiseleksi.ui.riwayat.RiwayatViewModel
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefManager: PrefManager
    
    private val riwayatViewModel: RiwayatViewModel by viewModels()
    private lateinit var riwayatAdapter: RiwayatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupUI()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.tvUserName).text = prefManager.getName()
        
        val lastTime = prefManager.getLastAttendanceTime()
        if (lastTime != null) {
            findViewById<TextView>(R.id.tvLastAttendanceTime).text = lastTime
        }
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatAdapter { item ->
            val intent = Intent(this, RiwayatDetailActivity::class.java).apply {
                putExtra(RiwayatDetailActivity.EXTRA_NAMA, item.userId)
                putExtra(RiwayatDetailActivity.EXTRA_TIME, item.timestamp)
                putExtra(RiwayatDetailActivity.EXTRA_LAT, item.latitude)
                putExtra(RiwayatDetailActivity.EXTRA_LON, item.longitude)
                putExtra(RiwayatDetailActivity.EXTRA_SELFIE, item.selfiePath)
                putExtra(RiwayatDetailActivity.EXTRA_PHOTO, item.photoPath)
            }
            startActivity(intent)
        }
        
        findViewById<RecyclerView>(R.id.rvRiwayat).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = riwayatAdapter
        }
    }

    private fun observeData() {
        riwayatViewModel.riwayatList.observe(this) { list ->
            riwayatAdapter.submitList(list)
        }
    }

    private fun setupListeners() {
        val btnAttendance = findViewById<View>(R.id.btnAttendance)
        btnAttendance.addClickAnimation()
        btnAttendance.setOnClickListener {
            val lastDate = prefManager.getLastAttendanceDate()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (lastDate == today) {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Info Absensi")
                    .setMessage("Anda sudah melakukan absensi hari ini.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                startActivity(Intent(this, AttendanceActivity::class.java))
            }
        }

        val btnLogout = findViewById<View>(R.id.btnLogout)
        btnLogout.addClickAnimation()
        btnLogout.setOnClickListener {
            prefManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}