package com.ankit.workdaytest.networking

import com.ankit.workdaytest.networking.models.ImageSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

const val BASE = "https://images-api.nasa.gov/"

interface NasaAPI {

    /**
     * subsequent pagination is through REST API response
     */
    @GET()
    suspend fun search(@Url url: String): ImageSearchResponse
}


/**
 * Main entry point for network access.
 */
object NasaAPINetwork {



    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    }.build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: NasaAPI = retrofit.create(NasaAPI::class.java)

}