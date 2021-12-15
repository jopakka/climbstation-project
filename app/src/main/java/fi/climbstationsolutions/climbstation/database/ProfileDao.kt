package fi.climbstationsolutions.climbstation.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Queries for climbing profiles
 *
 * @author Joonas Niemi
 */
@Dao
interface ProfileDao {
    @Insert
    suspend fun insertProfile(profile: ClimbProfile): Long

    @Query("DELETE FROM ClimbProfile WHERE id = :id AND isDefault = 0")
    suspend fun deleteProfile(id: Long)

    @Insert
    suspend fun insertStep(step: ClimbStep): Long

    @Query("DELETE FROM ClimbStep WHERE id = :id AND (SELECT isDefault FROM ClimbProfile WHERE id = ClimbStep.profileId) = 0")
    suspend fun deleteStep(id: Long)

    @Query("SELECT * FROM ClimbProfile WHERE manual = 0")
    fun getAllProfiles(): LiveData<List<ClimbProfileWithSteps>>

    @Query("SELECT * FROM ClimbProfile WHERE id = :id")
    suspend fun getProfileWithSteps(id: Long): ClimbProfileWithSteps

    @Query("SELECT * FROM ClimbProfile WHERE id = :id")
    fun getCustomProfileWithSteps(id: Long): LiveData<ClimbProfileWithSteps>

    @Query("SELECT * FROM ClimbProfile WHERE isDefault = 0 AND manual = 0")
    fun getAllCustomProfiles(): LiveData<List<ClimbProfileWithSteps>>

    @Query("UPDATE ClimbStep SET distance = :distance WHERE id = :id")
    suspend fun updateCustomStepDistance(distance: Int, id: Long)

    @Query("UPDATE ClimbStep SET angle = :angle WHERE id = :id")
    suspend fun updateCustomStepAngle(angle: Int, id: Long)
}