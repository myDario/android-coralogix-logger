package com.labstyle.coralogixlogger.service

import com.labstyle.coralogixlogger.models.CoralogixRegion
import com.labstyle.coralogixlogger.models.LogApiRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 120L

interface CoralogixApiService {
    @POST("api/v1/logs")
    suspend fun log(@Body request: LogApiRequest)

    companion object {
        fun buildService(region: CoralogixRegion, privateKey: String, debug: Boolean = false): CoralogixApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                this.level =
                    if (debug) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
            
            // Add Authorization header interceptor (new Coralogix requirement)
            val authInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $privateKey")
                    .build()
                chain.proceed(request)
            }
            
            val okClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(region.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build()
                .create(CoralogixApiService::class.java)
        }
    }
}