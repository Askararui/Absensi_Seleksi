package com.absensiseleksi.ui.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.absensiseleksi.data.repository.AbsensiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val absensiRepository: AbsensiRepository
) : ViewModel() {
    val riwayatList = absensiRepository.getAllAbsensi().asLiveData()
}