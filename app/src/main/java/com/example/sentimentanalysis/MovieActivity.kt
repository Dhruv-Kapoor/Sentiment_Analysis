package com.example.sentimentanalysis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.sentimentanalysis.adapter.MovieAdapter
import com.example.sentimentanalysis.adapter.OnMovieClickListener
import com.example.sentimentanalysis.responses.MovieResponse
import com.example.sentimentanalysis.responses.ResultsItem
import com.example.sentimentanalysis.retrofit.TmdbClient
import kotlinx.android.synthetic.main.activity_movie.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val KEY_MOVIE_ID = "MovieId"
const val KEY_MOVIE_TITLE = "MovieTitle"

class MovieActivity : AppCompatActivity(), OnMovieClickListener {

    val moviesList = ArrayList<ResultsItem>()
    val adapter = MovieAdapter(this, moviesList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        supportActionBar?.title = "Movies"
        rv.adapter = adapter

        TmdbClient.api.getPopularMovies().enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@MovieActivity, "Failed to load movies", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    moviesList.clear()
                    response.body()?.results?.forEach { moviesList.add(it!!) }
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemCustomReview -> {
                startActivity(Intent(this, CustomReviewsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMovieClick(position: Int) {
        val intent = Intent(this, ReviewsActivity::class.java)
        intent.putExtra(KEY_MOVIE_ID, moviesList[position].id)
        intent.putExtra(KEY_MOVIE_TITLE, moviesList[position].originalTitle)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movies_activity_menu, menu)
        val searchView = menu?.findItem(R.id.itemSearch)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) return false
                if (newText.length > 3 && newText.length % 2 == 0) {
                    searchMovies(newText)
                    return true
                }
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchMovies(query: String) {
        TmdbClient.api.searchMovies(query).enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    moviesList.clear()
                    response.body()?.results?.forEach { moviesList.add(it!!) }
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }
}