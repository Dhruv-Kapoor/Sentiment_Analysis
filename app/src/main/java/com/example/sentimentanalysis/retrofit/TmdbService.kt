package com.example.sentimentanalysis.retrofit

import com.example.sentimentanalysis.Credentials.TMDB_API_KEY
import com.example.sentimentanalysis.responses.MovieResponse
import com.example.sentimentanalysis.responses.ReviewResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w200"

interface TmdbService {

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = TMDB_API_KEY,
        @Query("region") region: String = "IN"
    ): Call<MovieResponse>

    @GET("movie/{id}/{reviews}")
    fun getMovieReviews(
        @Path("id") id: String,
        @Path("reviews") reviews: String = "reviews",
        @Query("api_key") apiKey: String = TMDB_API_KEY
    ): Call<ReviewResponse>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = TMDB_API_KEY
    ): Call<MovieResponse>
}