// core/data/local/Converters.kt
package com.johan.racketmatchapp.core.data.local

import androidx.room.TypeConverter
import com.johan.racketmatchapp.core.data.model.SportType
import java.util.Date

class Converters {

    // Enum <-> String
    @TypeConverter
    fun fromSportType(value: SportType): String = value.name

    @TypeConverter
    fun toSportType(value: String): SportType =
        SportType.valueOf(value)

    // Date <-> Long
    @TypeConverter
    fun fromTimestamp(ts: Long?): Date? =
        ts?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? =
        date?.time
}
