package com.example.bootdetector.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bootdetector.data.db.model.ConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    // Fetch the current configuration, if any
    @Query("SELECT * FROM ConfigEntity WHERE id = 1")
    fun getConfig(): Flow<ConfigEntity?>

    // Insert or update the configuration. On conflict, replace the existing config.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConfig(config: ConfigEntity)
}
