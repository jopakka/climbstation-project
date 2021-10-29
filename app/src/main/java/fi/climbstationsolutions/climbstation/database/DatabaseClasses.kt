package fi.climbstationsolutions.climbstation.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.sql.Date

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long
)

@Entity
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val speed: Int,
    val angle: Int,
    val totalDistance: Int
)

data class SessionWithData(
    @Embedded val session: Session,
    @Relation(parentColumn = "id", entityColumn = "sessionId") val data: List<Data>
)