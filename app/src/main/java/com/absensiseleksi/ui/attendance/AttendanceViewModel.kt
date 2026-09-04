package com.absensiseleksi.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.absensiseleksi.data.local.entity.AbsensiEntity
import com.absensiseleksi.data.remote.model.UploadResponse
import com.absensiseleksi.data.repository.AbsensiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val absensiRepository: AbsensiRepository
) : ViewModel() {

    private val _uploadResult = MutableLiveData<Response<UploadResponse>>()
    val uploadResult: LiveData<Response<UploadResponse>> = _uploadResult

    private val _isFaceDetected = MutableLiveData<Boolean>(false)
    val isFaceDetected: LiveData<Boolean> = _isFaceDetected

    fun setFaceDetected(detected: Boolean) {
        _isFaceDetected.postValue(detected)
    }

    fun submitAttendance(absensi: AbsensiEntity) {
        viewModelScope.launch {
            try {
                absensiRepository.saveAbsensiLocal(absensi)
                val response = absensiRepository.uploadAbsensi(absensi)
                _uploadResult.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}