package fi.climbstationsolutions.climbstation.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import java.util.*

/**
 * Queries for climbing sessions.
 *
 * @author Joonas Niemi, Patrik Pölkki
 */
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
    @Query("SELECT * FROM Session ORDER BY CASE WHEN :desc = 1 THEN createdAt END DESC, CASE WHEN :desc = 0 THEN createdAt END ASC")
    suspend fun getAllSessionsWithData(desc: Boolean = true): List<SessionWithData>

    @Query("SELECT SUM(max) FROM (SELECT MAX(totalDistance) AS max FROM Data GROUP BY sessionId)")
    fun getAllTimeDistance(): LiveData<Int>

    @Query("SELECT SUM(dates) FROM (SELECT endedAt - createdAt AS dates FROM Session)")
    fun getAllTimeDuration(): LiveData<Long>

    @Query("SELECT SUM(max) FROM (SELECT MAX(totalDistance) as max from Data INNER JOIN Session on Session.id = Data.sessionId WHERE DATETIME(Session.createdAt / 1000, 'unixepoch') >= DATETIME('now', '-7 days') GROUP by Data.sessionId)")
    fun getSevenDayDistance(): LiveData<Int>

    @Query("SELECT SUM(total) FROM (SELECT (endedAt - createdAt) AS total FROM Session WHERE DATETIME(session.createdAt / 1000, 'unixepoch') >= DATETIME('now', '-7 days'))")
    fun getSevenDayDuration(): LiveData<Long>

    @Query("SELECT * FROM Session WHERE createdAt >= :start AND createdAt < :end ORDER BY CASE WHEN :desc = 1 THEN createdAt END DESC, CASE WHEN :desc = 0 THEN createdAt END ASC")
    suspend fun getSessionWithDataBetween(
        start: Date,
        end: Date,
        desc: Boolean = true
    ): List<SessionWithData>
}