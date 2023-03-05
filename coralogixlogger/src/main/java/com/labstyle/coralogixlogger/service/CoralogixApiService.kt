package com.labstyle.coralogixlogger.service

import com.labstyle.coralogixlogger.models.LogApiRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val baseUrl = "https://api.coralogix.us/api/v1/"
private const val TIMEOUT_SECONDS = 120L

interface CoralogixApiService {
    @POST("logs")
    suspend fun log(@Body request: LogApiRequest)

    companion object {
        fun buildService(debug: Boolean = false): CoralogixApiService {
            val interceptor = HttpLoggingInterceptor().apply {
                this.level =
                    if (debug) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
            val okClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build()
                .create(CoralogixApiService::class.java)
        }
    }
}