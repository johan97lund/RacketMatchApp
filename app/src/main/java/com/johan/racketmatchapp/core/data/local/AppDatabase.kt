package com.johan.racketmatchapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.johan.racketmatchapp.core.data.local.daos.MatchDao
import com.johan.racketmatchapp.core.data.local.daos.PlayerDao
import com.johan.racketmatchapp.core.data.local.entities.PlayerEntity
import com.johan.racketmatchapp.core.data.local.entities.MatchEntity

@Database(
    entities = [PlayerEntity::class, MatchEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
}