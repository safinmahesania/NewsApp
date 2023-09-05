package com.project.newsapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.newsapp.adapter.AllNewsAdapter
import com.project.newsapp.contract.News
import com.project.newsapp.utils.DataProvider
import kotlinx.android.synthetic.main.activity_view_all_news.*

class ViewAllNewsActivity : AppCompatActivity() {

    lateinit var adapter:AllNewsAdapter
    lateinit var dataSource:MutableList<News>
    private lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_news)

        title = "All News"

        context = this
        dataSource= DataProvider.response.allNews
        adapter = AllNewsAdapter(context, dataSource)
        rvAllNews.adapter = adapter
    }
}