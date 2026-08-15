package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ModCategory
import com.example.data.model.ModPackEntity
import com.example.data.model.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY isFavorite DESC, isFeatured DESC, id ASC")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Long): ServerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(servers: List<ServerEntity>)

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Delete
    suspend fun deleteServer(server: ServerEntity)

    @Query("UPDATE servers SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("UPDATE servers SET pingMs = :pingMs, onlinePlayers = :players, lastPingTimestamp = :timestamp WHERE id = :id")
    suspend fun updatePing(id: Long, pingMs: Int, players: Int, timestamp: Long)
}

@Dao
interface ModPackDao {
    @Query("SELECT * FROM mod_packs ORDER BY isEnabled DESC, id DESC")
    fun getAllModPacks(): Flow<List<ModPackEntity>>

    @Query("SELECT * FROM mod_packs WHERE category = :category ORDER BY id DESC")
    fun getModPacksByCategory(category: ModCategory): Flow<List<ModPackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModPack(modPack: ModPackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modPacks: List<ModPackEntity>)

    @Update
    suspend fun updateModPack(modPack: ModPackEntity)

    @Delete
    suspend fun deleteModPack(modPack: ModPackEntity)

    @Query("UPDATE mod_packs SET isEnabled = NOT isEnabled WHERE id = :id")
    suspend fun toggleModPack(id: Long)
}
