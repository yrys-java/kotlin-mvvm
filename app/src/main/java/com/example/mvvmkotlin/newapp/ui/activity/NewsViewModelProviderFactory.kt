package com.example.mvvmkotlin.newapp.ui.activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmkotlin.newapp.repository.NewsRepository

//в этом классе создается экзепляр ViewModel
class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }
}