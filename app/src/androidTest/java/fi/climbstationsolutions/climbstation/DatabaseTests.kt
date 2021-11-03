package fi.climbstationsolutions.climbstation

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.Data
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
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
    private lateinit var calendar: Calendar

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        sessionDao = db.sessionDao()
        calendar = Calendar.getInstance()
    }

    @After
    fun tearDown() {
        db.close()
    }

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
        val dataList = listOf(Data(0, sId, 100, -21, 340),
            Data(0, sId, 80, 0, 430),
            Data(0, sId, 200, 10, 560)
        )
        dataList.forEach {
            sessionDao.insertData(it)
        }

        val getSession = sessionDao.getSessionWithData(sId)
        assertEquals(session.name, getSession.session.name)
        assertEquals(session.createdAt, getSession.session.createdAt)
        assertEquals("Amount of data", dataList.size, getSession.data.size)
        assertEquals(dataList[0].angle, getSession.data[0].angle)
        assertEquals(dataList[1].speed, getSession.data[1].speed)
        assertEquals(dataList[2].totalDistance, getSession.data[2].totalDistance)
    }
}