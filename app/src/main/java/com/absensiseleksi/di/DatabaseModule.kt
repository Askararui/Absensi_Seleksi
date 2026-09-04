package com.absensiseleksi.di

import android.content.Context
import androidx.room.Room
import com.absensiseleksi.data.local.AppDatabase
import com.absensiseleksi.data.local.dao.AbsensiDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "absensi_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAbsensiDao(database: AppDatabase): AbsensiDao {
        return database.absensiDao()
    }
}