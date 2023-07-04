package com.ankit.nasaimages.networking

import com.ankit.nasaimages.networking.models.ImageAssetResponse
import com.ankit.nasaimages.networking.models.ImageSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

//TODO the base url can be provided as build variable for testing automation
const val BASE = "https://images-api.nasa.gov/"

interface NasaAPI {

    //https://images-api.nasa.gov/search?q=IO&page=4&page_size=100&media_type=image
    /**
     * First search query API
     */
    @GET("search?media_type=image")
    suspend fun search(
        @Query("q") searchQuery: String,
        @Query("page") pageNo: Int,
        @Query("page_size") pageSize: Int
    ): ImageSearchResponse


    /**
     * Provides subsequent pagination through REST API response
     */
    @GET()
    suspend fun search(@Url url: String): ImageSearchResponse


    //https://images-api.nasa.gov/asset/PIA22486
    /**
     * get asset detail by nasa ID. provides list of image resolution.
     */
    @GET("asset/{id}")
    suspend fun getAssetById(@Path("id") id: String): ImageAssetResponse
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