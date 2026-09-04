package com.absensiseleksi.data.repository

import com.absensiseleksi.data.local.PrefManager
import com.absensiseleksi.data.remote.ApiService
import com.absensiseleksi.data.remote.model.LoginRequest
import com.absensiseleksi.data.remote.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefManager: PrefManager
) {
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val response = apiService.login(LoginRequest(email, password))
        if (response.isSuccessful) {
            response.body()?.token?.let {
                prefManager.saveToken(it)
            }
        }
        return response
    }

    fun isLoggedIn(): Boolean = prefManager.getToken() != null

    fun logout() {
        prefManager.clear()
    }
}