package fi.climbstationsolutions.climbstation.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Custom interceptor for retrofit which checks missing '}'
 */
class LoggerInterceptor : Interceptor {
    /**
     * Checks if response body's last character is "}".
     * If not then creates new response which is same as old one,
     * except adds "}" to it
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)
        val body = response.peekBody(2048).string()

        val lastChar = body.lastOrNull()
        if(lastChar != '}') {
            val newBody = "$body}".toResponseBody()
            return response.newBuilder()
                .headers(response.headers)
                .body(newBody)
                .build()
        }

        return response
    }
}