package fi.climbstationsolutions.climbstation.database

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Date,
    val endedAt: Date? = null
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = CASCADE
        )
    ]
)
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

@Entity
data class BodyWeight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Float = 70.00f
)