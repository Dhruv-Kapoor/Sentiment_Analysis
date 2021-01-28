package com.example.sentimentanalysis

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_custom_reviews.*

class CustomReviewsActivity : AppCompatActivity() {

    val classifier by lazy {
        Classifier(this, "word_dict.json")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_reviews)

        supportActionBar?.title = "My Review"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        classifier.setCallback(object : Classifier.DataCallback {
            override fun onDataProcessed(result: HashMap<String, Int>?) {
                classifier.setVocab(result)
                btnAnalyze.isEnabled = true
            }
        })
        btnAnalyze.setOnClickListener {
            classifyText(etText.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        classifier.loadData()
    }

    override fun onStop() {
        super.onStop()
        classifier.unload()
    }

    private fun classifyText(message: String) {
        val results = classifier.classify(message)
        if (results[0].isNaN()) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
        } else {
            if (results[0] >= 0.5) {
                tvResult.text = "Positive"
                tvResult.setBackgroundColor(getColor(R.color.brightGreen))
                tvResult.visibility = View.VISIBLE
            } else {
                tvResult.text = "Negative"
                tvResult.setBackgroundColor(getColor(R.color.red))
                tvResult.visibility = View.VISIBLE
            }
        }
    }
}
