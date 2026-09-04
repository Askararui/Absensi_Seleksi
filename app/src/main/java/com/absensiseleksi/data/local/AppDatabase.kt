package com.absensiseleksi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.absensiseleksi.data.local.dao.AbsensiDao
import com.absensiseleksi.data.local.entity.AbsensiEntity

@Database(entities = [AbsensiEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun absensiDao(): AbsensiDao
}