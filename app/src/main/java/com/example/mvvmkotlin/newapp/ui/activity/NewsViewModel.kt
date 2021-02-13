package com.example.mvvmkotlin.newapp.ui.activity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmkotlin.newapp.model.Article
import com.example.mvvmkotlin.newapp.model.NewsResponse
import com.example.mvvmkotlin.newapp.newsContext.NewsApplication
import com.example.mvvmkotlin.newapp.repository.NewsRepository
import com.example.mvvmkotlin.newapp.util.ApiResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    //для домашней страницы
    val breakingNews: MutableLiveData<ApiResult<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1


    //для страницы поиска
    val searchNews: MutableLiveData<ApiResult<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchNewsQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchNewsQuery)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        try {
            if (hasInternetConnections()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleResponses(response))
            } else {
                breakingNews.postValue(ApiResult.Error("Not internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(ApiResult.Error("Network Failure"))
                else -> breakingNews.postValue(ApiResult.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        try {
            if (hasInternetConnections()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleResponses(response))
            } else {
                searchNews.postValue(ApiResult.Error("Not internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(ApiResult.Error("Network Failure"))
                else -> searchNews.postValue(ApiResult.Error("Conversion Error"))
            }
        }
    }

    //обработчик запросов: возращает результат загрузки Api
    private fun handleResponses(response: Response<NewsResponse>): ApiResult<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                return ApiResult.Success(result)
            }
        }
        return ApiResult.Error(response.message())
    }

    // дальше три метода для базы данных
    fun getSavingNews() = newsRepository.getSavingNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insert(article)
    }

    //проверка интернет подключения: использую application context
    fun hasInternetConnections(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilites =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}