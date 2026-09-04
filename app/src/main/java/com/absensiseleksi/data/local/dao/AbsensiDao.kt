package com.absensiseleksi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absensiseleksi.data.local.entity.AbsensiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsensiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbsensi(absensi: AbsensiEntity)

    @Query("SELECT * FROM absensi WHERE isSynced = 0")
    fun getUnsyncedAbsensi(): Flow<List<AbsensiEntity>>

    @Query("SELECT * FROM absensi ORDER BY timestamp DESC")
    fun getAllAbsensi(): Flow<List<AbsensiEntity>>

    @Query("UPDATE absensi SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}