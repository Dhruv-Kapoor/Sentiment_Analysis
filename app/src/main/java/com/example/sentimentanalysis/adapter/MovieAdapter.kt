package com.example.sentimentanalysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sentimentanalysis.R
import com.example.sentimentanalysis.responses.ResultsItem
import com.example.sentimentanalysis.retrofit.IMAGES_BASE_URL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item_view.view.*

class MovieAdapter(
    val onMovieClickListener: OnMovieClickListener,
    val list: ArrayList<ResultsItem>
) : RecyclerView.Adapter<MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        MovieViewHolder(
            onMovieClickListener,
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item_view, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

class MovieViewHolder(val onMovieClickListener: OnMovieClickListener, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    fun bind(resultsItem: ResultsItem) {
        with(itemView) {
            tvTitle.text = resultsItem.originalTitle
            tvOverview.text = resultsItem.overview
            tvDate.text = "Release Date: ${resultsItem.releaseDate}"
            Picasso.get().load(IMAGES_BASE_URL + resultsItem.posterPath).into(ivPoster)
            setOnClickListener {
                onMovieClickListener.onMovieClick(adapterPosition)
            }
        }
    }

}

interface OnMovieClickListener {
    fun onMovieClick(position: Int)
}