package com.absensiseleksi.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.absensiseleksi.MainActivity
import com.absensiseleksi.R
import com.absensiseleksi.data.local.PrefManager
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceSuccessActivity : AppCompatActivity() {

    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_success)

        displayInfo()
        setupListeners()
    }

    private fun displayInfo() {
        findViewById<TextView>(R.id.tvUserName).text = prefManager.getName()
        findViewById<TextView>(R.id.tvLocation).text = "Kantor Pusat"
        
        val timeSdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        findViewById<TextView>(R.id.tvTime).text = timeSdf.format(Date())
        
        val dateSdf = SimpleDateFormat("'Today, 'dd MMMM yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDate).text = dateSdf.format(Date())
    }

    private fun setupListeners() {
        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.visibility = View.VISIBLE
        btnBack.addClickAnimation()
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val btnHome = findViewById<View>(R.id.btnHome)
        btnHome.addClickAnimation()
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}