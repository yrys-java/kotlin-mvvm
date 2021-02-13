package com.example.mvvmkotlin.newapp.model

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)