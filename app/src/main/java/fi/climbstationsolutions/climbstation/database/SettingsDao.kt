package fi.climbstationsolutions.climbstation.database

import androidx.room.*

@Dao
interface SettingsDao {
    //BodyWeight
    @Query("SELECT * FROM BodyWeight WHERE id =:id")
    suspend fun getBodyWeightById(id: Long): BodyWeight

    @Query("UPDATE BodyWeight SET weight =:weight WHERE id = 1")
    suspend fun updateUserBodyWeight(weight: Float)

    @Insert
    suspend fun insertUserBodyWeight(data: BodyWeight): Long
}