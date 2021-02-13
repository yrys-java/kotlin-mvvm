package com.example.mvvmkotlin.newapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmkotlin.R
import com.example.mvvmkotlin.newapp.adapters.NewsAdapter
import com.example.mvvmkotlin.newapp.ui.activity.MainActivity
import com.example.mvvmkotlin.newapp.ui.activity.NewsViewModel
import com.example.mvvmkotlin.newapp.util.ApiResult
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        //поиск //так как мы не можем запустить корутины где попало
        var job: Job? = null
        etSearch.addTextChangedListener {
            job?.cancel()
            //нужно ссоздать job и лаунчануть
            job = MainScope().launch {
                delay(500L)
                it?.let {
                   if (it.toString().isNotEmpty()) {
                       // вызов корутинной функции
                       viewModel.searchNews(it.toString())
                   }
                }
            }
        }

        //наблюдатель Live Data
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { responce ->
            when (responce) {
                is ApiResult.Success -> {
                    hideProgressBar()
                    responce.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                    }
                }

                is ApiResult.Error -> {
                    hideProgressBar()
                    responce.message?.let { message ->
                        Toast.makeText(activity, "An error: $message", Toast.LENGTH_LONG).show()
                    }
                }

                is ApiResult.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.GONE
    }
    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}