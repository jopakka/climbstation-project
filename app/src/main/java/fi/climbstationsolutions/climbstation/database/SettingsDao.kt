package fi.climbstationsolutions.climbstation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Queries for users body weight.
 *
 * @author Oskar Wiiala
 */
@Dao
interface SettingsDao {
    //BodyWeight
    @Query("SELECT * FROM BodyWeight WHERE id =:id")
    suspend fun getBodyWeightById(id: Long): BodyWeight?

    @Query("UPDATE BodyWeight SET weight =:weight WHERE id = 1")
    suspend fun updateUserBodyWeight(weight: Float)

    @Insert(onConflict = 1)
    suspend fun insertUserBodyWeight(data: BodyWeight): Long
}