package fi.climbstationsolutions.climbstation.utils

import androidx.room.TypeConverter
import java.util.*

/**
 * [TypeConverter]s for Room database
 */
object Converters {
    /**
     * Converts [value] to Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts [date] to Long
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}