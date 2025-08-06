package com.johan.racketmatchapp.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.johan.racketmatchapp.core.data.model.SportType
import java.util.Date

@TypeConverters
@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val matchId: Long = 0,

    val sportType: SportType,

    val startTime: Date,
    val endTime: Date? = null,

    val playerAId: Long,
    val playerBId: Long,

    val playerAScore: Int = 0,
    val playerBScore: Int = 0,

    val setAScore: Int = 0,
    val setBScore: Int = 0,

    val gameAScore: Int = 0,
    val gameBScore: Int = 0,

    val winnerPlayerId: Long? = null
)