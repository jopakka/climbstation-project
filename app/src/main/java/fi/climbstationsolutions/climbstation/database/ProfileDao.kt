package fi.climbstationsolutions.climbstation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProfileDao {
    @Insert
    suspend fun insertProfile(profile: ClimbProfile): Long

    @Query("DELETE FROM ClimbProfile WHERE id = :id AND isDefault = 0")
    suspend fun deleteProfile (id: Long)

    @Insert
    suspend fun insertStep(step: ClimbStep): Long

    @Query("DELETE FROM ClimbStep WHERE id = :id AND (SELECT isDefault FROM ClimbProfile WHERE id = ClimbStep.profileId) = 0")
    suspend fun deleteStep(id: Long)
}