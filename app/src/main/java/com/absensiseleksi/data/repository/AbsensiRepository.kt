package com.absensiseleksi.data.repository

import com.absensiseleksi.data.local.dao.AbsensiDao
import com.absensiseleksi.data.local.entity.AbsensiEntity
import com.absensiseleksi.data.remote.ApiService
import com.absensiseleksi.data.remote.model.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbsensiRepository @Inject constructor(
    private val apiService: ApiService,
    private val absensiDao: AbsensiDao
) {
    suspend fun saveAbsensiLocal(absensi: AbsensiEntity) {
        absensiDao.insertAbsensi(absensi)
    }

    suspend fun uploadAbsensi(absensi: AbsensiEntity): Response<UploadResponse> {
        val file = File(absensi.photoPath)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        
        val userId = absensi.userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val lat = absensi.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lon = absensi.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val response = apiService.uploadAbsensi(body, userId, lat, lon)
        if (response.isSuccessful) {
            absensiDao.markAsSynced(absensi.id)
        }
        return response
    }

    fun getUnsyncedAbsensi() = absensiDao.getUnsyncedAbsensi()

    fun getAllAbsensi() = absensiDao.getAllAbsensi()
}