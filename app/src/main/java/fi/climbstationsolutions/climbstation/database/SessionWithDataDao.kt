package fi.climbstationsolutions.climbstation.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface SessionWithDataDao {
    // Session
    @Query("SELECT * FROM Session WHERE id =:id")
    suspend fun getSessionById(id: Long): Session

    @Insert
    suspend fun insertSession(session: Session): Long

    @Query("UPDATE session SET endedAt = :date WHERE id = :id")
    suspend fun setEndedAtToSession(id: Long, date: Date)

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
    fun getSessionWithData(id: Long): LiveData<SessionWithData>

    // Latest SessionWithData
    @Transaction
    @Query("SELECT * FROM Session WHERE id = (SELECT MAX(id) FROM Session) LIMIT 1")
    fun getLastSessionWithData(): LiveData<SessionWithData>

    // SessionWithData
    @Transaction
    @Query("SELECT * FROM Session")
    fun getAllSessionsWithData(): LiveData<List<SessionWithData>>
}