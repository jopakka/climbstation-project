package fi.climbstationsolutions.climbstation

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.climbstationsolutions.climbstation.database.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var db: AppDatabase
    private lateinit var sessionDao: SessionWithDataDao
    private lateinit var settingsDao: SettingsDao
    private lateinit var profileDao: ProfileDao
    private lateinit var calendar: Calendar

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        sessionDao = db.sessionDao()
        settingsDao = db.settingsDao()
        profileDao = db.profileDao()
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

    @Test
    fun insertAndGetProfile(): Unit = runBlocking {
        val profile = ClimbProfile(0, "Test climb profile")
        val id = profileDao.insertProfile(profile)
        val profileWithSteps = profileDao.getProfileWithSteps(id)

        assertEquals(id, profileWithSteps.profile.id)
        assertEquals(profile.name, profileWithSteps.profile.name)
        assertEquals(10, profileWithSteps.profile.speed)
        assertEquals(false, profileWithSteps.profile.isDefault)
    }

    @Test
    fun insertSteps(): Unit = runBlocking {
        val profile = ClimbProfile(0, "Another test climb profile")
        val id = profileDao.insertProfile(profile)

        val steps = listOf(
            ClimbStep(0, id, 3, 10),
            ClimbStep(0, id, 1, -10),
            ClimbStep(0, id, 5, 34),
        )
        steps.forEach {
            profileDao.insertStep(it)
        }

        val profileWithSteps = profileDao.getProfileWithSteps(id)
        assertEquals(id, profileWithSteps.steps.first().profileId)
        assertEquals(-10, profileWithSteps.steps[1].angle)
        assertEquals(5, profileWithSteps.steps[2].distance)
    }

    @Test
    fun deleteStep(): Unit = runBlocking {
        val profile = ClimbProfile(0, "Another test climb profile 123")
        val id = profileDao.insertProfile(profile)

        val steps = listOf(
            ClimbStep(0, id, 5, 12),
            ClimbStep(0, id, 6, -31),
            ClimbStep(0, id, 1, 5),
        )
        steps.forEach {
            profileDao.insertStep(it)
        }

        var profileWithSteps = profileDao.getProfileWithSteps(id)
        assertEquals(3, profileWithSteps.steps.size)

        profileDao.deleteStep(1)

        profileWithSteps = profileDao.getProfileWithSteps(id)
        assertEquals(2, profileWithSteps.steps.size)
    }

    @Test
    fun deleteProfile(): Unit = runBlocking {
        val profile = ClimbProfile(0, "Another test climb profile 123")
        val id = profileDao.insertProfile(profile)

        profileDao.deleteProfile(id)

        val profileWithSteps = profileDao.getProfileWithSteps(id)
        assertEquals(null, profileWithSteps)
    }
}