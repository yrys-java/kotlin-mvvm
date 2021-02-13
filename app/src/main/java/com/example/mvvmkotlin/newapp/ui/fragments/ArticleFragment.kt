package com.example.mvvmkotlin.newapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmkotlin.R
import com.example.mvvmkotlin.newapp.adapters.NewsAdapter
import com.example.mvvmkotlin.newapp.ui.activity.MainActivity
import com.example.mvvmkotlin.newapp.ui.activity.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel

    //nav host
    val args: ArticleFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        fab.setOnClickListener {
            viewModel.insertArticle(article)
            Toast.makeText(activity, "Article saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

}