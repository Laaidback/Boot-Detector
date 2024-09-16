package com.example.bootdetector.domain

import com.example.bootdetector.domain.model.BootEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class SaveBootEventUseCase @Inject constructor(
    private val bootRepository: BootRepository,
) {

    suspend fun execute() {
        val currentMoment: Instant = Clock.System.now()
        val timestamp: LocalDateTime =
            currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
        val event = BootEvent(timestamp = timestamp)
        bootRepository.saveBootEvent(event)
    }
}
