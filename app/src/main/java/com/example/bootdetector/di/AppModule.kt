package com.example.bootdetector.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.bootdetector.data.BootRepositoryImpl
import com.example.bootdetector.data.db.AppDatabase
import com.example.bootdetector.data.db.dao.BootEventDao
import com.example.bootdetector.data.db.dao.ConfigDao
import com.example.bootdetector.domain.BootRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "boot_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBootEventDao(appDatabase: AppDatabase): BootEventDao {
        return appDatabase.bootEventDao()
    }

    @Provides
    @Singleton
    fun provideConfigDao(appDatabase: AppDatabase): ConfigDao {
        return appDatabase.configDao()
    }

    @Provides
    @Singleton
    fun provideBootRepository(
        bootEventDao: BootEventDao,
        configDao: ConfigDao,
    ): BootRepository {
        return BootRepositoryImpl(bootEventDao, configDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
