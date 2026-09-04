package com.absensiseleksi.data.remote

import com.absensiseleksi.data.remote.model.LoginRequest
import com.absensiseleksi.data.remote.model.LoginResponse
import com.absensiseleksi.data.remote.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("absensi/upload")
    suspend fun uploadAbsensi(
        @Part photo: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody
    ): Response<UploadResponse>
}