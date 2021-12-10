package fi.climbstationsolutions.climbstation

import com.google.gson.Gson
import fi.climbstationsolutions.climbstation.network.ClimbStationAPI
import fi.climbstationsolutions.climbstation.network.LoginRequest
import fi.climbstationsolutions.climbstation.network.LogoutRequest
import fi.climbstationsolutions.climbstation.network.ClimbStationHttpService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class APITests {
    private var server = MockWebServer()
    private lateinit var api: ClimbStationHttpService

    @Before
    fun setup() {
        server.start(8800)
        val baseUrl = server.url("/")
        api = ClimbStationAPI.get(baseUrl.toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun login() = runBlocking {
        val dispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val obj = Gson().fromJson(request.body.readUtf8(), LoginRequest::class.java)
                if (request.path == "/login" && obj.packetID == "2a") {
                    return MockResponse().setResponseCode(200).setBody(
                        "{\n" +
                                "\"PacketID\" : \"2aR1\" ,\n" +
                                "\"PacketNumber\": \"2\",\n" +
                                "\"clientKey\": \"2021110318543138\",\n" +
                                "\"Response\" : \"OK\" \n" +
                                "}"
                    )
                }
                return MockResponse().setResponseCode(200).setBody(
                    "{\n" +
                            "\"PacketID\" : \"$2aR2\" ,\n" +
                            "\"PacketNumber\": \"3\",\n" +
                            "\"Response\" : \"NOTOK\" \n" +
                            "}"
                )
            }
        }
        server.dispatcher = dispatcher

        val call = api.login(LoginRequest("", "", ""))
        assertEquals("OK", call.response)
    }

    @Test
    fun logout() = runBlocking {
        val dispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val obj = Gson().fromJson(request.body.readUtf8(), LogoutRequest::class.java)
                if (request.path == "/logout" && obj.logout == "request" && obj.packetID == "2g") {
                    return MockResponse().setResponseCode(200).setBody(
                        "{\n" +
                                "\"PacketID\" : \"${obj.packetID}R1\" ,\n" +
                                "\"PacketNumber\": \"2\",\n" +
                                "\"Response\" : \"OK\" \n" +
                                "}"
                    )
                }
                return MockResponse().setResponseCode(200).setBody(
                    "{\n" +
                            "\"PacketID\" : \"${obj.packetID}R2\" ,\n" +
                            "\"PacketNumber\": \"3\",\n" +
                            "\"Response\" : \"NOTOK\" \n" +
                            "}"
                )
            }
        }
        server.dispatcher = dispatcher

        val call = api.logout(LogoutRequest("", ""))
        assertEquals("OK", call.response)
    }
}