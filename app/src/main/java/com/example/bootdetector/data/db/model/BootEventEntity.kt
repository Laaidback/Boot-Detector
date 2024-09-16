package com.example.bootdetector.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.bootdetector.domain.model.BootEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity
data class BootEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: LocalDateTime
) {

    fun toDomain()  = BootEvent(timestamp = timestamp)

    companion object {
        fun fromDomain(domainObject: BootEvent): BootEventEntity {
            return BootEventEntity(
                timestamp = domainObject.timestamp
            )
        }
    }
}


object DateConverters {

    @TypeConverter
    fun dateToString(date: LocalDateTime): String {
        return date.toString()
    }

    @TypeConverter
    fun dateFromString(string: String): LocalDateTime {
        return LocalDateTime.parse(string)
    }
}
