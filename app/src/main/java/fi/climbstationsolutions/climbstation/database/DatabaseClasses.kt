package fi.climbstationsolutions.climbstation.database

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

/**
 * Data class for each session.
 */
@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Date,
    val profileId: Long,
    val endedAt: Date? = null
)

/**
 * Data class for each data item in session.
 */
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

/**
 * Data class which gets [Session] and all of its [Data] items.
 */
data class SessionWithData(
    @Embedded val session: Session,
    @Relation(parentColumn = "id", entityColumn = "sessionId") val data: List<Data>
)

/**
 * Data class for user body weight.
 */
@Entity
data class BodyWeight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Float = 70.00f
)

/**
 * Data class for climbing profile.
 */
@Entity
@Parcelize
data class ClimbProfile(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    var speed: Int = 10,
    val createdAt: Date? = null,
    val isDefault: Boolean = false,
    val manual: Boolean = false
) : Parcelable

/**
 * Data class for each climbing profile step
 */
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

/**
 * Data class which gets [ClimbProfile] and all of its [ClimbStep] items.
 */
@Parcelize
data class ClimbProfileWithSteps(
    @Embedded val profile: @RawValue ClimbProfile,
    @Relation(parentColumn = "id", entityColumn = "profileId") val steps: @RawValue List<ClimbStep>
) : Parcelable
