package com.example.mvvmkotlin.newapp.repository

import com.example.mvvmkotlin.newapp.api.RetrofitInstance
import com.example.mvvmkotlin.newapp.db.ArticleDatabase
import com.example.mvvmkotlin.newapp.model.Article

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, page: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, page)

    suspend fun searchNews(countryCode: String, page: Int) =
        RetrofitInstance.api.searchNews(countryCode, page)

    suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    fun getSavingNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}