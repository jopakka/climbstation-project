package fi.climbstationsolutions.climbstation

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.climbstationsolutions.climbstation.database.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var db: AppDatabase
    private lateinit var sessionDao: SessionWithDataDao
    private lateinit var settingsDao: SettingsDao
    private lateinit var calendar: Calendar

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        sessionDao = db.sessionDao()
        settingsDao = db.settingsDao()
        calendar = Calendar.getInstance()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // SessionDao tests
    @Test
    fun insertSessionAndGet(): Unit = runBlocking {
        val session = Session(0, "testing db", calendar.time)
        val id = sessionDao.insertSession(session)

        assertEquals(id, sessionDao.getSessionById(id).id)
        assertEquals(session.name, sessionDao.getSessionById(id).name)
        assertEquals(session.createdAt, sessionDao.getSessionById(id).createdAt)
    }

    @Test
    fun insertSessionWithData(): Unit = runBlocking {
        val session = Session(0, "Another db test", calendar.time)
        val sId = sessionDao.insertSession(session)
        val dataList = listOf(
            Data(0, sId, 100, -21, 340),
            Data(0, sId, 80, 0, 430),
            Data(0, sId, 200, 10, 560)
        )
        dataList.forEach {
            sessionDao.insertData(it)
        }

        val getSession = sessionDao.getSessionWithData(sId)
        getSession.observeForever { s ->
            assertEquals(session.name, s.session.name)
            assertEquals(session.createdAt, s.session.createdAt)
            assertEquals("Amount of data", dataList.size, s.data.size)
            assertEquals(dataList[0].angle, s.data[0].angle)
            assertEquals(dataList[1].speed, s.data[1].speed)
            assertEquals(dataList[2].totalDistance, s.data[2].totalDistance)
        }
    }

    //SettingsDao tests
    @Test
    fun userWeightTests(): Unit = runBlocking {
        val inputWeight = BodyWeight(1, 90.0f)

        // insert user weight
        var userId = settingsDao.insertUserBodyWeight(inputWeight)

        Log.d("DatabaseTests.kt", "insertWeightAndGet id: $userId")

        // get user weight
        var outputWeight = settingsDao.getBodyWeightById(userId)
        assertEquals(BodyWeight(1, 90.0f), outputWeight)

        // update user weight
        val newWeight = 62.4f
        settingsDao.updateUserBodyWeight(newWeight)
        outputWeight = settingsDao.getBodyWeightById(userId)
        assertEquals(BodyWeight(1, 62.4f), outputWeight)

        // try to insert after already having a weight set
        userId = settingsDao.insertUserBodyWeight(inputWeight)
        assertEquals(1, userId)
    }
}