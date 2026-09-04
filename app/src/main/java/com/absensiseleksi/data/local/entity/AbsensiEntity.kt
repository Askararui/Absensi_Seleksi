package com.absensiseleksi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "absensi")
data class AbsensiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val selfiePath: String,
    val photoPath: String,
    val isSynced: Boolean = false
)