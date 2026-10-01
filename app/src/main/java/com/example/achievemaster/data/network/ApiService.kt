package com.example.achievemaster.data.network

import com.example.achievemaster.data.model.Exercise
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("exercises")
    suspend fun getExercises(
        @Query("muscle") muscle: String? = null,
        @Query("type") type: String? = null,
        @Query("difficulty") difficulty: String? = null
    ): List<Exercise>

    @GET("exercises")
    suspend fun searchExercises(@Query("name") name: String): List<Exercise>
}