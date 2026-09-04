package com.absensiseleksi.ui.attendance

import android.content.Intent
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.absensiseleksi.R
import com.absensiseleksi.databinding.ActivityAttendanceBinding
import com.absensiseleksi.utils.FaceDetectorHelper
import com.absensiseleksi.utils.ViewUtils.addClickAnimation
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import android.preference.PreferenceManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityAttendanceBinding
    private val viewModel: AttendanceViewModel by viewModels()

    @Inject
    lateinit var faceDetectorHelper: FaceDetectorHelper

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var isAnalyzing = true

    private lateinit var locationManager: LocationManager
    private var currentMarker: Marker? = null
    private var userLatitude: Double = -6.1754
    private var userLongitude: Double = 106.8272

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        checkLocationStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocationStatus()

        setupMap()
        setupObservers()
        setupListeners()
    }

    private fun allPermissionsGranted() = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationStatus() {
        if (allPermissionsGranted() && isLocationEnabled()) {
            startFlow()
        } else {
            showWarning()
        }
    }

    private fun showWarning() {
        binding.clWarning.visibility = View.VISIBLE
        binding.btnAttendance.isEnabled = false
        binding.btnAttendance.backgroundTintList = ContextCompat.getColorStateList(this, R.color.btn_disabled_gray)
        binding.btnAttendance.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.btnAttendance.iconTint = ContextCompat.getColorStateList(this, R.color.black)
    }

    private fun startFlow() {
        binding.clWarning.visibility = View.GONE
        binding.btnAttendance.isEnabled = true
        binding.btnAttendance.backgroundTintList = ContextCompat.getColorStateList(this, R.color.brand_orange)
        binding.btnAttendance.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.btnAttendance.iconTint = ContextCompat.getColorStateList(this, R.color.white)
        
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, this)
            
            val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val bestLocation = lastGps ?: lastNetwork
            bestLocation?.let { onLocationChanged(it) }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (isAnalyzing) {
                            processImageProxy(imageProxy)
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera binding failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            getExternalFilesDir(null),
            "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext, "Gagal mengambil foto: ${exc.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    isAnalyzing = false
                    try {
                        ProcessCameraProvider.getInstance(this@AttendanceActivity).get().unbindAll()
                    } catch (e: Exception) {}

                    saveToGallery(photoFile)

                    val intent = Intent(this@AttendanceActivity, AttendanceDetailActivity::class.java).apply {
                        putExtra(AttendanceDetailActivity.EXTRA_LATITUDE, userLatitude)
                        putExtra(AttendanceDetailActivity.EXTRA_LONGITUDE, userLongitude)
                        putExtra(AttendanceDetailActivity.EXTRA_IMAGE_URI, photoFile.absolutePath)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        )
    }

    private fun saveToGallery(file: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AbsensiSeleksi")
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: androidx.camera.core.ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            faceDetectorHelper.detectFaces(
                image,
                onSuccess = { faces ->
                    runOnUiThread {
                        viewModel.setFaceDetected(faces.isNotEmpty())
                    }
                    imageProxy.close()
                },
                onError = {
                    imageProxy.close()
                }
            )
        } else {
            imageProxy.close()
        }
    }

    private fun setupMap() {
        binding.mapView.setMultiTouchControls(true)
        val mapController = binding.mapView.controller
        mapController.setZoom(17.0)
        
        val startPoint = GeoPoint(userLatitude, userLongitude)
        mapController.setCenter(startPoint)

        currentMarker = Marker(binding.mapView).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Lokasi Anda"
        }
        binding.mapView.overlays.add(currentMarker)
    }

    private fun setupObservers() {
        viewModel.isFaceDetected.observe(this) { detected ->
            binding.tvFaceStatus.text = if (detected) "Wajah Terdeteksi" else "Posisikan wajah di dalam bingkai"
            binding.ivCapture.isEnabled = detected
            binding.ivCapture.alpha = if (detected) 1.0f else 0.5f
        }
    }

    private fun setupListeners() {
        binding.btnBack.addClickAnimation()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAttendance.addClickAnimation()
        binding.btnAttendance.setOnClickListener {
            binding.clMapLayer.visibility = View.GONE
            binding.clCameraLayer.visibility = View.VISIBLE
            startCamera()
        }

        binding.ivCapture.addClickAnimation()
        binding.ivCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnActivate.addClickAnimation()
        binding.btnActivate.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) Manifest.permission.WRITE_EXTERNAL_STORAGE else Manifest.permission.ACCESS_MEDIA_LOCATION
                    ).filterNotNull().toTypedArray()
                )
            } else if (!isLocationEnabled()) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        userLatitude = location.latitude
        userLongitude = location.longitude
        
        val point = GeoPoint(userLatitude, userLongitude)
        binding.mapView.controller.animateTo(point)
        
        currentMarker?.position = point
        binding.mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        isAnalyzing = true
        checkLocationStatus()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        isAnalyzing = false
        cameraExecutor.shutdown()
        faceDetectorHelper.close()
    }
}