package com.example.bootdetector.domain

import com.example.bootdetector.domain.model.BootEvent
import com.example.bootdetector.domain.model.Config
import kotlinx.coroutines.flow.Flow

interface BootRepository {
    suspend fun saveBootEvent(event: BootEvent)
    suspend fun getBootEventData(): List<BootEvent>
    suspend fun getConfig(): Flow<Config>
    suspend fun saveConfig(config: Config)
    suspend fun resetDismissalCount()
    suspend fun incrementDismissalCount()
    suspend fun getDismissalCount(): Int
}
