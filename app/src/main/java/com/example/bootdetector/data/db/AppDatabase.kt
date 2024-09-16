package com.example.bootdetector.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bootdetector.data.db.dao.BootEventDao
import com.example.bootdetector.data.db.dao.ConfigDao
import com.example.bootdetector.data.db.model.BootEventEntity
import com.example.bootdetector.data.db.model.ConfigEntity
import com.example.bootdetector.data.db.model.DateConverters

@Database(
    entities = [
        BootEventEntity::class,
        ConfigEntity::class,
    ],
    version = 1,
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bootEventDao(): BootEventDao
    abstract fun configDao(): ConfigDao
}
