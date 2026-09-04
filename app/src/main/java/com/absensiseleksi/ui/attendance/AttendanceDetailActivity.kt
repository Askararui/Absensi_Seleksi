package com.absensiseleksi.ui.attendance

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.absensiseleksi.R
import com.absensiseleksi.data.local.PrefManager
import com.absensiseleksi.data.local.entity.AbsensiEntity
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceDetailActivity : AppCompatActivity() {

    private val viewModel: AttendanceViewModel by viewModels()

    @Inject
    lateinit var prefManager: PrefManager

    private var selfiePath: String? = null
    private var buktiPath: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                try {
                    val file = copyUriToFile(uri)
                    buktiPath = file.absolutePath
                    
                    val ivPreview = findViewById<ImageView>(R.id.ivPreviewBukti)
                    ivPreview.visibility = View.VISIBLE
                    ivPreview.setImageURI(uri)
                    
                    findViewById<TextView>(R.id.tvHintUpload).text = "Foto berhasil dipilih"
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memproses gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_detail)

        getDataFromIntent()
        displayInfo()
        setupListeners()
    }

    private fun getDataFromIntent() {
        latitude = intent.getDoubleExtra(EXTRA_LATITUDE, -6.7725)
        longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 110.4288)
        selfiePath = intent.getStringExtra(EXTRA_IMAGE_URI)
    }

    private fun displayInfo() {
        findViewById<TextView>(R.id.tvEmployeeName).text = prefManager.getName()
        findViewById<TextView>(R.id.tvLatitude).text = latitude.toString()
        findViewById<TextView>(R.id.tvLongitude).text = longitude.toString()

        val sdf = SimpleDateFormat("dd MMMM yyyy; HH:mm", Locale("id", "ID"))
        findViewById<TextView>(R.id.tvAttendanceTime).text = sdf.format(Date())
        
        selfiePath?.let { path ->
            findViewById<ImageView>(R.id.ivAvatar).setImageURI(Uri.fromFile(File(path)))
        }
    }

    private fun copyUriToFile(uri: Uri): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val myFile = File(getExternalFilesDir(null), "BUKTI_$timeStamp.jpg")
        
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(myFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var length: Int
                while (true) {
                    length = inputStream.read(buffer)
                    if (length <= 0) break
                    outputStream.write(buffer, 0, length)
                }
            }
        } ?: throw Exception("Gagal membuka aliran data gambar")
        
        return myFile
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnBack).addClickAnimation()
        findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        findViewById<View>(R.id.btnBrowse).addClickAnimation()
        findViewById<View>(R.id.btnBrowse).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcherGallery.launch(intent)
        }

        val btnSend = findViewById<View>(R.id.btnSend)
        btnSend.addClickAnimation()
        btnSend.setOnClickListener {
            if (buktiPath == null) {
                Toast.makeText(this, "Silahkan pilih foto bukti terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val absensi = AbsensiEntity(
                userId = prefManager.getName(),
                timestamp = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
                selfiePath = selfiePath ?: "",
                photoPath = buktiPath ?: ""
            )
            viewModel.submitAttendance(absensi)

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            prefManager.saveLastAttendanceDate(currentDate)
            
            val currentTime = SimpleDateFormat("HH:mm:ss 'WIB'", Locale.getDefault()).format(Date())
            prefManager.saveLastAttendanceTime(currentTime)

            startActivity(Intent(this, AttendanceSuccessActivity::class.java))
            finish()
        }
    }

    companion object {
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}