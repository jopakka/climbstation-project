package fi.climbstationsolutions.climbstation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.utils.Converters
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.Executors

@Database(
    entities = [
        Session::class,
        Data::class,
        BodyWeight::class,
        ClimbProfile::class,
        ClimbStep::class
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionWithDataDao
    abstract fun settingsDao(): SettingsDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var mInstance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return mInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        val job = Job()
                        val scope = CoroutineScope(Dispatchers.IO + job)

                        scope.launch {
                            val dao = get(context).profileDao()
                            val profiles = ProfileHandler.readProfiles(context, R.raw.profiles)
                            profiles.forEach {
                                val id = dao.insertProfile(ClimbProfile(0, it.name, isDefault = true))
                                it.steps.forEach { s ->
                                    dao.insertStep(ClimbStep(0, id, s.distance, s.angle))
                                }
                            }
                        }
                    }
                }).build()
                mInstance = instance
                instance
            }
        }
    }
}