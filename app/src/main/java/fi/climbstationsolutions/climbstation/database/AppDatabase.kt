package fi.climbstationsolutions.climbstation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fi.climbstationsolutions.climbstation.utils.Converters

@Database(entities = [Session::class, Data::class, BodyWeight::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionWithDataDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var mInstance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return mInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                ).build()
                mInstance = instance
                instance
            }
        }
    }
}