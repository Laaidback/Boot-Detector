package com.example.bootdetector.data

import com.example.bootdetector.data.db.dao.BootEventDao
import com.example.bootdetector.data.db.dao.ConfigDao
import com.example.bootdetector.data.db.model.BootEventEntity
import com.example.bootdetector.data.db.model.ConfigEntity
import com.example.bootdetector.domain.BootRepository
import com.example.bootdetector.domain.model.BootEvent
import com.example.bootdetector.domain.model.Config
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BootRepositoryImpl @Inject constructor(
    private val bootEventDao: BootEventDao,
    private val configDao: ConfigDao,
) : BootRepository {


    override suspend fun saveBootEvent(event: BootEvent) {
        val bootEventEntity = BootEventEntity.fromDomain(event)
        bootEventDao.insert(bootEventEntity)
    }

    override suspend fun getBootEventData(): List<BootEvent> {
        return bootEventDao.getAll().map {
            it.toDomain()
        }
    }

    override suspend fun getConfig(): Flow<Config> {
        return configDao.getConfig().map { it?.toDomain() ?: Config() }
    }

    override suspend fun saveConfig(config: Config) {
        configDao.updateConfig(ConfigEntity.fromDomain(config))
    }

    override suspend fun resetDismissalCount() {
//        val config = getConfig().copy(dismissalCount = 0)
//        saveConfig(config)
    }

    override suspend fun incrementDismissalCount() {
        val config = getConfig().first().run {
            copy(dismissalCount = dismissalCount + 1)
        }
        saveConfig(config)
    }

    override suspend fun getDismissalCount(): Int {
        return getConfig().first().dismissalCount
    }
}
