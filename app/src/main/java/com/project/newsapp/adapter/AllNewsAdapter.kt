package com.project.newsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.newsapp.R
import com.project.newsapp.contract.News
import kotlinx.android.synthetic.main.it_all_news.view.*

class AllNewsAdapter(var context:Context, var dataSource:MutableList<News>):RecyclerView.Adapter<AllNewsAdapter.AllNewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.it_all_news,parent,false)
        return AllNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllNewsViewHolder, position: Int) {
        val news = dataSource[position]
        holder.headline.text = news.headline
        holder.description.text = news.description
        holder.userName.text = news.newsBy
        holder.dateTime.text = news.dateTime
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    class AllNewsViewHolder(view:View):RecyclerView.ViewHolder(view){
        var headline = view.headline
        var userName = view.userName
        var dateTime = view.dateTime
        var description = view.description
    }
}