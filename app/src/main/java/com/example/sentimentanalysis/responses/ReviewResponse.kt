package com.example.sentimentanalysis.responses

data class ReviewResponse(
    val id: Int? = null,
    val page: Int? = null,
    val totalPages: Int? = null,
    val results: List<ReviewResultsItem?>? = null,
    val totalResults: Int? = null
)

data class ReviewResultsItem(
    val author: String? = null,
    val id: String? = null,
    val content: String? = null,
    val url: String? = null
)

