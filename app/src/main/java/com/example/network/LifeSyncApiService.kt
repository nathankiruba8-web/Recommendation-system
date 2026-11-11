package com.example.network

import com.example.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class AuthRequest(val username: String, val password: String, val email: String? = null)
data class AuthResponse(val token: String, val user: UserProfile)
data class HealthResponse(val status: String, val service: String)

interface LifeSyncApiService {
    @GET("health/")
    suspend fun checkHealth(): HealthResponse

    @POST("auth/login/")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("auth/register/")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @GET("profile/")
    suspend fun getProfile(): UserProfile

    @PUT("profile/")
    suspend fun updateProfile(@Body profile: UserProfile): UserProfile

    @GET("decisions/")
    suspend fun getDecisions(): List<Decision>

    @POST("decisions/")
    suspend fun createDecision(@Body decision: Decision): Decision

    @POST("decisions/{id}/evaluate/")
    suspend fun evaluateDecisionOnServer(@Path("id") decisionId: String): Decision

    @GET("goals/")
    suspend fun getGoals(): List<Goal>

    @POST("goals/")
    suspend fun createGoal(@Body goal: Goal): Goal

    @GET("planner/")
    suspend fun getPlannerTasks(): List<PlannerTask>

    @POST("planner/")
    suspend fun createPlannerTask(@Body task: PlannerTask): PlannerTask

    @PATCH("planner/{id}/toggle/")
    suspend fun togglePlannerTask(@Path("id") taskId: String): PlannerTask

    @GET("analytics/")
    suspend fun getAnalytics(): AnalyticsSummary
}

/**
 * Manages dynamic Base URL switching (e.g. Render live URL vs localhost/demo mode)
 * and holds authorization tokens in memory.
 */
object ApiConfigurationManager {
    // Default placeholder for Render deployment
    var currentBaseUrl: String = "https://lifesync-api.onrender.com/api/v1/"
        private set

    var authToken: String? = null
    var isDemoMode: Boolean = true // Starts in Demo Mode so app works out-of-the-box

    fun updateBaseUrl(newUrl: String) {
        var cleanUrl = newUrl.trim()
        if (!cleanUrl.endsWith("/")) {
            cleanUrl += "/"
        }
        currentBaseUrl = cleanUrl
        rebuildRetrofitClient()
    }

    private val dynamicInterceptor = Interceptor { chain ->
        var request = chain.request()
        val newHttpUrl = currentBaseUrl.toHttpUrlOrNull()
        if (newHttpUrl != null) {
            val updatedUrl = request.url.newBuilder()
                .scheme(newHttpUrl.scheme)
                .host(newHttpUrl.host)
                .port(newHttpUrl.port)
                .build()
            val requestBuilder = request.newBuilder().url(updatedUrl)
            authToken?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }
            request = requestBuilder.build()
        }
        chain.proceed(request)
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(dynamicInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(currentBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    var apiService: LifeSyncApiService = retrofit.create(LifeSyncApiService::class.java)
        private set

    private fun rebuildRetrofitClient() {
        retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        apiService = retrofit.create(LifeSyncApiService::class.java)
    }
}
