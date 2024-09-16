package com.example.bootdetector.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bootdetector.domain.model.Config

@Entity
data class ConfigEntity(
    @PrimaryKey val id: Int = 1,
    val maxDismissals: Int = 5,
    val intervalMultiplier: Int = 20,
    val dismissalCount: Int = 0
) {
    fun toDomain(): Config = Config(
        maxDismissals = maxDismissals,
        intervalMultiplier = intervalMultiplier,
        dismissalCount = dismissalCount,
    )

    companion object {

        fun fromDomain(config: Config) = ConfigEntity(
            maxDismissals = config.maxDismissals,
            intervalMultiplier = config.intervalMultiplier,
            dismissalCount = config.dismissalCount,
        )
    }
}
