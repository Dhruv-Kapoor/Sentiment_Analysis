package com.example.sentimentanalysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sentimentanalysis.R
import com.example.sentimentanalysis.responses.ReviewResultsItem
import kotlinx.android.synthetic.main.review_item_view.view.*

class ReviewsAdapter(val onReviewClickListener: OnReviewClickListener, val list: ArrayList<ReviewResultsItem>, val results: ArrayList<Boolean>) :
    RecyclerView.Adapter<ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder =
        ReviewViewHolder(
            onReviewClickListener,
            LayoutInflater.from(parent.context).inflate(R.layout.review_item_view, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        if (results.size >= position + 1) {
            holder.bind(list[position], results[position])
        } else {
            holder.bind(list[position])
        }
    }
}

class ReviewViewHolder(val onReviewClickListener: OnReviewClickListener, val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(resultsItem: ReviewResultsItem, result: Boolean? = null) {
        with(view) {
            tvAuthor.text = resultsItem.author
            tvContent.text = resultsItem.content
            if (result == null) {
                tvResult.visibility = View.GONE
            } else {
                if (result) {
                    tvResult.text = "Positive"
                    tvResult.setBackgroundColor(context.getColor(R.color.brightGreen))
                } else {
                    tvResult.text = "Negative"
                    tvResult.setBackgroundColor(context.getColor(R.color.red))
                }
                tvResult.visibility = View.VISIBLE
            }

            setOnClickListener {
                onReviewClickListener.onReviewClicked(adapterPosition, result)
            }
        }

    }

}

interface OnReviewClickListener{
    fun onReviewClicked(position: Int, isPositive: Boolean?)
}