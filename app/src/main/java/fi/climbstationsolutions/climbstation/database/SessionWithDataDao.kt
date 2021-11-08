package fi.climbstationsolutions.climbstation.database

import androidx.room.*

@Dao
interface SessionWithDataDao {
    // Session
    @Query("SELECT * FROM Session WHERE id =:id")
    suspend fun getSessionById(id: Long): Session

    @Insert
    suspend fun insertSession(session: Session): Long

    @Query("DELETE FROM Session WHERE id =:id")
    suspend fun deleteSession(id: Long)

    // Data
    @Query("SELECT * FROM Data WHERE id =:id")
    suspend fun getDataById(id: Long): Data

    @Insert
    suspend fun insertData(data: Data): Long

    @Query("DELETE FROM Data WHERE id =:id")
    suspend fun deleteData(id: Long)

    // SessionWithData
    @Transaction
    @Query("SELECT * FROM Session WHERE id =:id")
    suspend fun getSessionWithData(id: Long): SessionWithData

    // SessionWithData
    @Transaction
    @Query("SELECT * FROM Session")
    suspend fun getAllSessionsWithData(): List<SessionWithData>
}