package com.example.sentimentanalysis

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sentimentanalysis.adapter.OnReviewClickListener
import com.example.sentimentanalysis.adapter.ReviewsAdapter
import com.example.sentimentanalysis.responses.ReviewResponse
import com.example.sentimentanalysis.responses.ReviewResultsItem
import com.example.sentimentanalysis.retrofit.TmdbClient
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewsActivity : AppCompatActivity(), OnReviewClickListener {

    val reviewsList = ArrayList<ReviewResultsItem>()
    val reviewsResults = ArrayList<Boolean>()
    val adapter by lazy {
        ReviewsAdapter(this, reviewsList, reviewsResults)
    }

    var reviewLoaded = false
    var vocabLoaded = false
    val classifier by lazy {
        Classifier(this, "word_dict.json")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        supportActionBar?.title = "Reviews"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rv.adapter = adapter

        if (!intent.hasExtra(KEY_MOVIE_ID)) {
            finish()
        }
        if (intent.hasExtra(KEY_MOVIE_TITLE)) {
            supportActionBar?.title = intent.getStringExtra(KEY_MOVIE_TITLE)
        }

        classifier.setCallback(object : Classifier.DataCallback {
            override fun onDataProcessed(result: HashMap<String, Int>?) {
                Log.e("TAG", "onDataProcessed: ")
                classifier.setVocab(result)
                vocabLoaded = true
                if (reviewLoaded) {
                    classifyReviews()
                }
            }
        })
        TmdbClient.api.getMovieReviews("${intent.getIntExtra(KEY_MOVIE_ID, -1)}").enqueue(object :
            Callback<ReviewResponse> {
            override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {
                Toast.makeText(this@ReviewsActivity, "Failed to load reviews", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<ReviewResponse>,
                response: Response<ReviewResponse>
            ) {
                if (response.isSuccessful) {
                    reviewsList.clear()
                    response.body()?.results?.forEach { reviewsList.add(it!!) }
                    if(reviewsList.isEmpty()){
                        switchToNoContentLayout()
                    }else {
                        switchToReviewsLayout()
                        adapter.notifyDataSetChanged()
                    }
                    reviewLoaded = true
                    if (vocabLoaded) {
                        classifyReviews()
                    }
                }
            }

        })

    }

    fun switchToNoContentLayout(){
        rv.visibility = View.GONE
        noContentLayout.visibility = View.VISIBLE
    }

    fun switchToReviewsLayout(){
        noContentLayout.visibility = View.GONE
        rv.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        classifier.loadData()
    }

    override fun onStop() {
        super.onStop()
        classifier.unload()
    }

    private fun classifyReviews() {
        reviewsResults.clear()
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 0 until reviewsList.size) {
                val review = reviewsList[i]
                if (review.content == null) continue
                val results = classifier.classify(review.content)
                Log.d("TAG", "classifyReviews: result: ${results[0]}")
                if (results[0].isNaN()) {
                    reviewsResults.add(true)
                } else {
                    reviewsResults.add(results[0] >= 0.5)
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyItemChanged(i)
                }
            }
        }
    }

    override fun onReviewClicked(position: Int, isPositive: Boolean?) {
        val review = reviewsList[position]
        val dialog = ReviewDetailsDialog(review.author, review.content, isPositive)
        dialog.show(supportFragmentManager, "dialog")
    }

}