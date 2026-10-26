package com.example.achievemaster.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val API_KEY = "yyPGb746h01jOcNsNiYTeTS6H8Vnib4e2Jg7tVeZ"

    private var appContext: Context? = null

    private var _apiService: ApiService? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (_apiService == null) {
            _apiService = buildApiService(context.applicationContext)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val ctx = appContext ?: return true
        return try {
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    private fun buildApiService(context: Context): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 10L * 1024 * 1024)

        val forceCacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!isNetworkAvailable()) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .build()
            }
            chain.proceed(request)
        }

        val networkCacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = response.header("Cache-Control")
            if (cacheControl == null) {
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=3600")
                    .removeHeader("Pragma")
                    .build()
            } else {
                response
            }
        }

        val client = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", API_KEY)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(forceCacheInterceptor)
            .addNetworkInterceptor(networkCacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    val apiService: ApiService
        get() {
            val existing = _apiService
            if (existing != null) return existing

            val ctx = appContext
                ?: throw IllegalStateException(
                    "RetrofitClient.initialize(context) must be called before using apiService"
                )
            val created = buildApiService(ctx)
            _apiService = created
            return created
        }
}