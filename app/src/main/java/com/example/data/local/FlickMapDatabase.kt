package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_places")
data class FavoritePlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val categoryName: String,
    val rating: Float,
    val userRatingsTotal: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String,
    val phone: String,
    val website: String,
    val customTag: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val id: String,
    val query: String,
    val placeName: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface FlickMapDao {
    @Query("SELECT * FROM favorite_places ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoritePlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(place: FavoritePlaceEntity)

    @Query("DELETE FROM favorite_places WHERE id = :placeId")
    suspend fun deleteFavorite(placeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_places WHERE id = :placeId)")
    suspend fun isFavorite(placeId: String): Boolean

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :searchId")
    suspend fun deleteSearch(searchId: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearches()
}

@Database(
    entities = [FavoritePlaceEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlickMapDatabase : RoomDatabase() {
    abstract fun dao(): FlickMapDao

    companion object {
        @Volatile
        private var INSTANCE: FlickMapDatabase? = null

        fun getInstance(context: Context): FlickMapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlickMapDatabase::class.java,
                    "flick_map_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
