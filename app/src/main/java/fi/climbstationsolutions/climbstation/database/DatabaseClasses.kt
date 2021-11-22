package fi.climbstationsolutions.climbstation.database

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Date,
    val profileId: Long,
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

@Entity
@Parcelize
data class ClimbProfile(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val speed: Int = 10,
    val isDefault: Boolean = false
) : Parcelable

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ClimbProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = CASCADE
        )
    ]
)
@Parcelize
data class ClimbStep(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val profileId: Long,
    val distance: Int,
    val angle: Int
) : Parcelable

@Parcelize
data class ClimbProfileWithSteps(
    @Embedded val profile: @RawValue ClimbProfile,
    @Relation(parentColumn = "id", entityColumn = "profileId") val steps: @RawValue List<ClimbStep>
) : Parcelable
