package com.johan.racketmatchapp.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity (
    @PrimaryKey(autoGenerate = true)

    val id: Long = 0L,

    @ColumnInfo(name = "username")

    val username: String
)