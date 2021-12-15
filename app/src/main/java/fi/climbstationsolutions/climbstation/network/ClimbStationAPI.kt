package fi.climbstationsolutions.climbstation.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Handles HTTP requests to ClimbStation machine
 *
 * @author Joonas Niemi
 */
abstract class ClimbStationAPI {
    companion object {
        @Volatile
        private var mInstance: ClimbStationHttpService? = null

        /**
         * Gets instance of [ClimbStationHttpService].
         */
        fun get(baseUrl: String): ClimbStationHttpService {
            return mInstance ?: synchronized(this) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(LoggerInterceptor())
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                retrofit.create(ClimbStationHttpService::class.java)
            }
        }
    }
}