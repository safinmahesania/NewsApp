package com.project.newsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.newsapp.adapter.MyNewsAdapter
import com.project.newsapp.contract.News
import com.project.newsapp.utils.Constant
import com.project.newsapp.utils.DataProvider
import kotlinx.android.synthetic.main.activity_view_my_news.*

class ViewMyNewsActivity : AppCompatActivity() {

    lateinit var adapter: MyNewsAdapter
    lateinit var dataSource:MutableList<News>
    private lateinit var context: Context
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_news)

        title = "My News"

        context = this
        activity = this
        dataSource= DataProvider.response.myNews
        if(dataSource.size>0){
            adapter = MyNewsAdapter(activity, context, dataSource)
            rvMyNews.visibility = View.VISIBLE
            noNews.visibility = View.INVISIBLE
            rvMyNews.adapter = adapter
        }else{
            noNews.visibility = View.VISIBLE
            rvMyNews.visibility = View.INVISIBLE
        }

        btnAdd.setOnClickListener {
            Intent(this, AddOrUpdateNewsActivity::class.java).apply {
                putExtra(Constant.KEY_REASON,1)
                startActivity(this)
            }
        }
    }
}