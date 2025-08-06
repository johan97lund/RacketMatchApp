package com.johan.racketmatchapp.core.data.local.daos

import androidx.room.*
import com.johan.racketmatchapp.core.data.local.entities.MatchEntity

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY startTime DESC")
    suspend fun getAllMatches(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE matchId = :id")
    suspend fun getMatchById(id: Long): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)
}