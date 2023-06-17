package com.ankit.workdaytest.networking

import com.ankit.workdaytest.networking.models.ImageSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

const val BASE = "https://images-api.nasa.gov/"

interface NasaAPI {

    /**
     * get the first query
     * https://images-api.nasa.gov/search?q=IO&page=4&page_size=100&media_type=image
     */
    fun searchInitialUrl(
        searchQuery: String,
        pageNo: Int,
        pageSize: Int
    ) = "${BASE}?q=${searchQuery}&page=${pageNo}&page_size=${pageSize}&media_type=image"

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

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(NasaAPI::class.java)

}