package com.example.bootdetector.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bootdetector.data.db.model.BootEventEntity

@Dao
interface BootEventDao {
    @Query("SELECT * FROM BootEventEntity ORDER BY timestamp DESC")
    fun getAll(): List<BootEventEntity>

    @Insert
    fun insert(bootEventEntity: BootEventEntity)
}
