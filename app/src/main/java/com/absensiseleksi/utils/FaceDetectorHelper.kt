package com.absensiseleksi.utils

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import javax.inject.Inject

class FaceDetectorHelper @Inject constructor() {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

    private val detector = FaceDetection.getClient(options)

    fun detectFaces(
        image: InputImage,
        onSuccess: (List<Face>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            detector.process(image)
                .addOnSuccessListener { onSuccess(it) }
                .addOnFailureListener {
                    Log.e("FaceDetector", "Face detection failed", it)
                    onError(it)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun close() {
        try {
            detector.close()
        } catch (e: Exception) {
            Log.e("FaceDetector", "Error closing face detector", e)
        }
    }
}