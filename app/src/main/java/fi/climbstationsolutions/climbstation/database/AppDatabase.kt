package fi.climbstationsolutions.climbstation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Session::class, Data::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionWithDataDao

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