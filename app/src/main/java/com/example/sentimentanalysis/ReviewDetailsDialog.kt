package com.example.sentimentanalysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.review_details.view.*

class ReviewDetailsDialog(val author: String?, val content: String?, val isPositive: Boolean?): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.review_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view){
            tvAuthor.text = author
            tvContent.text = content

            btnDone.setOnClickListener {
                dismiss()
            }

            if(isPositive==null){
                ivIsPositive.visibility = View.GONE
            }else{
                if(isPositive){
                    ivIsPositive.setImageResource(R.drawable.thumbs_up)
                }else{
                    ivIsPositive.setImageResource(R.drawable.thumbs_down)
                }
                ivIsPositive.visibility = View.VISIBLE
            }

        }
    }
}