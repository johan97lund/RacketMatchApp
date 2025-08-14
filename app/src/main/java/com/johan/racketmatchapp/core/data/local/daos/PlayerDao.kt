package com.johan.racketmatchapp.core.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.johan.racketmatchapp.core.data.local.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    /**
     * Observe all players in the database, ordered by username.
     * Emits updates whenever the table changes.
     */
    @Query("SELECT * FROM players ORDER BY username")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    /**
     * Look up a single player by its unique ID.
     * Returns null if no such player exists.
     */
    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerEntity?

    /**
     * Insert a new player. If a player with the same primary key
     * already exists, it will be replaced.
     * Returns the newly assigned row ID.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(player: PlayerEntity): Long

    /**
     * Update an existing player's fields.
     * Returns the number of rows updated (should be 1).
     */
    @Update
    suspend fun update(player: PlayerEntity): Int

    /**
     * Remove a player from the database.
     */
    @Delete
    suspend fun delete(player: PlayerEntity)
}