package com.example.mvvmkotlin.newapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mvvmkotlin.newapp.model.Article

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long

    //сортировка по возрастанию
    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}